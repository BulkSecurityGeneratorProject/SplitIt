package pl.put.splitit.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.put.splitit.domain.UserGroup;
import pl.put.splitit.repository.UserGroupRepository;

import javax.inject.Inject;

/**
 * Service Implementation for managing UserGroup.
 */
@Service
@Transactional
public class UserGroupService {

    private final Logger log = LoggerFactory.getLogger(UserGroupService.class);

    @Inject
    private UserGroupRepository userGroupRepository;

    /**
     * Save a userGroup.
     *
     * @param userGroup the entity to save
     * @return the persisted entity
     */
    public UserGroup save(UserGroup userGroup) {
        log.debug("Request to save UserGroup : {}", userGroup);
        return userGroupRepository.save(userGroup);
    }

    /**
     * Get all the userGroups.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<UserGroup> findAll(Pageable pageable) {
        log.debug("Request to get all groups");
        return userGroupRepository.findAll(pageable);
    }


    /**
     * Get all the userGroups.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<UserGroup> findAllPublic(Pageable pageable) {
        log.debug("Request to get all public groups");
        return userGroupRepository.findAllPublic(pageable);
    }


    @Transactional(readOnly = true)
    public Page<UserGroup> findAllGroupsOfUser(String login, Pageable pageable) {
        log.debug("Request to get all groups of user: " + login);
        return userGroupRepository.findAllGroupsOfUser(login, pageable);
    }

    /**
     * Get one userGroup by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public UserGroup findOne(Long id) {
        log.debug("Request to get UserGroup : {}", id);
        return userGroupRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the  userGroup by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete UserGroup : {}", id);
        userGroupRepository.delete(id);
    }
}
