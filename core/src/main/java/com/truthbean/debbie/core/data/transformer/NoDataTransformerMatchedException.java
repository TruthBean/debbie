package com.truthbean.debbie.core.data.transformer;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class NoDataTransformerMatchedException extends RuntimeException {
    private static final long serialVersionUID = 617541828674906950L;

    public NoDataTransformerMatchedException() {
    }

    public NoDataTransformerMatchedException(String message) {
        super(message);
    }

    public NoDataTransformerMatchedException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoDataTransformerMatchedException(Throwable cause) {
        super(cause);
    }

    public NoDataTransformerMatchedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
