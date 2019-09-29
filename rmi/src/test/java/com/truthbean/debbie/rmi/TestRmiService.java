package com.truthbean.debbie.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

@DebbieRmiService
public interface TestRmiService extends Remote {
    String queryName(String id) throws RemoteException;
}