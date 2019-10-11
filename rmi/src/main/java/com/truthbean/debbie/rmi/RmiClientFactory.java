package com.truthbean.debbie.rmi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

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

    public <S> S lookup(String name) {
        if (exits(name)) {
            try {
                RemoteService<S> remoteService = (RemoteService<S>) registry.lookup(name);
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
