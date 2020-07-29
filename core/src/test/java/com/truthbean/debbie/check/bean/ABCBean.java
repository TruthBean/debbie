package com.truthbean.debbie.check.bean;

import com.truthbean.debbie.bean.BeanComponent;
import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.bean.BeanType;

import java.util.UUID;

@BeanComponent(type = BeanType.SINGLETON)
public class ABCBean {

    private final String a;

    private final ABean aBean;
    private final BBean bBean;
    private final CBean cBean;

    public ABCBean(@BeanInject ABean aBean, @BeanInject BBean bBean, @BeanInject CBean cBean) {
        this.aBean = aBean;
        this.bBean = bBean;
        this.cBean = cBean;

        a = "ABC --- " + UUID.randomUUID().toString();
    }

    public ABean getaBean() {
        return aBean;
    }

    public BBean getbBean() {
        return bBean;
    }

    public CBean getcBean() {
        return cBean;
    }

    @Override
    public String toString() {
        return "ABCBean:{" + "a:\'" + a + '\'' + "," + "aBean:" + aBean + "," + "bBean:" + bBean + "," + "cBean:" + cBean + '}';
    }
}