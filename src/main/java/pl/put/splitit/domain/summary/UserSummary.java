package pl.put.splitit.domain.summary;

import lombok.Getter;
import pl.put.splitit.domain.Transaction;
import pl.put.splitit.domain.User;

import java.util.List;

/**
 * Created by Krystek on 2016-11-04.
 *
 * Podsumowanie transakcji z danym użytkownikem w ramch grupy
 */
public class UserSummary implements TransactionSummary {

    @Getter
    private Double totalAsDebitor, totalAsCreditor;

    @Getter
    private User user, secondUser;

    @Getter
    List<Transaction> transactions;

    public UserSummary(User user, User secondUser, List<Transaction> transactions) {
        this.user = user;
        this.secondUser = secondUser;
        this.transactions = transactions;
        calculate();
    }


    @Override
    public Double getTotal() {
        return totalAsCreditor - totalAsDebitor;
    }

    @Override
    public void calculate() {
        totalAsCreditor = transactions.parallelStream().filter(t -> t.getCreditor().equals(user)).mapToDouble(p -> p.getValue()).sum();
        totalAsDebitor = transactions.parallelStream().filter(t -> t.getCreditor().equals(user)).mapToDouble(p -> p.getValue()).sum();
    }
}
