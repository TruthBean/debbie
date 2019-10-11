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

    private final DebbieRmiServiceRegister rmiServiceRegister = new DebbieRmiServiceRegister();

    @Override
    public void registerBean(BeanFactoryHandler beanFactoryHandler) {
        DebbieConfigurationFactory configurationFactory = beanFactoryHandler.getConfigurationFactory();
        configurationFactory.register(RmiServerProperties.class);

        BeanInitialization beanInitialization = beanFactoryHandler.getBeanInitialization();
        beanInitialization.addAnnotationRegister(rmiServiceRegister);
    }

    @Override
    public void starter(DebbieConfigurationFactory configurationFactory, BeanFactoryHandler beanFactoryHandler) {
        RmiServerConfiguration configuration = configurationFactory.factory(RmiServerConfiguration.class, beanFactoryHandler);

        // 注册管理器
        var register = new RemoteServiceRegister(beanFactoryHandler, configuration.getRmiBindAddress(), configuration.getRmiBindPort());
        // bind
        BeanInitialization beanInitialization = beanFactoryHandler.getBeanInitialization();
        Set<Class<?>> rmiServiceMappers = rmiServiceRegister.getRmiServiceMappers(beanInitialization);
        for (Class<?> rmiServiceMapper : rmiServiceMappers) {
            register.bind(rmiServiceMapper);
        }

    }
}
