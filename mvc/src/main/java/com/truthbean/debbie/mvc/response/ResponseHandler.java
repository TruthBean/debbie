package com.truthbean.debbie.mvc.response;

import com.truthbean.debbie.io.MediaTypeInfo;

import java.nio.ByteBuffer;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public interface ResponseHandler {

    default void handle(RouterResponse response) {

        Object responseData = response.getContent();
        MediaTypeInfo responseType = response.getResponseType();

        if (responseData instanceof ByteBuffer) {
            response.setContent(response);
        }
        if (responseData instanceof byte[]) {
            response.setContent(response);
        } else {
            response.addHeader("Content-Type", responseType.toString());
            if (responseData != null) {
                response.setContent(responseData.toString());
            } else {
                response.setContent("");
            }
        }
    }
}
