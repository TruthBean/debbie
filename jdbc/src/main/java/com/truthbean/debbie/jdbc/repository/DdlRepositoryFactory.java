package com.truthbean.debbie.jdbc.repository;

import com.truthbean.debbie.bean.DebbieApplicationContext;
import com.truthbean.debbie.bean.BeanFactory;
import com.truthbean.debbie.bean.GlobalBeanFactory;
import com.truthbean.debbie.jdbc.datasource.DataSourceConfiguration;

/**
 * @author truthbean/Rogar·Q
 * @since 0.0.2
 * Created on 2020-04-13 15:11.
 */
public class DdlRepositoryFactory implements BeanFactory<DdlRepository> {

    private GlobalBeanFactory beanFactory;

    @Override
    public void setGlobalBeanFactory(GlobalBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public DdlRepository getBean() {
        DataSourceConfiguration configuration = this.beanFactory.factory(DataSourceConfiguration.class);
        return new DdlRepository(configuration);
    }

    @Override
    public Class<DdlRepository> getBeanType() {
        return DdlRepository.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    @Override
    public void destroy() {

    }
}
