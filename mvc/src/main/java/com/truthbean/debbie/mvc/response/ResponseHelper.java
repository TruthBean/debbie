package com.truthbean.debbie.mvc.response;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class ResponseHelper {

    public static ResponseEntity success(String message) {
        var entity = new ResponseEntity();
        entity.setStatus(0);
        entity.setMessage(message);
        return entity;
    }

    public static <D> ResponseEntity success(String message, D data) {
        var entity = new ResponseEntity<D>();
        entity.setStatus(0);
        entity.setMessage(message);
        entity.setData(data);
        return entity;
    }

    public static ResponseEntity<Object> error(String message) {
        var response = new ResponseEntity<>();
        response.setStatus(500);
        response.setMessage(message);
        return response;
    }

    public static ResponseEntity paramsError() {
        var response = new ResponseEntity();
        response.setStatus(400);
        response.setMessage("params error");
        return response;
    }

    public static ResponseEntity<Object> resourcesNotFound() {
        var response = new ResponseEntity<>();
        response.setStatus(404);
        response.setMessage("resources not found");
        return response;
    }

    public static <D> ResponseEntity response(int status, String message, D data) {
        var entity = new ResponseEntity<D>();
        entity.setStatus(status);
        entity.setMessage(message);
        entity.setData(data);
        return entity;
    }
}
