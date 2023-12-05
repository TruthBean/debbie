package com.truthbean.debbie.environment;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.5
 */
public class NoProfiledEnvironmentException extends RuntimeException {
    public NoProfiledEnvironmentException() {
    }

    public NoProfiledEnvironmentException(String message) {
        super(message);
    }

    public NoProfiledEnvironmentException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoProfiledEnvironmentException(Throwable cause) {
        super(cause);
    }

    public NoProfiledEnvironmentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
