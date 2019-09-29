package com.truthbean.debbie.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class TestRmiServiceImpl extends UnicastRemoteObject implements TestRmiService {
    public TestRmiServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public String queryName(String id) throws RemoteException {
        return "pong " + id;
    }
}
