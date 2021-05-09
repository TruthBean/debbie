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

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * @author truthbean
 * @since 0.0.2
 */
public class RmiClientFactory {

    private Registry registry;

    public RmiClientFactory(int port) {
        try {
            registry = LocateRegistry.getRegistry(port);
        } catch (RemoteException e) {
            LOGGER.error("", e);
        }
    }

    public RmiClientFactory(String address, int port) {
        try {
            registry = LocateRegistry.getRegistry(address, port);
        } catch (RemoteException e) {
            LOGGER.error("", e);
        }
    }

    public Registry getRegistry() {
        return registry;
    }

    public String[] listName() {
        try {
            return registry.list();
        } catch (RemoteException e) {
            LOGGER.error("", e);
        }
        return null;
    }

    public boolean exits(String name) {
        try {
            String[] list = registry.list();
            for (String s : list) {
                if (s.equals(name)) {
                    return true;
                }
            }
        } catch (RemoteException e) {
            LOGGER.error("", e);
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public <S> S lookup(String name) {
        if (exits(name)) {
            try {
                RemoteService<S> remoteService = (RemoteService<S>) registry.lookup(name);
                if (LOGGER.isDebugEnabled())
                    LOGGER.debug("remote server version: " + remoteService.getVersion());
                return remoteService.getService();
            } catch (RemoteException | NotBoundException e) {
                LOGGER.error("", e);
            }
        }
        return null;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(RmiClientFactory.class);
}
