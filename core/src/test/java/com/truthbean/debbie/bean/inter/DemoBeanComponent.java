package com.truthbean.debbie.bean.inter;

import com.truthbean.debbie.bean.BeanComponent;
import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.bean.BeanType;
import com.truthbean.debbie.proxy.MethodProxy;

import java.util.Random;
import java.util.UUID;

@BeanComponent(type = BeanType.NO_LIMIT)
public class DemoBeanComponent {

    private final String uuid;

    private final Demo2 demo1;
    private final Demo2 demo2;

    public DemoBeanComponent(@BeanInject(name = "demo") Demo2 demo1, @BeanInject(name = "demo") Demo2 demo2) {
        uuid = UUID.randomUUID().toString();
        this.demo2 = demo2;
        this.demo1 = demo1;
    }

    @MethodProxy(order = 31)
    public String getUuid() {
        return uuid;
    }

    public Demo2 getDemo2() {
        return demo2;
    }

    public Demo2 getDemo1() {
        return demo1;
    }

    @BeanComponent(name = "demo", type = BeanType.NO_LIMIT)
    public static class Demo2 {
        private boolean id;

        public Demo2() {
        }

        @MethodProxy(order = 32)
        public boolean getUuid() {
            return id;
        }

        @MethodProxy(order = 32)
        public void setId(boolean id, int a) {
            this.id = id;
        }
    }
}