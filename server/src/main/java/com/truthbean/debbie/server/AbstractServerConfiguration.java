/**
 * Copyright (c) 2024 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.server;

import com.truthbean.debbie.properties.DebbieConfiguration;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public abstract class AbstractServerConfiguration implements DebbieConfiguration {

    private String name;

    // NOTE: 端口不能用short类型，因为short类型范围是-32768~32767，而Linux目前的端口最大可以为65535
    private int port = 8080;
    private String host = "localhost";

    private String serverHeader;

    /**
     * NOTE: need java16 least
     * unix domain socket path
     */
    private String socketPath;

    public String getName() {
        return name;
    }

    protected AbstractServerConfiguration name(String name) {
        this.name = name;
        return this;
    }

    public int getPort() {
        return port;
    }

    protected AbstractServerConfiguration port(int port) {
        // TODO: check port between -1 to 65535
        this.port = port;
        return this;
    }

    public String getHost() {
        return host;
    }

    protected AbstractServerConfiguration host(String host) {
        this.host = host;
        return this;
    }

    protected AbstractServerConfiguration serverHeader(String serverHeader) {
        this.serverHeader = serverHeader;
        return this;
    }

    public String getServerHeader() {
        return serverHeader;
    }

    protected AbstractServerConfiguration socketPath(String socketPath) {
        this.socketPath = socketPath;
        return this;
    }

    public String getSocketPath() {
        return socketPath;
    }

    public void check() {
        boolean illegal = ((host == null || host.isBlank()) || port <= 0) || socketPath == null;
        if (illegal) {
            throw new RuntimeException("host is null or port is wrong, or java 16 unix domain socket path is null");
        }
    }

}
