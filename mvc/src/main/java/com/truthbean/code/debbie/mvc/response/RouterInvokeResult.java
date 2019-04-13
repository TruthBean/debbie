package com.truthbean.code.debbie.mvc.response;

import com.truthbean.code.debbie.core.io.MediaType;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-02-13 21:53
 */
public class RouterInvokeResult<T> {
    private MediaType responseType;

    private T data;

    private AbstractResponseHandler<T, Object> handler;

    public MediaType getResponseType() {
        return responseType;
    }

    public void setResponseType(MediaType responseType) {
        this.responseType = responseType;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public AbstractResponseHandler<T, Object> getHandler() {
        return handler;
    }

    public void setHandler(AbstractResponseHandler<T, Object> handler) {
        this.handler = handler;
    }
}
