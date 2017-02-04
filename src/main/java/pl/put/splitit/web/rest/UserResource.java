package pl.put.splitit.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import pl.put.splitit.config.Constants;
import pl.put.splitit.domain.Transaction;
import pl.put.splitit.domain.User;
import pl.put.splitit.domain.UserGroup;
import pl.put.splitit.domain.summary.GroupSummary;
import pl.put.splitit.domain.summary.OverallSummary;
import pl.put.splitit.domain.summary.UserSummary;
import pl.put.splitit.repository.UserRepository;
import pl.put.splitit.security.AuthoritiesConstants;
import pl.put.splitit.service.MailService;
import pl.put.splitit.service.TransactionService;
import pl.put.splitit.service.UserGroupService;
import pl.put.splitit.service.UserService;
import pl.put.splitit.web.rest.util.HeaderUtil;
import pl.put.splitit.web.rest.util.PaginationUtil;
import pl.put.splitit.web.rest.vm.ManagedUserVM;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST controller for managing users.
 * <p>
 * <p>This class accesses the User entity, and needs to fetch its collection of authorities.</p>
 * <p>
 * For a normal use-case, it would be better to have an eager relationship between User and Authority,
 * and send everything to the client side: there would be no View Model and DTO, a lot less code, and an outer-join
 * which would be good for performance.
 * </p>
 * <p>
 * We use a View Model and a DTO for 3 reasons:
 * <ul>
 * <li>We want to keep a lazy association between the user and the authorities, because people will
 * quite often do relationships with the user, and we don't want them to get the authorities all
 * the time for nothing (for performance reasons). This is the #1 goal: we should not impact our users'
 * application because of this use-case.</li>
 * <li> Not having an outer join causes n+1 requests to the database. This is not a real issue as
 * we have by default a second-level cache. This means on the first HTTP call we do the n+1 requests,
 * but then all authorities come from the cache, so in fact it's much better than doing an outer join
 * (which will get lots of data from the database, for each HTTP call).</li>
 * <li> As this manages users, for security reasons, we'd rather have a DTO layer.</li>
 * </ul>
 * <p>Another option would be to have a specific JPA entity graph to handle this case.</p>
 */
@RestController
@RequestMapping("/api")
public class UserResource {

    private final Logger log = LoggerFactory.getLogger(UserResource.class);

    @Inject
    private UserRepository userRepository;

    @Inject
    private MailService mailService;

    @Inject
    private UserService userService;

    @Inject
    private TransactionService transactionService;

    @Inject
    private UserGroupService userGroupService;


