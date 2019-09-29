package com.truthbean.debbie.rmi;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RmiClientFactory {

    private Registry registry;

    public RmiClientFactory(int port) {
        try {
            registry = LocateRegistry.getRegistry(port);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public RmiClientFactory(String address, int port) {
        try {
            registry = LocateRegistry.getRegistry(address, port);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public Registry getRegistry() {
        return registry;
    }

    public String[] listName() {
        try {
            return registry.list();
        } catch (RemoteException e) {
            e.printStackTrace();
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
            e.printStackTrace();
        }
        return false;
    }

    public Remote lookup(String name) {
        if (exits(name)) {
            try {
                return registry.lookup(name);
            } catch (RemoteException | NotBoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
