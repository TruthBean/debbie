package com.truthbean.debbie.properties;

import com.truthbean.debbie.bean.AnnotationRegister;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/5/22 21:08.
 */
public class PropertiesConfigurationRegister implements AnnotationRegister<PropertiesConfiguration> {
    @Override
    public void register() {
        register(PropertiesConfiguration.class);
    }
}