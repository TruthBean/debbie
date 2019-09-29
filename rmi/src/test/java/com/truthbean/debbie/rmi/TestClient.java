package com.truthbean.debbie.rmi;

import java.rmi.RemoteException;

public class TestClient {

    public static void main(String[] args) {
        //注册管理器
        RmiClientFactory factory = new RmiClientFactory("192.168.1.198", 8088);

        //列出所有注册的服务
        String[] list = factory.listName();
        for (String s : list) {
            System.out.println(s);
        }

        //根据命名获取服务
        TestRmiService service = (TestRmiService) factory.lookup("test");
        //调用远程方法
        String result = null;
        try {
            result = service.queryName("jack");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        //输出调用结果
        System.out.println("result from remote : " + result);
    }
}
