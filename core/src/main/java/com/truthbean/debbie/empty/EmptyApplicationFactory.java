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
    private final ApplicationContext applicationContext = new EmptyApplicationContext();

    @Override
    public ApplicationFactory preInit(String... args) {
        return this;
    }

    @Override
    public ApplicationFactory registerModuleStarter(DebbieModuleStarter moduleStarter) {
        return this;
    }

    @Override
    public ApplicationFactory init() {
        return this;
    }

    @Override
    public ApplicationFactory init(Class<?> applicationClass) {
        return this;
    }

    @Override
    public ApplicationFactory init(ClassLoader classLoader) {
        return this;
    }

    @Override
    public ApplicationFactory init(Class<?> applicationClass, ClassLoader classLoader) {
        return this;
    }

    @Override
    public ApplicationFactory config() {
        return this;
    }

    @Override
    public ApplicationFactory config(BeanScanConfiguration configuration) {
        return this;
    }

    @Override
    public ApplicationFactory create() {
        return this;
    }

    @Override
    public ApplicationFactory postCreate() {
        return this;
    }

    @Override
    public ApplicationFactory build() {
        return this;
    }

    @Override
    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public DebbieApplication factory() {
        return debbieApplication;
    }

    @Override
    public void release() {

    }
}
