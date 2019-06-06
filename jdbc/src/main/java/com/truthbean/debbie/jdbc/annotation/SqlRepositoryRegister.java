package com.truthbean.debbie.jdbc.annotation;

import com.truthbean.debbie.bean.AnnotationRegister;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/5/22 21:33.
 */
public class SqlRepositoryRegister implements AnnotationRegister<SqlRepository> {
    @Override
    public void register() {
        register(SqlRepository.class);
    }
}
