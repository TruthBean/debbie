package com.truthbean.debbie.bean;

import java.util.UUID;

@BeanComponent(type = BeanType.SINGLETON)
class ABCBean {

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

@BeanComponent(type = BeanType.SINGLETON)
class ABean {

    private final String a;

    private BBean bBean;
    private CBean cBean;

    public String getA() {
        return a;
    }

    public ABean(@BeanInject BBean bBean, @BeanInject CBean cBean) {
        this.bBean = bBean;
        this.cBean = cBean;

        a = "A --- " + UUID.randomUUID().toString();
    }

    @Override
    public String toString() {
        return "ABean:{" + "a:\'" + a + '\'' + "," + "bBean:" + bBean + "," + "cBean:" + cBean + '}';
    }
}

@BeanComponent(type = BeanType.SINGLETON)
class BBean {

    private final String b;

    private final CBean cBean;

    public String getB() {
        return b;
    }

    public BBean(@BeanInject CBean cBean) {
        this.cBean = cBean;

        b = "B --- " + UUID.randomUUID().toString();
    }

    @Override
    public String toString() {
        return "BBean:{" + "b:\'" + b + '\'' + "," + "cBean:" + cBean + '}';
    }
}

@BeanComponent(type = BeanType.SINGLETON)
class CBean {

    private final String c;

    @BeanInject
    private ABean aBean;

    @BeanInject
    private BBean bBean;


    public String getC() {
        return c;
    }

    public CBean() {
        c = "C --- " + UUID.randomUUID().toString();
    }

    @Override
    public String toString() {
        String a = null, b = null;
        if (aBean != null) {
            a = aBean.getA();
        }
        if (bBean != null) {
            b = bBean.getB();
        }
        return "{" +
                "\"c\":\"" + c + '\"' +
                ",\"aBean\":" + a +
                ",\"bBean\":" + b +
                '}';
    }
}
