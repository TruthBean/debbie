package com.truthbean.debbie.bean;

/**
 * @author TruthBean
 * @since 0.0.2
 * Created on 2019/06/02 16:54.
 */
public class DebbieBeanFactory<Bean> implements BeanFactory<Bean> {

    private DebbieBeanInfo<Bean> beanInfo;
    private BeanFactoryHandler beanFactoryHandler;

    public DebbieBeanFactory() {
    }

    public DebbieBeanFactory(DebbieBeanInfo<Bean> beanInfo) {
        this.beanInfo = beanInfo;
    }

    public void setBeanInfo(DebbieBeanInfo<Bean> beanInfo) {
        this.beanInfo = beanInfo;
    }

    @Override
    public void setBeanFactoryHandler(BeanFactoryHandler beanFactoryHandler) {
        this.beanFactoryHandler = beanFactoryHandler;
    }

    private boolean canNew() {
        BeanType beanType = beanInfo.getBeanType();
        return beanType == BeanType.NO_LIMIT || (beanType == BeanType.SINGLETON && beanInfo.getBean() == null);
    }

    @Override
    public Bean getBean() {
        if (canNew()) {
            var beanBeanInvoker = new BeanInvoker<>(getBeanType(), beanFactoryHandler);
            var bean = beanBeanInvoker.getBean();
            var classInfo = beanBeanInvoker.getBeanInfo();
            beanFactoryHandler.resolveDependentBean(bean, classInfo);
            beanInfo.setBean(bean);
            return bean;
        }
        return beanInfo.getBean();
    }

    @Override
    public Class<Bean> getBeanType() {
        return beanInfo.getBeanClass();
    }

    @Override
    public boolean isSingleton() {
        return beanInfo.getBeanType() == BeanType.SINGLETON;
    }

    @Override
    public void destroy() {
        // do nothing
    }
}
