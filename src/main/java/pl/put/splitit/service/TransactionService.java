package pl.put.splitit.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.put.splitit.domain.Transaction;
import pl.put.splitit.repository.TransactionRepository;
import pl.put.splitit.web.rest.TransactionType;

import javax.inject.Inject;

/**
 * Service Implementation for managing Transaction.
 */
@Service
@Transactional
public class TransactionService {

    private final Logger log = LoggerFactory.getLogger(TransactionService.class);

    @Inject
    private TransactionRepository transactionRepository;

    /**
     * Save a transaction.
     *
     * @param transaction the entity to save
     * @return the persisted entity
     */
    public Transaction save(Transaction transaction) {
        log.debug("Request to save Transaction : {}", transaction);
        Transaction result = transactionRepository.save(transaction);
        return result;
    }

    /**
     * Get all the transactions.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Transaction> findAll(Pageable pageable) {
        log.debug("Request to get all Transactions");
        Page<Transaction> result = transactionRepository.findAll(pageable);
        return result;
    }

    @Transactional(readOnly = true)
    public Page<Transaction> findAllByUserAndType(String login, TransactionType type, Pageable pageable) {
        log.debug("Request to get all Transactions of user: " + login + " and type: " + type);

        switch (type) {
            case DEBIT:
                return transactionRepository.findByDebtorAndUser(login, pageable);
            case CREDIT:
                return transactionRepository.findByCreditorAndUser(login, pageable);
            default:
                return transactionRepository.findAllByUser(login, pageable);
        }
    }


    /**
     * Get one transaction by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public Transaction findOne(Long id) {
        log.debug("Request to get Transaction : {}", id);
        Transaction transaction = transactionRepository.findOne(id);
        return transaction;
    }

    /**
     * Delete the  transaction by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Transaction : {}", id);
        transactionRepository.delete(id);
    }
}
