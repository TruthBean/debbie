/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.httpclient.test;

import com.truthbean.Logger;
import com.truthbean.debbie.data.serialize.JacksonJsonUtils;
import com.truthbean.debbie.mvc.response.ResponseEntity;
import com.truthbean.LoggerFactory;

import java.net.http.WebSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-08-22 10:26
 */
public class WebSocketListener implements WebSocket.Listener {

    private final AtomicBoolean first = new AtomicBoolean();
    private volatile StringBuffer stringBuffer = new StringBuffer();

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
        error.printStackTrace();
    }

    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
        webSocket.request(1);
        if (first.compareAndSet(false, true)) {
            LOGGER.debug(data.toString());
            var dataStr = data.toString();
            ResponseEntity<?> response = JacksonJsonUtils.jsonToBean(dataStr, ResponseEntity.class);
            if (response == null) {
                return null;
            }
            if (response.getData() instanceof String) {
                String o = (String) response.getData();
                Map<String, Object> request = new HashMap<>();
                request.put("request", 1);
                request.put("requestId", o);
                request.put("carriageId", 1);
                var text = JacksonJsonUtils.toJson(request);
                LOGGER.debug(text);
                webSocket.sendText(text, last);
            }
        } else if (!last) {
            stringBuffer.append(data);
        } else {
            stringBuffer.append(data);
            var dataStr = stringBuffer.toString();
            LOGGER.debug(dataStr);
            stringBuffer = new StringBuffer();
        }
        return CompletableFuture.completedFuture(webSocket);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketListener.class);
}
