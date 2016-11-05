package pl.put.splitit.domain.summary;

import lombok.Getter;
import pl.put.splitit.domain.User;
import pl.put.splitit.domain.UserGroup;

import java.util.List;

/**
 * Created by Krystek on 2016-11-04.
 */
public class GroupSummary extends OverallSummary<UserSummary> {

    @Getter
    private UserGroup group;

    GroupSummary(User user, UserGroup group, List<UserSummary> summaries) {
        super(user, summaries);
        this.group = group;
    }
}
