package pl.put.splitit.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.put.splitit.domain.Transaction;

import java.util.List;

/**
 * Spring Data JPA repository for the Transaction entity.
 */
@SuppressWarnings("unused")
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("select transaction from Transaction transaction where transaction.debitor.login = ?#{principal.username}")
    List<Transaction> findByDebitorIsCurrentUser();

    @Query("select transaction from Transaction transaction where transaction.creditor.login = ?#{principal.username}")
    List<Transaction> findByCreditorIsCurrentUser();

    @Query("select transaction from Transaction transaction where transaction.creditor.login = :login or transaction.debitor.login = :login")
    Page<Transaction> findAllByUser(@Param("login") String login, Pageable pageable);

    @Query("select transaction from Transaction transaction where transaction.creditor.login = :login or transaction.debitor.login = :login")
    List<Transaction> findAllByUser(@Param("login") String login);

    @Query("select transaction from Transaction transaction where (transaction.creditor.login = :login and transaction.debitor.login = :login2) or (transaction.debitor.login = :login and transaction.creditor.login = :login2)")
    List<Transaction> findAllByUsers(@Param("login") String login, @Param("login2") String login2);

    @Query("select transaction from Transaction transaction where (transaction.creditor.login = :login and transaction.debitor.login = :login2) or (transaction.debitor.login = :login and transaction.creditor.login = :login2) and transaction.userGroup.id = :id")
    List<Transaction> findAllByUsersAndGroup(@Param("login") String login, @Param("login2") String login2,  @Param("id") Long id);

    @Query("select transaction from Transaction transaction where transaction.creditor.login = :login or transaction.debitor.login = :login and transaction.userGroup.id = :id")
    List<Transaction> findAllByUserAndGroup(@Param("login") String login,  @Param("id") Long id);



    @Query("select transaction from Transaction transaction where transaction.debitor.login = :login")
    Page<Transaction> findByDebtorAndUser(@Param("login") String login, Pageable pageable);

    @Query("select transaction from Transaction transaction where transaction.creditor.login = :login")
    Page<Transaction> findByCreditorAndUser(@Param("login") String login, Pageable pageable);

    @Query("select transaction from Transaction transaction where transaction.userGroup.id = :id")
    Page<Transaction> findAllByGroup(@Param("id") Long id, Pageable pageable);
}
