package com.truthbean.debbie.core.bean;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/06/02 16:37.
 */
public class DebbieBeanInfoFactory {
    private DebbieBeanInfo beanInfo;

    public DebbieBeanInfoFactory() {
    }

    public DebbieBeanInfoFactory(DebbieBeanInfo beanInfo) {
        this.beanInfo = beanInfo;
    }

    public void setBeanInfo(DebbieBeanInfo beanInfo) {
        this.beanInfo = beanInfo;
    }
}
