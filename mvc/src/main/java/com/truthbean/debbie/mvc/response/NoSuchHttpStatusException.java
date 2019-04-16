package com.truthbean.debbie.mvc.response;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class NoSuchHttpStatusException extends RuntimeException {

    public NoSuchHttpStatusException(int status) {
        super("no such http status (" + status + ")");
    }

    public NoSuchHttpStatusException(int status, String message) {
        super("no such http status (" + status + "); " + message);
    }

    public NoSuchHttpStatusException(int status, String message, Throwable cause) {
        super("no such http status (" + status + "); " + message, cause);
    }

    public NoSuchHttpStatusException(int status, Throwable cause) {
        super("no such http status (" + status + "); ", cause);
    }

    public NoSuchHttpStatusException(int status, String message, Throwable cause, boolean enableSuppression,
                                     boolean writableStackTrace) {
        super("no such http status (" + status + "); " + message, cause, enableSuppression, writableStackTrace);
    }
}
