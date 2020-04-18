package com.truthbean.debbie.properties;

import com.truthbean.debbie.bean.AnnotationRegister;
import com.truthbean.debbie.bean.BeanInitialization;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/5/22 21:08.
 */
public class PropertiesConfigurationRegister implements AnnotationRegister<PropertiesConfiguration> {
    private final BeanInitialization initialization;
    public PropertiesConfigurationRegister(BeanInitialization beanInitialization) {
        this.initialization = beanInitialization;
    }

    @Override
    public void register() {
        register(PropertiesConfiguration.class);
    }

    @Override
    public BeanInitialization getBeanInitialization() {
        return initialization;
    }
}
