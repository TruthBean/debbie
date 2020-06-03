/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.tomcat;

import com.truthbean.debbie.server.AbstractServerConfiguration;

import java.nio.charset.Charset;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/10 14:30.
 */
public class TomcatConfiguration extends AbstractServerConfiguration {

    public TomcatConfiguration(ClassLoader classLoader){
        super(classLoader);
    }

    public TomcatConfiguration(AbstractServerConfiguration abstractServerConfiguration, ClassLoader classLoader) {
        super(classLoader);
        port(abstractServerConfiguration.getPort());
        host(abstractServerConfiguration.getHost());
        web(abstractServerConfiguration.isWeb());
        enableCors(abstractServerConfiguration.isEnableCors());
        setCorsOrigins(abstractServerConfiguration.getCorsOrigins());
        setCorsHeaders(abstractServerConfiguration.getCorsHeaders());
        setCorsMethods(abstractServerConfiguration.getCorsMethods());
    }

    private String webappDir;

    private boolean disableMBeanRegistry;

    private boolean autoDeploy;

    private String connectorProtocol;

    private Charset uriEncoding;

    private boolean cachingAllowed;
    private Integer cacheMaxSize;

    public String getWebappDir() {
        return webappDir;
    }

    public void setWebappDir(String webappDir) {
        this.webappDir = webappDir;
    }

    public boolean isDisableMBeanRegistry() {
        return disableMBeanRegistry;
    }

    public void setDisableMBeanRegistry(boolean disableMBeanRegistry) {
        this.disableMBeanRegistry = disableMBeanRegistry;
    }

    public boolean isAutoDeploy() {
        return autoDeploy;
    }

    public void setAutoDeploy(boolean autoDeploy) {
        this.autoDeploy = autoDeploy;
    }

    public String getConnectorProtocol() {
        return connectorProtocol;
    }

    public void setConnectorProtocol(String connectorProtocol) {
        this.connectorProtocol = connectorProtocol;
    }

    public Charset getUriEncoding() {
        return uriEncoding;
    }

    public void setUriEncoding(Charset uriEncoding) {
        this.uriEncoding = uriEncoding;
    }

    public boolean isCachingAllowed() {
        return cachingAllowed;
    }

    public void setCachingAllowed(boolean cachingAllowed) {
        this.cachingAllowed = cachingAllowed;
    }

    public Integer getCacheMaxSize() {
        return cacheMaxSize;
    }

    public void setCacheMaxSize(Integer cacheMaxSize) {
        this.cacheMaxSize = cacheMaxSize;
    }
}
