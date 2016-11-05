package pl.put.splitit.domain.summary;

import pl.put.splitit.domain.User;

/**
 * Created by Krystek on 2016-11-04.
 */
public interface TransactionSummary {

    User getUser();

    Double getTotal();

    Double getTotalAsDebitor();

    Double getTotalAsCreditor();

    void calculate();
}
