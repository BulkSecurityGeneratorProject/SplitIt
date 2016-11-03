package pl.put.splitit.web.rest;

/**
 * Created by Krystek on 2016-11-03.
 */
public enum TransactionType {
    // Determines on which transaction side user occurs
    DEBIT, // debtor's side
    CREDIT, // creditor's side
    BOTH;

    static TransactionType getType(String value) {
        if (value == null || value.trim().isEmpty()) {
            return BOTH;
        }
        try {
            return valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return BOTH;
        }
    }
}
