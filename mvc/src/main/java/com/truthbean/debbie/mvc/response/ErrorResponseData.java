package com.truthbean.debbie.mvc.response;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.0.2
 * Created on 2020-03-22 21:49
 */
public class ErrorResponseData {
    private String uri;
    private Long timestamp;
    private String method;
    private Throwable exception;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }

}
