package com.truthbean.debbie.bean;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.5
 */
public class BeanDestructException extends RuntimeException {
    public BeanDestructException() {
        super();
    }

    public BeanDestructException(String message) {
        super(message);
    }

    public BeanDestructException(String message, Throwable cause) {
        super(message, cause);
    }

    public BeanDestructException(Throwable cause) {
        super(cause);
    }

    protected BeanDestructException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
