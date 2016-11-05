package pl.put.splitit.domain.summary;

import lombok.Getter;
import pl.put.splitit.domain.User;

/**
 * Created by Krystek on 2016-11-05.
 */
public abstract class Summary implements TransactionSummary {

    protected Double totalAsDebitor, totalAsCreditor;

    @Getter
    protected User user;


    @Override
    public Double getTotal() {
        return totalAsDebitor - totalAsCreditor;
    }

    @Override
    public Double getTotalAsDebitor() {
        return Math.abs(totalAsDebitor);
    }

    @Override
    public Double getTotalAsCreditor() {
        return Math.abs(totalAsCreditor);
    }
}
