package com.truthbean.debbie.jdbc.repository;

import com.truthbean.debbie.bean.*;
import com.truthbean.debbie.jdbc.datasource.DataSourceConfiguration;

/**
 * @author OceanAi/武汉魅瞳科技有限公司
 * @since 0.1.0
 * Created on 2020/7/11 17:01.
 */
public class CustomRepositoryFactory<R extends CustomRepository<E, I>, E, I> implements BeanFactory<R> {

    private final Object reference = new Object();

    private GlobalBeanFactory globalBeanFactory;
    private DataSourceConfiguration configuration;

    private final DebbieBeanInfo<R> beanInfo;
    private R bean;

    public CustomRepositoryFactory(DebbieBeanInfo<R> beanInfo) {
        this.beanInfo = beanInfo;
    }

    @Override
    public R getBean() {
        if (bean == null) {
            synchronized (reference) {
                if (bean == null) {
                    if (configuration == null)
                        configuration = globalBeanFactory.factory(DataSourceConfiguration.class);
                    DmlRepositoryHandler<E, I> repositoryHandler = new DmlRepositoryHandler<>(configuration, getBeanType());
                    bean = globalBeanFactory.factoryBeanByDependenceProcessor(beanInfo, true);
                    bean.setRepositoryHandler(repositoryHandler);
                }
            }
        }
        return bean;
    }

    @Override
    public Class<R> getBeanType() {
        return beanInfo.getBeanClass();
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void destroy() {
        this.bean = null;
    }

    @Override
    public void setGlobalBeanFactory(GlobalBeanFactory globalBeanFactory) {
        this.globalBeanFactory = globalBeanFactory;
    }

}
