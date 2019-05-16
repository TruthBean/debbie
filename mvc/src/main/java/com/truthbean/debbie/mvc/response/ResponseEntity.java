package com.truthbean.debbie.mvc.response;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class ResponseEntity<D> {
    private int status;
    private String message;
    private D data;

    public ResponseEntity() {
    }

    public ResponseEntity(int status, String message, D data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(D data) {
        this.data = data;
    }
}
