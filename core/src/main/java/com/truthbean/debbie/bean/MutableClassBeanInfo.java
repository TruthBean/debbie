package com.truthbean.debbie.bean;

import com.truthbean.debbie.reflection.ClassInfo;

/**
 * @author TruthBean
 * @since 0.5.3
 * Created on 2021/12/07 23:07.
 */
public abstract class MutableClassBeanInfo<Bean> extends ClassInfo<Bean> implements MutableBeanInfo<Bean> {
    public MutableClassBeanInfo(Class<Bean> clazz) {
        super(clazz);
    }

    @Override
    public abstract MutableClassBeanInfo<Bean> copy();

    @Override
    public boolean equals(Object o) {
        return isEquals(o);
    }

    @Override
    public int hashCode() {
        return getHashCode(super.hashCode());
    }
}
