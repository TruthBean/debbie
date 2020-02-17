package com.truthbean.debbie.jdbc.transaction;

/**
 * @author truthbean
 * @since 0.0.1
 */
public class MethodNoJdbcTransactionalException extends RuntimeException {

    public MethodNoJdbcTransactionalException() {
    }

    public MethodNoJdbcTransactionalException(String message) {
        super(message);
    }

    public MethodNoJdbcTransactionalException(String message, Throwable cause) {
        super(message, cause);
    }

    public MethodNoJdbcTransactionalException(Throwable cause) {
        super(cause);
    }

    public MethodNoJdbcTransactionalException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
