package com.truthbean.debbie.mvc.response.view;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-02-18 22:35
 */
public class NoTemplateHandlerProviderException extends RuntimeException {
    public NoTemplateHandlerProviderException() {
    }

    public NoTemplateHandlerProviderException(String message) {
        super(message);
    }

    public NoTemplateHandlerProviderException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoTemplateHandlerProviderException(Throwable cause) {
        super(cause);
    }

    public NoTemplateHandlerProviderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
