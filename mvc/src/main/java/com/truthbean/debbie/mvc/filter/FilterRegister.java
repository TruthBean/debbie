package com.truthbean.debbie.mvc.filter;

import com.truthbean.debbie.bean.AnnotationRegister;
import com.truthbean.debbie.bean.BeanInitialization;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/5/22 21:26.
 */
public class FilterRegister implements AnnotationRegister<Filter> {
    private BeanInitialization initialization;

    public void setInitialization(BeanInitialization initialization) {
        this.initialization = initialization;
    }

    @Override
    public void register() {
        register(Filter.class);
    }

    @Override
    public BeanInitialization getBeanInitialization() {
        return initialization;
    }
}
