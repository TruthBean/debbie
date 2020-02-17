package com.truthbean.debbie.proxy.interfaces;

import com.truthbean.debbie.proxy.MethodProxyHandler;
import com.truthbean.debbie.proxy.MethodProxy;

public class TargetProxyHandler implements MethodProxyHandler<MethodProxy> {

    private int order = 10000;

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public void setOrder(int order) {
        this.order = order;
    }

    public void before() {
        System.out.println("思考中......");
    }

    public void after() {
        System.out.println("检查中......");
    }

    @Override
    public void catchException(Throwable e) throws Throwable {
        System.out.println("啊~！出问题啊啊啊啊啊啊啊啊");
    }

    @Override
    public void finallyRun() {
        System.out.println("睡觉~");
    }
}
