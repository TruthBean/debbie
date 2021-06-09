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
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.data.serialize.JacksonJsonUtils;
import com.truthbean.debbie.httpclient.ws.FirstMessageCallback;
import com.truthbean.debbie.mvc.response.ResponseEntity;

import java.net.http.WebSocket;
import java.util.HashMap;
import java.util.Map;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.1
 * Created on 2021-06-04 16:38
 */
public class FirstMessageCallbackTest implements FirstMessageCallback {

    @Override
    public boolean callback(String message, WebSocket webSocket, boolean last) {
        ResponseEntity<?> response = JacksonJsonUtils.jsonToBean(message, ResponseEntity.class);
        if (response == null) {
            return true;
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
        return false;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(FirstMessageCallbackTest.class);
}
