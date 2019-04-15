package com.truthbean.debbie.tomcat;

import com.truthbean.debbie.boot.AbstractServerConfiguration;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/10 14:30.
 */
public class TomcatConfiguration extends AbstractServerConfiguration {

    public TomcatConfiguration(){
        super();
    }

    public TomcatConfiguration(AbstractServerConfiguration abstractServerConfiguration) {
        port(abstractServerConfiguration.getPort());
        host(abstractServerConfiguration.getHost());
        web(abstractServerConfiguration.isWeb());
        setCors(abstractServerConfiguration.isCors());
        setCorsOrigins(abstractServerConfiguration.getCorsOrigins());
        setCorsHeaders(abstractServerConfiguration.getCorsHeaders());
        setCorsMethods(abstractServerConfiguration.getCorsMethods());
    }

    private String webappDir;

    public String getWebappDir() {
        return webappDir;
    }

    public void setWebappDir(String webappDir) {
        this.webappDir = webappDir;
    }
}
