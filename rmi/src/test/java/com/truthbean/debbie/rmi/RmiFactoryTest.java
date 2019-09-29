package com.truthbean.debbie.rmi;

import java.rmi.RemoteException;

public class RmiFactoryTest {

    public static void main(String[] args) {
        //注册管理器
        RemoteServiceRegister register = new RemoteServiceRegister("192.168.1.198", 8088);
        //创建一个服务
        TestRmiService service = null;
        try {
            service = new TestRmiServiceImpl();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        register.bind("test", service);
    }
}
