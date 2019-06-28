package com.truthbean.debbie.mvc.filter;

import com.truthbean.debbie.bean.AnnotationRegister;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/5/22 21:26.
 */
public class FilterRegister implements AnnotationRegister<Filter> {
    @Override
    public void register() {
        register(Filter.class);
    }
}
