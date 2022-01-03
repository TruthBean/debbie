package com.truthbean.debbie.empty;

import com.truthbean.debbie.bean.*;
import com.truthbean.debbie.boot.DebbieApplication;
import com.truthbean.debbie.boot.DebbieModuleStarter;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.core.ApplicationFactory;

import java.util.Collection;

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
    public ApplicationFactory preInit(Class<?> applicationClass, String... args) {
        return this;
    }

    @Override
    public ApplicationFactory registerModuleStarter(DebbieModuleStarter moduleStarter) {
        return this;
    }

    @Override
    public ApplicationFactory init(Class<?>... beanClasses) {
        return this;
    }

    @Override
    public ApplicationFactory init(ClassLoader classLoader, Class<?>... beanClasses) {
        return this;
    }

    @Override
    public ApplicationFactory register(Collection<BeanInfo<?>> beanInfos) {
        return this;
    }

    @Override
    public ApplicationFactory register(BeanFactory<?> beanFactory) {
        return this;
    }

    @Override
    public ApplicationFactory register(BeanInfo<?> beanInfo) {
        return this;
    }

    @Override
    public ApplicationFactory register(BeanLifecycle beanLifecycle) {
        return this;
    }

    @Override
    public ApplicationFactory register(BeanRegister beanRegister) {
        return null;
    }

    @Override
    public ApplicationFactory config() {
        return this;
    }

    @Override
    public ApplicationFactory config(Object application) {
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
