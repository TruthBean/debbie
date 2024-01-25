/**
 * Copyright (c) 2024 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.httpclient.ws;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;

import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-08-22 10:26
 */
public class WebSocketListener implements WebSocket.Listener {

    private final AtomicBoolean textFirst = new AtomicBoolean();
    private final AtomicBoolean binaryFirst = new AtomicBoolean();
    private final ThreadLocal<StringBuilder> stringBuffer = new ThreadLocal<>() {
        @Override
        protected StringBuilder initialValue() {
            return new StringBuilder();
        }
    };

    private final FirstMessageCallback firstMessageCallback;

    public WebSocketListener() {
        this.firstMessageCallback = null;
    }

    public WebSocketListener(FirstMessageCallback firstMessageCallback) {
        this.firstMessageCallback = firstMessageCallback;
    }

    @Override
    public void onOpen(WebSocket webSocket) {
        LOGGER.debug("onOpen ...... ");
        webSocket.request(1);
    }

    @Override
    public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
        LOGGER.debug("onClose with " + statusCode + " for " + reason + " ..... ");
        return new CompletableFuture<>();
    }

    @Override
    public void onError(WebSocket webSocket, Throwable error) {
        LOGGER.error("onError. ", error);
    }

    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
        webSocket.request(1);
        if (!last) {
            stringBuffer.get().append(data);
        } else {
            stringBuffer.get().append(data);
            var dataStr = stringBuffer.get().toString();
            LOGGER.debug("message: " + dataStr);
            stringBuffer.remove();
            stringBuffer.set(new StringBuilder());
        }
        if (textFirst.compareAndSet(false, true)) {
            var dataStr = data.toString();
            LOGGER.debug("first message: " + dataStr);
            if (firstMessageCallback != null) {
                boolean check = firstMessageCallback.callback(dataStr, webSocket, last);
                if (check) {
                    return null;
                }
            }
        }
        return CompletableFuture.completedFuture(webSocket);
    }

    @Override
    public CompletionStage<?> onBinary(WebSocket webSocket, ByteBuffer data, boolean last) {
        webSocket.request(1);
        return CompletableFuture.completedFuture(webSocket);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketListener.class);
}
