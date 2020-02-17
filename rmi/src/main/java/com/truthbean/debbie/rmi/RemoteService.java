package com.truthbean.debbie.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author truthbean
 * @since 0.0.2
 */
public interface RemoteService<Service> extends Remote {

    void setService(Service service) throws RemoteException;

    Service getService() throws RemoteException;

    String getVersion() throws RemoteException;
}
