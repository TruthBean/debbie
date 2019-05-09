package com.truthbean.debbie.core.bean;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class OneMoreBeanRegisteredException extends RuntimeException {

    public OneMoreBeanRegisteredException() {
    }

    public OneMoreBeanRegisteredException(String message) {
        super(message);
    }

    public OneMoreBeanRegisteredException(String message, Throwable cause) {
        super(message, cause);
    }

    public OneMoreBeanRegisteredException(Throwable cause) {
        super(cause);
    }

    public OneMoreBeanRegisteredException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
