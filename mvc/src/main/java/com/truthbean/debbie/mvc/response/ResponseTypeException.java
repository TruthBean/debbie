package com.truthbean.debbie.mvc.response;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.0.2
 * Created on 2020-03-21 19:50
 */
public class ResponseTypeException extends RuntimeException {
    public ResponseTypeException() {
    }

    public ResponseTypeException(String message) {
        super(message);
    }

    public ResponseTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResponseTypeException(Throwable cause) {
        super(cause);
    }

    public ResponseTypeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
