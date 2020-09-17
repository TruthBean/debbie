package com.truthbean.debbie.httpclient.model;

public class ListResponse<T> {
    private Integer request;

    private String requestId;

    private T data;

    public Integer getRequest() {
        return request;
    }

    public void setRequest(Integer request) {
        this.request = request;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "{" +
                "\"request\":" + request +
                ",\"requestId\":\"" + requestId + '\"' +
                ",\"data\":" + data +
                '}';
    }
}