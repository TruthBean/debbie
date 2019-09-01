package com.truthbean.debbie.task;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.bean.SingletonBeanRegister;

public class DebbieTaskStarter {

    public void start(BeanFactoryHandler beanFactoryHandler) {
        DebbieTaskFactory debbieTaskFactory = new DebbieTaskFactory();
        debbieTaskFactory.setBeanFactoryHandler(beanFactoryHandler);
        debbieTaskFactory.registerTask();
        SingletonBeanRegister register = new SingletonBeanRegister(beanFactoryHandler);
        register.registerSingletonBean(debbieTaskFactory, DebbieTaskFactory.class, "taskFactory");
        beanFactoryHandler.refreshBeans();
    }
}
