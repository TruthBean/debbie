package com.truthbean.debbie.mvc.exception;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class DispatcherMappingFormatException extends RuntimeException {
    public DispatcherMappingFormatException() {
    }

    public DispatcherMappingFormatException(String message) {
        super(message);
    }

    public DispatcherMappingFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public DispatcherMappingFormatException(Throwable cause) {
        super(cause);
    }

    public DispatcherMappingFormatException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
