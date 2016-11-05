package pl.put.splitit.domain.summary;

import pl.put.splitit.domain.User;

/**
 * Created by Krystek on 2016-11-04.
 */
public interface TransactionSummary {


    User getUser();

    /**
     * Returns total value of transactions on both sides
     *
     * @return <li>negative value - if User owns somebody money</li>
     * <li>positive value - if User received more money then he should</li>
     */
    Double getTotal();

    /**
     * Returns total value of transactions on debtor side
     *
     * @return always non-negative value
     */
    Double getTotalAsDebitor();

    /**
     * Returns total value of transactions on creditor side
     *
     * @return always non-negative value
     */
    Double getTotalAsCreditor();

    /**
     * Calculates values of <em>total, totalAsCreditor, totalAsDebitor</em>
     */
    void calculate();
}
