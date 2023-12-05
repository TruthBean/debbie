package com.truthbean.debbie.environment;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.5
 */
public class NoCategoryConfigurationException extends RuntimeException {
    public NoCategoryConfigurationException() {
    }

    public NoCategoryConfigurationException(String message) {
        super(message);
    }

    public NoCategoryConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoCategoryConfigurationException(Throwable cause) {
        super(cause);
    }

    public NoCategoryConfigurationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
