package com.truthbean.debbie.mvc.router;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.0.2
 * Created on 2020-03-21 21:17
 */
public class RouterException extends RuntimeException {
    public RouterException() {
    }

    public RouterException(String message) {
        super(message);
    }

    public RouterException(String message, Throwable cause) {
        super(message, cause);
    }

    public RouterException(Throwable cause) {
        super(cause);
    }

    public RouterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
