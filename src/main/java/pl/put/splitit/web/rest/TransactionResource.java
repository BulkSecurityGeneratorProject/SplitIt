package pl.put.splitit.web.rest;

import com.codahale.metrics.annotation.Timed;
import pl.put.splitit.domain.Transaction;
import pl.put.splitit.domain.User;
import pl.put.splitit.service.TransactionService;
import pl.put.splitit.service.UserService;
import pl.put.splitit.web.rest.util.HeaderUtil;
import pl.put.splitit.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Transaction.
 */
@RestController
@RequestMapping("/api")
public class TransactionResource {

    private final Logger log = LoggerFactory.getLogger(TransactionResource.class);

    @Inject
    private TransactionService transactionService;
    @Inject
    private UserService userService;

    /**
     * POST  /transactions : Create a new transaction.
     *
     * @param transaction the transaction to create
     * @return the ResponseEntity with status 201 (Created) and with body the new transaction, or with status 400 (Bad Request) if the transaction has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/transactions")
    @Timed
    public ResponseEntity<Transaction> createTransaction(@Valid @RequestBody Transaction transaction) throws URISyntaxException {
        log.debug("REST request to save Transaction : {}", transaction);
        if (transaction.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("transaction", "idexists", "A new transaction cannot already have an ID")).body(null);
        }
        Transaction result = transactionService.save(transaction);
        return ResponseEntity.created(new URI("/api/transactions/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("transaction", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /transactions : Updates an existing transaction.
     *
     * @param transaction the transaction to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated transaction,
     * or with status 400 (Bad Request) if the transaction is not valid,
     * or with status 500 (Internal Server Error) if the transaction couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/transactions")
    @Timed
    public ResponseEntity<Transaction> updateTransaction(@Valid @RequestBody Transaction transaction) throws URISyntaxException {
        log.debug("REST request to update Transaction : {}", transaction);
        if (transaction.getId() == null) {
            return createTransaction(transaction);
        }
        Transaction result = transactionService.save(transaction);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("transaction", transaction.getId().toString()))
            .body(result);
    }

    /**
     * GET  /transactions : get all the transactions.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of transactions in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/transactions")
    @Timed
    public ResponseEntity<List<Transaction>> getAllTransactions(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Transactions");
        Page<Transaction> page = transactionService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/transactions");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /transactions/:id : get the "id" transaction.
     *
     * @param id the id of the transaction to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the transaction, or with status 404 (Not Found)
     */
    @GetMapping("/transactions/{id}")
    @Timed
    public ResponseEntity<Transaction> getTransaction(@PathVariable Long id) {
        log.debug("REST request to get Transaction : {}", id);
        Transaction transaction = transactionService.findOne(id);
        return Optional.ofNullable(transaction)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /transactions/:id : delete the "id" transaction.
     *
     * @param id the id of the transaction to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/transactions/{id}")
    @Timed
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        log.debug("REST request to delete Transaction : {}", id);
        transactionService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("transaction", id.toString())).build();
    }

}
