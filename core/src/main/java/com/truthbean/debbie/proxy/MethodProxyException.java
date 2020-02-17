package com.truthbean.debbie.proxy;

public class MethodProxyException extends RuntimeException {
    public MethodProxyException() {
    }

    public MethodProxyException(String message) {
        super(message);
    }

    public MethodProxyException(String message, Throwable cause) {
        super(message, cause);
    }

    public MethodProxyException(Throwable cause) {
        super(cause);
    }

    public MethodProxyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
