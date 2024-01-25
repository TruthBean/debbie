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

import java.net.http.WebSocket;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.1
 * Created on 2021-06-04 16:35
 */
public interface FirstMessageCallback {

    /**
     * if break message
     * @return if true break, else continue
     */
    default boolean callback(String message, WebSocket webSocket, boolean last) {
        return false;
    }
}
