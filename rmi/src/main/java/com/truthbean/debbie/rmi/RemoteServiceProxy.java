/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * @author truthbean
 * @since 0.0.2
 */
public class RemoteServiceProxy<Service> extends UnicastRemoteObject implements RemoteService<Service> {

    protected RemoteServiceProxy() throws RemoteException {
        super();
    }

    private Service service;

    @Override
    public Service getService() throws RemoteException {
        return service;
    }

    @Override
    public void setService(Service service) throws RemoteException {
        this.service = service;
    }

    @Override
    public String getVersion() throws RemoteException {
        return "0.0.2-SNAPSHOT";
    }
}