    /**
     * POST  /users  : Creates a new user.
     * <p>
     * Creates a new user if the login and email are not already used, and sends an
     * mail with an activation link.
     * The user needs to be activated on creation.
     * </p>
     *
     * @param managedUserVM the user to create
     * @param request       the HTTP request
     * @return the ResponseEntity with status 201 (Created) and with body the new user, or with status 400 (Bad Request) if the login or email is already in use
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/users")
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<?> createUser(@RequestBody ManagedUserVM managedUserVM, HttpServletRequest request) throws URISyntaxException {
        log.debug("REST request to save User : {}", managedUserVM);

        //Lowercase the user login before comparing with database
        if (userRepository.findOneByLogin(managedUserVM.getLogin().toLowerCase()).isPresent()) {
            return ResponseEntity.badRequest()
                .headers(HeaderUtil.createFailureAlert("userManagement", "userexists", "Login already in use"))
                .body(null);
        } else if (userRepository.findOneByEmail(managedUserVM.getEmail()).isPresent()) {
            return ResponseEntity.badRequest()
                .headers(HeaderUtil.createFailureAlert("userManagement", "emailexists", "Email already in use"))
                .body(null);
        } else {
            User newUser = userService.createUser(managedUserVM);
            String baseUrl = request.getScheme() + // "http"
                "://" +                                // "://"
                request.getServerName() +              // "myhost"
                ":" +                                  // ":"
                request.getServerPort() +              // "80"
                request.getContextPath();              // "/myContextPath" or "" if deployed in root context
            mailService.sendCreationEmail(newUser, baseUrl);
            return ResponseEntity.created(new URI("/api/users/" + newUser.getLogin()))
                .headers(HeaderUtil.createAlert("userManagement.created", newUser.getLogin()))
                .body(newUser);
        }
    }

    /**
     * PUT  /users : Updates an existing User.
     *
     * @param managedUserVM the user to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated user,
     * or with status 400 (Bad Request) if the login or email is already in use,
     * or with status 500 (Internal Server Error) if the user couldn't be updated
     */
    @PutMapping("/users")
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<ManagedUserVM> updateUser(@RequestBody ManagedUserVM managedUserVM) {
        log.debug("REST request to update User : {}", managedUserVM);
        Optional<User> existingUser = userRepository.findOneByEmail(managedUserVM.getEmail());
        if (existingUser.isPresent() && (!existingUser.get().getId().equals(managedUserVM.getId()))) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("userManagement", "emailexists", "E-mail already in use")).body(null);
        }
        existingUser = userRepository.findOneByLogin(managedUserVM.getLogin().toLowerCase());
        if (existingUser.isPresent() && (!existingUser.get().getId().equals(managedUserVM.getId()))) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("userManagement", "userexists", "Login already in use")).body(null);
        }
        userService.updateUser(managedUserVM.getId(), managedUserVM.getLogin(), managedUserVM.getFirstName(),
            managedUserVM.getLastName(), managedUserVM.getEmail(), managedUserVM.isActivated(),
            managedUserVM.getLangKey(), managedUserVM.getAuthorities());

        return ResponseEntity.ok()
            .headers(HeaderUtil.createAlert("userManagement.updated", managedUserVM.getLogin()))
            .body(new ManagedUserVM(userService.getUserWithAuthorities(managedUserVM.getId())));
    }

    /**
     * GET  /users : get all users.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and with body all users
     * @throws URISyntaxException if the pagination headers couldn't be generated
     */
    @GetMapping("/users")
    @Timed
    public ResponseEntity<List<ManagedUserVM>> getAllUsers(Pageable pageable)
        throws URISyntaxException {
        Page<User> page = userRepository.findAllWithAuthorities(pageable);
        List<ManagedUserVM> managedUserVMs = page.getContent().stream()
            .map(ManagedUserVM::new)
            .collect(Collectors.toList());
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/users");
        return new ResponseEntity<>(managedUserVMs, headers, HttpStatus.OK);
    }

    /**
     * GET  /users/:login : get the "login" user.
     *
     * @param login the login of the user to find
     * @return the ResponseEntity with status 200 (OK) and with body the "login" user, or with status 404 (Not Found)
     */
    @GetMapping("/users/{login:" + Constants.LOGIN_REGEX + "}")
    @Timed
    public ResponseEntity<ManagedUserVM> getUser(@PathVariable String login) {
        log.debug("REST request to get User : {}", login);
        return userService.getUserWithAuthoritiesByLogin(login)
            .map(ManagedUserVM::new)
            .map(managedUserVM -> new ResponseEntity<>(managedUserVM, HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }


    @GetMapping("/users/{login:" + Constants.LOGIN_REGEX + "}/summary")
    @Timed
    public ResponseEntity<OverallSummary> getSummary(@PathVariable String login) {
        log.debug("REST request to get summary for user : {}", login);
        Optional<User> user = userService.getUserWithAuthoritiesByLogin(login);
        if (!user.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(transactionService.getSummary(user.get()), HttpStatus.OK);
    }

    @GetMapping("/users/{login:" + Constants.LOGIN_REGEX + "}/summary/{login2:" + Constants.LOGIN_REGEX + "}")
    @Timed
    public ResponseEntity<UserSummary> getUserSummary(@PathVariable String login, @PathVariable String login2) {
        log.debug("REST request to get summary for users : {} and {}", login, login2);
        Optional<User> user = userService.getUserWithAuthoritiesByLogin(login);
        Optional<User> user2 = userService.getUserWithAuthoritiesByLogin(login2);
        if (!user.isPresent() || !user2.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(transactionService.getSummary(user.get(), user2.get()), HttpStatus.OK);
    }

    @GetMapping("/users/{login:" + Constants.LOGIN_REGEX + "}/groups/{group}/summary")
    @Timed
    public ResponseEntity<GroupSummary> getSummaryInGroup(@PathVariable String login, @PathVariable Long group) {
        log.debug("REST request to get summary for user {} in group {}", login, group);
        Optional<User> user = userService.getUserWithAuthoritiesByLogin(login);
        UserGroup userGroup = userGroupService.findOne(group);
        if (!user.isPresent() || userGroup == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(transactionService.getSummary(user.get(), userGroup), HttpStatus.OK);
    }


    @GetMapping("/users/{login:" + Constants.LOGIN_REGEX + "}/groups/{group}/summary/{login2:" + Constants.LOGIN_REGEX + "}")
    @Timed
    public ResponseEntity<UserSummary> getUserSummaryInGroup(@PathVariable String login, @PathVariable String login2, @PathVariable Long group) {
        log.debug("REST request to get summary for users : {} and {}", login, login2);
        Optional<User> user = userService.getUserWithAuthoritiesByLogin(login);
        Optional<User> user2 = userService.getUserWithAuthoritiesByLogin(login2);
        UserGroup userGroup = userGroupService.findOne(group);
        if (!user.isPresent() || !user2.isPresent() || userGroup == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(transactionService.getSummary(user.get(), user2.get(), userGroup), HttpStatus.OK);
    }


    @GetMapping("/users/{login:" + Constants.LOGIN_REGEX + "}/groups")
    @Timed
    public ResponseEntity<List<UserGroup>> getUserGroups(@PathVariable String login, Pageable pageable) throws URISyntaxException {
        log.debug("REST request to get groups of user: {}", login);
        Optional<User> user = userService.getUserWithAuthoritiesByLogin(login);

        // If there is no user with given name
        if (!user.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Page<UserGroup> groups = userGroupService.findAllGroupsOfUser(login, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(groups, String.format("/api/users/%s/groups", login));
        return new ResponseEntity<>(groups.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/users/{login:" + Constants.LOGIN_REGEX + "}/transactions")
    @Timed
    public ResponseEntity<List<Transaction>> getUserTransactions(
        @PathVariable String login,
        @RequestParam(value = "type", required = false, defaultValue = "both") String transactionType,
        Pageable pageable) throws URISyntaxException {

        log.debug("REST request to get transactions of user: {}", login);
        Optional<User> user = userService.getUserWithAuthoritiesByLogin(login);

        // If there is no user with given name
        if (!user.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Page<Transaction> transactions = transactionService.findAllByUserAndType(login, TransactionType.getType(transactionType), pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(transactions, String.format("/api/users/%s/transactions", login));
        return new ResponseEntity<>(transactions.getContent(), headers, HttpStatus.OK);
    }


    /**
     * DELETE /users/:login : delete the "login" User.
     *
     * @param login the login of the user to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/users/{login:" + Constants.LOGIN_REGEX + "}")
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<Void> deleteUser(@PathVariable String login) {
        log.debug("REST request to delete User: {}", login);
        userService.deleteUser(login);
        return ResponseEntity.ok().headers(HeaderUtil.createAlert("userManagement.deleted", login)).build();
    }
}
