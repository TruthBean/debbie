package com.truthbean.debbie.bean.inter;

import com.truthbean.debbie.bean.BeanComponent;
import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.bean.BeanType;

import java.util.UUID;

@BeanComponent(name = "abc", type = BeanType.SINGLETON)
public class AbcImpl implements Abc {

    private final String a;

    private final A aBean;
    private final B bBean;
    private final C cBean;

    public AbcImpl(@BeanInject(name = "a") A aBean, @BeanInject(name = "b") B bBean, @BeanInject(name = "c") C cBean) {
        this.aBean = aBean;
        this.bBean = bBean;
        this.cBean = cBean;

        a = "ABC --- " + UUID.randomUUID().toString();
    }

    public A getaBean() {
        return aBean;
    }

    public B getbBean() {
        return bBean;
    }

    public C getcBean() {
        return cBean;
    }

    @Override
    public String toString() {
        return "ABCBean:{" + "a:\'" + a + '\'' + "," + "aBean:" + aBean + "," + "bBean:" + bBean + "," + "cBean:" + cBean + '}';
    }
}

