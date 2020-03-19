package com.truthbean.debbie.jdbc.repository;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.0.2
 * Created on 2020-03-04 20:43
 */
public class MoreRowException extends RuntimeException {
    public MoreRowException() {
    }

    public MoreRowException(String message) {
        super(message);
    }

    public MoreRowException(String message, Throwable cause) {
        super(message, cause);
    }

    public MoreRowException(Throwable cause) {
        super(cause);
    }

    public MoreRowException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
