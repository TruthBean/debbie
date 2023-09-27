package com.truthbean.debbie.environment;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.5
 */
public class NoSuchEnvironmentException extends RuntimeException {
    public NoSuchEnvironmentException() {
        super();
    }

    public NoSuchEnvironmentException(String message) {
        super(message);
    }

    public NoSuchEnvironmentException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchEnvironmentException(Throwable cause) {
        super(cause);
    }

    protected NoSuchEnvironmentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
