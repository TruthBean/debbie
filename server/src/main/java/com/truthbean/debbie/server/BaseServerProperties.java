/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.server;

import com.truthbean.debbie.environment.DebbieEnvironmentDepositoryHolder;
import com.truthbean.debbie.properties.DebbieProperties;

import java.util.UUID;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public abstract class BaseServerProperties<C extends AbstractServerConfiguration> extends DebbieEnvironmentDepositoryHolder
        implements DebbieProperties<C> {

    //===========================================================================
    private static final String APPLICATION_NAME = "debbie.application.name";

    private static final String SERVER_PORT = "debbie.server.port";
    private static final String SERVER_HOST = "debbie.server.host";

    private static final String SERVER_HEADER = "debbie.server.server-header";
    //===========================================================================
    // unix domain socket
    // NOTE: need java16 least
    private static final String SOCKET_PATH = "debbie.server.unix.socket";
    // ===========================================================================
    private static final String SERVER_PREFIX = "debbie.server.";
    private static final String DOT_PORT = "port";
    private static final String DOT_HOST = "host";
    private static final String DOT_SERVER_HEADER = ".server-header";

    public <P extends BaseServerProperties<C>> void loadAndSet(String category, P properties, C configuration) {
        if (DEFAULT_CATEGORY.equals(category)) {
            category = "";
        }
        String name = properties.getStringValue(APPLICATION_NAME, UUID.randomUUID().toString());
        var portStr = properties.getValue(SERVER_PREFIX + category + DOT_PORT);
        int port;
        if (portStr == null) {
            port = properties.getIntegerValue(SERVER_PORT, 8080);
        } else {
            port = properties.getInteger(portStr, 8080);
        }
        var hostStr = properties.getValue(SERVER_PREFIX + category + DOT_HOST);
        String host;
        if (hostStr == null) {
            host = properties.getStringValue(SERVER_HOST, "0.0.0.0");
        } else {
            host = properties.getStringValue(hostStr, "0.0.0.0");
        }
        configuration.name(name).port(port).host(host);
    }

}
