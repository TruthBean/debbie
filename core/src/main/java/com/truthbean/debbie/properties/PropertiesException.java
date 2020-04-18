package com.truthbean.debbie.properties;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.0.2
 * Created on 2020-03-21 21:18
 */
public class PropertiesException extends RuntimeException {
    public PropertiesException() {
    }

    public PropertiesException(String message) {
        super(message);
    }

    public PropertiesException(String message, Throwable cause) {
        super(message, cause);
    }

    public PropertiesException(Throwable cause) {
        super(cause);
    }

    public PropertiesException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
