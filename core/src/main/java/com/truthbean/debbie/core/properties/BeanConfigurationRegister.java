package com.truthbean.debbie.core.properties;

import com.truthbean.debbie.core.bean.AnnotationRegister;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/5/22 21:08.
 */
public class BeanConfigurationRegister implements AnnotationRegister<BeanConfiguration> {
    @Override
    public void register() {
        register(BeanConfiguration.class);
    }
}
