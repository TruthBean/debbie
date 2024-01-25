/**
 * Copyright (c) 2024 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mvc.response;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class ResponseHelper {

    public static <D> ResponseEntity<D> success(String message) {
        var entity = new ResponseEntity<D>();
        entity.setStatus(0);
        entity.setMessage(message);
        return entity;
    }

    public static <D> ResponseEntity<D> success(String message, D data) {
        var entity = new ResponseEntity<D>();
        entity.setStatus(0);
        entity.setMessage(message);
        entity.setData(data);
        return entity;
    }

    public static <D> ResponseEntity<D> error(String message) {
        var response = new ResponseEntity<D>();
        response.setStatus(500);
        response.setMessage(message);
        return response;
    }

    public static <D> ResponseEntity<D> error(int status, String message) {
        var response = new ResponseEntity<D>();
        response.setStatus(status);
        response.setMessage(message);
        return response;
    }

    public static <D> ResponseEntity<D> paramsError() {
        var response = new ResponseEntity<D>();
        response.setStatus(400);
        response.setMessage("params error");
        return response;
    }

    public static <D> ResponseEntity<D> resourcesNotFound() {
        var response = new ResponseEntity<D>();
        response.setStatus(404);
        response.setMessage("resources not found");
        return response;
    }

    public static <D> ResponseEntity<D> response(int status, String message, D data) {
        var entity = new ResponseEntity<D>();
        entity.setStatus(status);
        entity.setMessage(message);
        entity.setData(data);
        return entity;
    }
}
