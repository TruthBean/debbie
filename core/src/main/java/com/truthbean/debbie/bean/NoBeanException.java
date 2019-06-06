package com.truthbean.debbie.bean;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class NoBeanException extends RuntimeException {

    public NoBeanException() {
    }

    public NoBeanException(String message) {
        super(message);
    }

    public NoBeanException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoBeanException(Throwable cause) {
        super(cause);
    }

    public NoBeanException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
