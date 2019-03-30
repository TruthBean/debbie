package com.truthbean.code.debbie.core.bean;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/23 11:32.
 */
public class BeanFactory {

    public static <Bean> BeanInvoker<Bean> factory(Class<Bean> beanClass) {
        var beanBeanInvoker = new BeanInvoker<>(beanClass);
        return beanBeanInvoker;
    }
}
