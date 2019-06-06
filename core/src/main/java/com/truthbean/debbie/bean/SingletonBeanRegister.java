package com.truthbean.debbie.bean;

/**
 * @author TruthBean
 * @since 0.0.2
 */
public class SingletonBeanRegister {
    private BeanFactoryHandler beanFactoryHandler;
    private BeanInitialization initialization;

    public SingletonBeanRegister(BeanFactoryHandler beanFactoryHandler) {
        this.beanFactoryHandler = beanFactoryHandler;
        initialization = beanFactoryHandler.getBeanInitialization();
    }

    public <T extends I, I> void registerSingletonBean(T bean, Class<I> beanClass, String beanName) {
        DebbieBeanInfo beanInfo = new DebbieBeanInfo<>(beanClass);
        beanInfo.setBeanName(beanName);
        beanInfo.setBean(bean);
        initialization.initSingletonBean(beanInfo);
        beanFactoryHandler.refreshBeans();
    }
}
