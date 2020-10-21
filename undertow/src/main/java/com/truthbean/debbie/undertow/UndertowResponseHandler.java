/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.undertow;

import com.truthbean.debbie.io.MediaTypeInfo;
import com.truthbean.debbie.mvc.response.HttpStatus;
import com.truthbean.debbie.mvc.response.ResponseHandler;
import com.truthbean.debbie.mvc.response.RouterResponse;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import com.truthbean.Logger;
import com.truthbean.logger.LoggerFactory;

import java.net.HttpCookie;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Set;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class UndertowResponseHandler implements ResponseHandler {

    private final HttpServerExchange exchange;

    public UndertowResponseHandler(final HttpServerExchange exchange) {
        this.exchange = exchange;
    }

    @Override
    public void changeResponseWithoutContent(RouterResponse response) {
        Map<String, String> headers = response.getHeaders();
        if (!headers.isEmpty()) {
            headers.forEach((key, value) -> exchange.getResponseHeaders().put(new HttpString(key), value));
        }

        Set<HttpCookie> cookies = response.getCookies();
        if (!cookies.isEmpty()) {
            cookies.forEach(cookie -> exchange.setResponseCookie(new UndertowRouterCookie(cookie).getCookie()));
        }
        var responseType = response.getResponseType();
        if (responseType != null) {
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, responseType.toString());
        }
        try {
            Map<String, Object> modelAttributes = response.getModelAttributes();
            if (modelAttributes != null && !modelAttributes.isEmpty()) {
                // todo set model and attributes
            }
        } catch (Exception e) {
            LOGGER.error("request.setAttribute error", e);
        }
    }

    @Override
    public void handle(RouterResponse response, MediaTypeInfo defaultResponseType) {
        HttpStatus status = response.getStatus();
        if (status == null) {
            status = HttpStatus.OK;
        }
        exchange.setStatusCode(status.getStatus());

        Object responseData = response.getContent();
        MediaTypeInfo responseType = response.getResponseType();

        var sender = exchange.getResponseSender();
        if (responseData instanceof ByteBuffer) {
            LOGGER.trace("response ByteBuffer");
            sender.send((ByteBuffer) responseData);
        }
        if (responseData instanceof byte[]) {
            LOGGER.trace("response byte[]");
            sender.send(ByteBuffer.wrap((byte[]) responseData));
        } else {
            // 404
            // exchange.setStatusCode(HttpStatus.NOT_FOUND.getStatus());

            // Response Headers
            if (responseType == null) {
                responseType = defaultResponseType;
            }
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, responseType.toString());
            // Response Sender
            if (responseData != null) {
                var strResponse = responseData.toString();
                LOGGER.trace(() -> "response : " + strResponse);
                sender.send(strResponse);
            } else {
                sender.send("");
            }
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(UndertowResponseHandler.class);
}
