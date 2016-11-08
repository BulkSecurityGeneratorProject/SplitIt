package pl.put.splitit.domain.summary;

import pl.put.splitit.domain.Transaction;
import pl.put.splitit.domain.User;
import pl.put.splitit.domain.UserGroup;

import java.security.acl.Group;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Krystek on 2016-11-04.
 */
public class SummaryBuilder {

    Map<UserGroup, Map<User, List<Transaction>>> map;
    private List<Transaction> transactions;

    public SummaryBuilder(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public SummaryBuilder(Transaction... transactions) {
        this.transactions = Arrays.asList(transactions);
    }


    //<editor-fold desc="buildUserSummary">

    /**
     * Builds summary for pair of users within given group
     *
     * @param group
     * @param user
     * @param secondUser
     * @return
     */
    public UserSummary buildUserSummary(UserGroup group, User user, User secondUser) {
        // Select transactions only from given group
        List<Transaction> transactions = this.transactions.parallelStream().filter(p -> p.getUserGroup().equals(group)).collect(Collectors.toList());
        return buildUserSummary(user, secondUser, transactions);
    }

    /**
     * Builds summary for pair of users omitting groups
     *
     * @param user
     * @param secondUser
     * @return
     */
    public UserSummary buildUserSummary(User user, User secondUser) {
        return buildUserSummary(user, secondUser, transactions);
    }

    private UserSummary buildUserSummary(User user, User secondUser, List<Transaction> transactions) {
        transactions = transactions.parallelStream().filter(p ->
            (p.getCreditor().equals(user) && p.getDebitor().equals(secondUser)) ||
                (p.getCreditor().equals(secondUser) && p.getDebitor().equals(user))).collect(Collectors.toList());

        UserSummary summary = new UserSummary(user, secondUser, transactions);
        summary.calculate();
        return summary;
    }
    //</editor-fold>


    /**
     * Builds summary for given group and user
     *
     * @param user
     * @param group
     * @return
     */
    public GroupSummary buildGroupSummary(User user, UserGroup group) {
        return buildGroupSummary(user, group, transactions);
    }

    private GroupSummary buildGroupSummary(User user, UserGroup group, List<Transaction> transactions) {
        Map<User, List<Transaction>> map = new HashMap<>();
        for (Transaction t : transactions) {
            // If user isn't present in transaction or given group doesn't match transaction group
            if ((!t.getDebitor().equals(user) && !t.getCreditor().equals(user)) || !t.getUserGroup().equals(group)) {
                continue;
            }
            User u = t.getDebitor();
            if (u.equals(user)) {
                u = t.getCreditor();
            }

            if (map.containsKey(u)) {
                map.get(u).add(t);
            } else {
                map.put(u, new ArrayList<Transaction>() {{
                    add(t);
                }});
            }
        }

        List<UserSummary> userSummaries = new ArrayList<>();
        for (Map.Entry<User, List<Transaction>> entry : map.entrySet()) {
            userSummaries.add(buildUserSummary(user, entry.getKey(), entry.getValue()));
        }
        GroupSummary summary = new GroupSummary(user, group, userSummaries);
        summary.calculate();
        return summary;
    }

    public OverallSummary buildOverallSummary(User user){
        return buildOverallSummary(user, transactions);
    }


    private OverallSummary buildOverallSummary(User user, List<Transaction> transactions){
        Map<UserGroup, List<Transaction>> map = new HashMap<>();
        for (Transaction t : transactions) {
            // If user isn't present in transaction
            if ((!t.getDebitor().equals(user) && !t.getCreditor().equals(user))) {
                continue;
            }

            UserGroup group = t.getUserGroup();

            if (map.containsKey(group)) {
                map.get(group).add(t);
            } else {
                map.put(group, new ArrayList<Transaction>() {{
                    add(t);
                }});
            }
        }

        List<GroupSummary> groupSummaries = new ArrayList();
        for (Map.Entry<UserGroup, List<Transaction>> entry : map.entrySet()) {
            groupSummaries.add(buildGroupSummary(user, entry.getKey(), entry.getValue()));
        }

        OverallSummary summary = new OverallSummary(user, groupSummaries);
        summary.calculate();
        return summary;
    }
}
