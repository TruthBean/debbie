package com.truthbean.debbie.jdbc.repository;

import com.truthbean.debbie.bean.BeanFactory;
import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.jdbc.datasource.DataSourceConfiguration;

/**
 * @author truthbean/Rogar·Q
 * @since 0.0.2
 * Created on 2020-04-13 15:11.
 */
public class DdlRepositoryFactory implements BeanFactory<DdlRepository> {

    private BeanFactoryHandler beanFactoryHandler;

    @Override
    public void setBeanFactoryHandler(BeanFactoryHandler beanFactoryHandler) {
        this.beanFactoryHandler = beanFactoryHandler;
    }

    @Override
    public DdlRepository getBean() {
        DataSourceConfiguration configuration = this.beanFactoryHandler.factory(DataSourceConfiguration.class);
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
