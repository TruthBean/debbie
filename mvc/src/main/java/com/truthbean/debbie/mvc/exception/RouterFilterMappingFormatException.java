package com.truthbean.debbie.mvc.exception;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class RouterFilterMappingFormatException extends RuntimeException {
    public RouterFilterMappingFormatException() {
    }

    public RouterFilterMappingFormatException(String message) {
        super(message);
    }

    public RouterFilterMappingFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public RouterFilterMappingFormatException(Throwable cause) {
        super(cause);
    }

    public RouterFilterMappingFormatException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
