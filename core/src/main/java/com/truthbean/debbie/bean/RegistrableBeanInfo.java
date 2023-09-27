package com.truthbean.debbie.bean;

/**
 * @author TruthBean
 * @since 0.5.3
 * Created on 2021/12/22 20:32.
 */
public interface RegistrableBeanInfo<Bean> extends MultiNameBeanInfo<Bean> {

    boolean isCreated();

}
