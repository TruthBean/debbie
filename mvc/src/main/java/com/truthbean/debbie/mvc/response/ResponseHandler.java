/**
 * Copyright (c) 2022 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
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
