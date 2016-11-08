package pl.put.splitit.domain.summary;

import lombok.Getter;
import org.codehaus.jackson.annotate.JsonIgnore;
import pl.put.splitit.domain.Transaction;
import pl.put.splitit.domain.User;

import java.util.List;

/**
 * Created by Krystek on 2016-11-04.
 * <p>
 * Podsumowanie transakcji z danym użytkownikem
 */
public class UserSummary extends Summary {
    @Getter
    private User secondUser;

    List<Transaction> transactions;

    UserSummary(User user, User secondUser, List<Transaction> transactions) {
        this.user = user;
        this.secondUser = secondUser;
        this.transactions = transactions;
    }

    @Override
    public void calculate() {
        totalAsCreditor = transactions.parallelStream().filter(t -> t.getCreditor().equals(user)).mapToDouble(p -> p.getValue()).sum();
        totalAsDebitor = transactions.parallelStream().filter(t -> t.getDebitor().equals(user)).mapToDouble(p -> p.getValue()).sum();
    }
}
