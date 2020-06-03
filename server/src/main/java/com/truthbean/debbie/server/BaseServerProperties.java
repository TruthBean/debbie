/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.server;

import com.truthbean.debbie.properties.BaseProperties;
import com.truthbean.debbie.properties.DebbieProperties;

import java.util.UUID;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public abstract class BaseServerProperties<C extends AbstractServerConfiguration> extends BaseProperties
        implements DebbieProperties<C> {

    //===========================================================================
    private static final String APPLICATION_NAME = "debbie.application.name";

    private static final String SERVER_PORT = "debbie.server.port";
    private static final String SERVER_HOST = "debbie.server.host";

    private static final String SERVER_HEADER = "debbie.server.server-header";

    //===========================================================================

    public <P extends BaseServerProperties<C>> void loadAndSet(P properties, C configuration) {
        String name = properties.getStringValue(APPLICATION_NAME, UUID.randomUUID().toString());
        int port = properties.getIntegerValue(SERVER_PORT, 8080);
        String host = properties.getStringValue(SERVER_HOST, "0.0.0.0");
        configuration.name(name).web(true).port(port).host(host);
    }

}
