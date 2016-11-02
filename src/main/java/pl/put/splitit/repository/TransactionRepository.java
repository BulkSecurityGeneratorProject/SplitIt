package pl.put.splitit.repository;

import pl.put.splitit.domain.Transaction;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Transaction entity.
 */
@SuppressWarnings("unused")
public interface TransactionRepository extends JpaRepository<Transaction,Long> {

    @Query("select transaction from Transaction transaction where transaction.debitor.login = ?#{principal.username}")
    List<Transaction> findByDebitorIsCurrentUser();

    @Query("select transaction from Transaction transaction where transaction.creditor.login = ?#{principal.username}")
    List<Transaction> findByCreditorIsCurrentUser();

}
