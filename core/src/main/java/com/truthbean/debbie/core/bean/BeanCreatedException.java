package com.truthbean.debbie.core.bean;

public class BeanCreatedException extends RuntimeException {
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
