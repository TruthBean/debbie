package com.truthbean.debbie.lang;

/**
 * @author TruthBean
 * @since 0.0.2
 * Created on 2019-11-25 19:09.
 */
public class NoEnumException extends RuntimeException {
    public NoEnumException() {
    }

    public NoEnumException(String message) {
        super(message);
    }

    public NoEnumException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoEnumException(Throwable cause) {
        super(cause);
    }

    public NoEnumException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
