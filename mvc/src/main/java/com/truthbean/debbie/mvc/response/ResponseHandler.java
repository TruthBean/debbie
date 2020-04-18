package com.truthbean.debbie.mvc.response;

import com.truthbean.debbie.io.MediaTypeInfo;

import java.nio.ByteBuffer;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public interface ResponseHandler {

    void changeResponseWithoutContent(RouterResponse routerResponse);

    default void handle(RouterResponse response, MediaTypeInfo defaultResponseType) {

        Object responseData = response.getContent();
        MediaTypeInfo responseType = response.getResponseType();

        if (responseData instanceof ByteBuffer) {
            response.setContent(response);
        }
        if (responseData instanceof byte[]) {
            response.setContent(response);
        } else {
            if (responseType == null) {
                responseType = defaultResponseType;
            }
            response.addHeader("Content-Type", responseType.toString());
            if (responseData != null) {
                response.setContent(responseData.toString());
            } else {
                response.setContent("");
            }
        }
    }
}
