package com.truthbean.debbie.undertow;

import com.truthbean.debbie.core.io.MediaType;
import com.truthbean.debbie.mvc.response.ResponseHandler;
import com.truthbean.debbie.mvc.response.RouterResponse;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;

import java.nio.ByteBuffer;
import java.util.Map;

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
    public void handle(RouterResponse response) {
        Object responseData = response.getContent();
        MediaType responseType = response.getResponseType();

        Map<String, String> headers = response.getHeaders();
        if (!headers.isEmpty()) {
            headers.forEach((key, value) -> exchange.getResponseHeaders().put(new HttpString(key), value));
        }

        var sender = exchange.getResponseSender();
        if (responseData instanceof ByteBuffer) {
            sender.send((ByteBuffer) responseData);
        }
        if (responseData instanceof byte[]) {
            sender.send(ByteBuffer.wrap((byte[]) responseData));
        } else {
            // 404
            // Response Headers
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, responseType.getValue());
            // Response Sender
            if (responseData != null) {
                sender.send(responseData.toString());
            } else {
                sender.send("");
            }
        }
    }
}
