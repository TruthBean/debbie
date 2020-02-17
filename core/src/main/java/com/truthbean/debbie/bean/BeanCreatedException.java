package com.truthbean.debbie.bean;

/**
 * @author truthbean
 * @since 0.0.1
 */
public class BeanCreatedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public BeanCreatedException() {
    }

    public BeanCreatedException(String message) {
        super(message);
    }

    public BeanCreatedException(String message, Throwable cause) {
        super(message, cause);
    }

    public BeanCreatedException(Throwable cause) {
        super(cause);
    }

    public BeanCreatedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
