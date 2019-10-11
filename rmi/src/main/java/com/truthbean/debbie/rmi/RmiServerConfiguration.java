package com.truthbean.debbie.rmi;

import com.truthbean.debbie.properties.DebbieConfiguration;

public class RmiServerConfiguration implements DebbieConfiguration {

    private String rmiBindAddress;

    private int rmiBindPort;

    public String getRmiBindAddress() {
        return rmiBindAddress;
    }

    public void setRmiBindAddress(String rmiBindAddress) {
        this.rmiBindAddress = rmiBindAddress;
    }

    public int getRmiBindPort() {
        return rmiBindPort;
    }

    public void setRmiBindPort(int rmiBindPort) {
        this.rmiBindPort = rmiBindPort;
    }
}
