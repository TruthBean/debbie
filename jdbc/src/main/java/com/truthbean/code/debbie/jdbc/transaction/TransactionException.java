package com.truthbean.code.debbie.jdbc.transaction;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class TransactionException extends Exception {
    /**
     * auto generate uid
     */
    private static final long serialVersionUID = -5529320793687176184L;

    public TransactionException() {
    }

    public TransactionException(String message) {
        super(message);
    }

    public TransactionException(String message, Throwable cause) {
        super(message, cause);
    }

    public TransactionException(Throwable cause) {
        super(cause);
    }

    public TransactionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
