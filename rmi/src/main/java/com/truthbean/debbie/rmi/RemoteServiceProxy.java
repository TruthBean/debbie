package com.truthbean.debbie.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

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
