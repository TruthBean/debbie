/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.aio.websocket;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry of {@link WebSocketEndpoint} instances for the AIO server.
 * Endpoints are discovered with {@link ServiceLoader} at server start and
 * can also be registered programmatically. Path matching prefers exact paths,
 * then the longest {@code prefix/*} pattern, and finally a global {@code /*}.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.4
 */
public final class WebSocketEndpointManager {

    private static final Map<String, WebSocketEndpoint> ENDPOINTS = new ConcurrentHashMap<>();

    private WebSocketEndpointManager() {
    }

    /**
     * Registers an endpoint under its {@link WebSocketEndpoint#getPath()}.
     *
     * @param endpoint the endpoint to register
     */
    public static void registerEndpoint(WebSocketEndpoint endpoint) {
        if (endpoint != null && endpoint.getPath() != null && !endpoint.getPath().isBlank()) {
            ENDPOINTS.put(endpoint.getPath(), endpoint);
            LOGGER.debug(() -> "registered WebSocket endpoint: " + endpoint.getPath());
        }
    }

    /**
     * Discovers and registers all endpoints declared via
     * {@code META-INF/services/com.truthbean.debbie.aio.websocket.WebSocketEndpoint}.
     *
     * @param classLoader the class loader to load providers from
     */
    public static void loadEndpoints(ClassLoader classLoader) {
        ServiceLoader.load(WebSocketEndpoint.class, classLoader)
                .forEach(WebSocketEndpointManager::registerEndpoint);
    }

    /**
     * Finds the endpoint serving the given request path.
     *
     * @param path the upgrade request path
     * @return the matched endpoint, or {@code null} when nothing matches
     */
    public static WebSocketEndpoint matchEndpoint(String path) {
        if (path == null) {
            return null;
        }
        WebSocketEndpoint exact = ENDPOINTS.get(path);
        if (exact != null) {
            return exact;
        }
        WebSocketEndpoint best = null;
        int bestLength = -1;
        for (Map.Entry<String, WebSocketEndpoint> entry : ENDPOINTS.entrySet()) {
            String pattern = entry.getKey();
            if (pattern.endsWith("/*")) {
                String prefix = pattern.substring(0, pattern.length() - 2);
                if (prefix.isEmpty()) {
                    if (best == null) {
                        best = entry.getValue();
                    }
                } else if (path.startsWith(prefix) && prefix.length() > bestLength) {
                    bestLength = prefix.length();
                    best = entry.getValue();
                }
            }
        }
        return best;
    }

    /**
     * @return {@code true} when at least one endpoint is registered
     */
    public static boolean hasEndpoints() {
        return !ENDPOINTS.isEmpty();
    }

    static void clear() {
        ENDPOINTS.clear();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketEndpointManager.class);
}
