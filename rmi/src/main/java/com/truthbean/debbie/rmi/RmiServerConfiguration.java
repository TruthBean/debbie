/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.rmi;

import com.truthbean.debbie.properties.DebbieConfiguration;

/**
 * @author truthbean
 * @since 0.0.2
 */
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

    @Override
    public void reset() {

    }
}
