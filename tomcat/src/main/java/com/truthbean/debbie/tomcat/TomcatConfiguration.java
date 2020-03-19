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
}
