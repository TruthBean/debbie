package com.truthbean.debbie.jdbc.annotation;

import com.truthbean.debbie.bean.AnnotationRegister;
import com.truthbean.debbie.bean.BeanInitialization;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/5/22 21:33.
 */
public class SqlRepositoryRegister implements AnnotationRegister<SqlRepository> {
    private final BeanInitialization initialization;
    public SqlRepositoryRegister(BeanInitialization beanInitialization) {
        this.initialization = beanInitialization;
    }

    @Override
    public void register() {
        register(SqlRepository.class);
    }

    @Override
    public BeanInitialization getBeanInitialization() {
        return initialization;
    }
}
