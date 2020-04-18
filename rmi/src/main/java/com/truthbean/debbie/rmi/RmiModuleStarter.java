package com.truthbean.debbie.rmi;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.bean.BeanInitialization;
import com.truthbean.debbie.boot.DebbieModuleStarter;
import com.truthbean.debbie.properties.DebbieConfigurationFactory;

import java.util.Set;

/**
 * @author truthbean
 * @since 0.0.2
 */
public class RmiModuleStarter implements DebbieModuleStarter {

    private DebbieRmiServiceRegister rmiServiceRegister;

    @Override
    public void registerBean(BeanFactoryHandler beanFactoryHandler, BeanInitialization beanInitialization) {
        rmiServiceRegister = new DebbieRmiServiceRegister(beanInitialization);
        beanInitialization.addAnnotationRegister(rmiServiceRegister);
    }

    @Override
    public void configure(DebbieConfigurationFactory configurationFactory, BeanFactoryHandler beanFactoryHandler) {
        configurationFactory.register(RmiServerProperties.class, RmiServerConfiguration.class);

        RmiServerConfiguration configuration = configurationFactory.factory(RmiServerConfiguration.class, beanFactoryHandler);

        // 注册管理器
        var register = new RemoteServiceRegister(beanFactoryHandler, configuration.getRmiBindAddress(), configuration.getRmiBindPort());
        // bind
        BeanInitialization beanInitialization = beanFactoryHandler.getBeanInitialization();
        Set<Class<?>> rmiServiceMappers = rmiServiceRegister.getRmiServiceMappers();
        for (Class<?> rmiServiceMapper : rmiServiceMappers) {
            register.bind(rmiServiceMapper);
        }
    }

    @Override
    public int getOrder() {
        return 12;
    }

    @Override
    public void release(DebbieConfigurationFactory configurationFactory, BeanFactoryHandler beanFactoryHandler) {
        // todo
    }
}
