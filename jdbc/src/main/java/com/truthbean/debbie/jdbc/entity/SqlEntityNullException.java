package com.truthbean.debbie.jdbc.entity;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.0.2
 * Created on 2020-03-15 11:13
 */
public class SqlEntityNullException extends RuntimeException {
    public SqlEntityNullException() {
    }

    public SqlEntityNullException(String message) {
        super(message);
    }

    public SqlEntityNullException(String message, Throwable cause) {
        super(message, cause);
    }

    public SqlEntityNullException(Throwable cause) {
        super(cause);
    }

    public SqlEntityNullException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
