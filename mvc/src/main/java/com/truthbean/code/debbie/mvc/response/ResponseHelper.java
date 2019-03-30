package com.truthbean.code.debbie.mvc.response;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public final class ResponseHelper {

    private ResponseHelper() {
    }

    public static <T> ResponseEntity<T> success(String message) {
        var entity = new ResponseEntity<T>();
        entity.setStatus(0);
        entity.setMessage(message);
        return entity;
    }

    public static ResponseEntity error(String message) {
        var response = new ResponseEntity();
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

    public static <T> ResponseEntity<T> resourcesNotFound() {
        var response = new ResponseEntity<T>();
        response.setStatus(404);
        response.setMessage("resources not found");
        return response;
    }
}
