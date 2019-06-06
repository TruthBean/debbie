package com.truthbean.debbie.spi;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class NoServiceProviderException extends RuntimeException {

    public NoServiceProviderException(String serviceClassName) {
        super(serviceClassName + " has no provider error. ");
    }

    public NoServiceProviderException(String serviceClassName, String message) {
        super(serviceClassName + " has no provider error; " + message);
    }

    public NoServiceProviderException(String serviceClassName, String message, Throwable cause) {
        super(serviceClassName + " has no provider error; " + message, cause);
    }

    public NoServiceProviderException(String serviceClassName, Throwable cause) {
        super(serviceClassName + " has no provider error. ", cause);
    }

    public NoServiceProviderException(String serviceClassName, String message, Throwable cause,
                                      boolean enableSuppression, boolean writableStackTrace) {
        super(serviceClassName + " has no provider error; " + message, cause, enableSuppression, writableStackTrace);
    }
}