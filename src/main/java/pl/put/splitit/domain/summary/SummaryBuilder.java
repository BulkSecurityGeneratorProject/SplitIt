package pl.put.splitit.domain.summary;

import pl.put.splitit.domain.Transaction;
import pl.put.splitit.domain.User;
import pl.put.splitit.domain.UserGroup;

import java.util.List;
import java.util.Map;

/**
 * Created by Krystek on 2016-11-04.
 */
public class SummaryBuilder {

    Map<UserGroup, Map<User, List<Transaction>>> map;
    private List<Transaction> transactions;


    public SummaryBuilder(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public UserSummary buildUserSummary(UserGroup group, User user, User secondUser) {
        UserSummary summary = new UserSummary(user, secondUser, transactions);
        return summary;
    }

}
