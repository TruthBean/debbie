/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
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
 * Base configuration for a Debbie web server, holding the server name,
 * port, host, server header, and optional Unix domain socket path.
 *
 * @author TruthBean
 * @since 0.0.1
 */
public abstract class AbstractServerConfiguration implements DebbieConfiguration {

    /** server name */
    private String name;

    /** server port (1–65535; stored as int because short cannot hold 65535) */
    private int port = 8080;
    /** server bind host */
    private String host = "localhost";

    /** value for the {@code Server} response header */
    private String serverHeader;

    /**
     * Unix domain socket path (requires Java 16+).
     */
    private String socketPath;

    /** Returns the server name. */
    public String getName() {
        return name;
    }

    /** Sets the server name; returns {@code this} for chaining. */
    protected AbstractServerConfiguration name(String name) {
        this.name = name;
        return this;
    }

    /** Returns the server port. */
    public int getPort() {
        return port;
    }

    /** Sets the server port; returns {@code this} for chaining. */
    protected AbstractServerConfiguration port(int port) {
        // TODO: check port between -1 to 65535
        this.port = port;
        return this;
    }

    /** Returns the bind host. */
    public String getHost() {
        return host;
    }

    /** Sets the bind host; returns {@code this} for chaining. */
    protected AbstractServerConfiguration host(String host) {
        this.host = host;
        return this;
    }

    /** Sets the {@code Server} header value; returns {@code this} for chaining. */
    protected AbstractServerConfiguration serverHeader(String serverHeader) {
        this.serverHeader = serverHeader;
        return this;
    }

    /** Returns the {@code Server} header value. */
    public String getServerHeader() {
        return serverHeader;
    }

    /** Sets the Unix domain socket path; returns {@code this} for chaining. */
    protected AbstractServerConfiguration socketPath(String socketPath) {
        this.socketPath = socketPath;
        return this;
    }

    /** Returns the Unix domain socket path, or {@code null} if not using UDS. */
    public String getSocketPath() {
        return socketPath;
    }

    /**
     * Validates that host is non-blank, port is positive, or a socket
     * path is set; throws {@link RuntimeException} otherwise.
     */
    public void check() {
        boolean illegal = ((host == null || host.isBlank()) || port <= 0) || socketPath == null;
        if (illegal) {
            throw new RuntimeException("host is null or port is wrong, or java 16 unix domain socket path is null");
        }
    }

}
