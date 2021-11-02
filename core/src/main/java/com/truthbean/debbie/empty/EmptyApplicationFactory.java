package com.truthbean.debbie.empty;

import com.truthbean.debbie.bean.BeanScanConfiguration;
import com.truthbean.debbie.boot.DebbieApplication;
import com.truthbean.debbie.boot.DebbieModuleStarter;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.core.ApplicationFactory;

/**
 * @author TruthBean
 * @since 0.5.3
 */
public class EmptyApplicationFactory implements ApplicationFactory {

    private final DebbieApplication debbieApplication = new EmptyDebbieApplication();

    @Override
    public void config(BeanScanConfiguration configuration) {

    }

    @Override
    public void release() {

    }

    @Override
    public DebbieApplication factoryApplication() {
        return debbieApplication;
    }

    @Override
    public ApplicationContext getApplicationContext() {
        return debbieApplication.getApplicationContext();
    }

    @Override
    public DebbieApplication postCreateApplication() {
        return debbieApplication;
    }

    @Override
    public DebbieApplication createApplication(Class<?> applicationClass) {
        return debbieApplication;
    }

    @Override
    public ApplicationFactory registerModuleStarter(DebbieModuleStarter moduleStarter) {
        return this;
    }

    @Override
    public ApplicationFactory preInit(String... args) {
        return this;
    }

    @Override
    public ApplicationFactory init(Class<?> applicationClass) {
        return this;
    }

    @Override
    public ApplicationFactory init() {
        return this;
    }

    @Override
    public ApplicationFactory config(Class<?> applicationClass) {
        return this;
    }

    @Override
    public ApplicationFactory createApplicationFactory() {
        return this;
    }

    @Override
    public DebbieApplication createApplication() {
        return debbieApplication;
    }
}
