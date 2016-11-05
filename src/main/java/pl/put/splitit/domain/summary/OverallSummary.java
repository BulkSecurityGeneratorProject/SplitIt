package pl.put.splitit.domain.summary;

import lombok.Getter;
import pl.put.splitit.domain.User;

import java.util.List;

/**
 * Created by Krystek on 2016-11-04.
 */
public class OverallSummary<T extends TransactionSummary> extends Summary {

    @Getter
    private List<T> summaries;

    @Getter
    private Double total;

    OverallSummary(User user,List<T> summaries) {
        this.summaries = summaries;
        this.user = user;
    }

    @Override
    public void calculate() {
        total = 0.0;
        totalAsDebitor = 0.0;
        totalAsCreditor = 0.0;

        summaries.parallelStream().forEach(summary -> {
            total += summary.getTotal();
            totalAsDebitor += summary.getTotalAsDebitor();
            totalAsCreditor += summary.getTotalAsCreditor();
        });
    }
}
