package com.truthbean.debbie.proxy.bean;

import com.truthbean.debbie.bean.BeanComponent;
import com.truthbean.debbie.proxy.BeanProxyType;
import com.truthbean.debbie.proxy.MethodProxy;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.0.2
 * Created on 2020-04-22 22:45
 */
@BeanComponent(proxy = BeanProxyType.ASM)
public class TargetObject {

    public TargetObject(String value, int a, char c, byte b, short s, long l, float f, double d) throws Exception {
    }

    @MethodProxy(order = 1)
    public void code(String language) throws Exception {
        System.out.println("逗逼程序猿在用" + language + "编程...");
        // throw new NullPointerException();
    }

    @MethodProxy(order = 1)
    public void code(String language, int index, final float f) {
        System.out.println("逗逼程序猿在用" + language + "编程...");
        System.out.println("index: " + index);
        System.out.println("float: " + f);
    }

    public void coding() {
        System.out.println("coding");
    }

    public void learn() {
        System.out.println("逗逼程序猿在疯狂学习中...");
    }
}
