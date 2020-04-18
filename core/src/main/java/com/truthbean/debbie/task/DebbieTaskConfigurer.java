package com.truthbean.debbie.task;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.bean.SingletonBeanRegister;

/**
 * @author TruthBean
 * @since 0.0.2
 */
public class DebbieTaskConfigurer {

    public void configure(BeanFactoryHandler beanFactoryHandler) {
        TaskFactory debbieTaskFactory = new TaskFactory();
        SingletonBeanRegister register = new SingletonBeanRegister(beanFactoryHandler);

        debbieTaskFactory.setBeanFactoryHandler(beanFactoryHandler);

        ThreadPooledExecutor factory = new ThreadPooledExecutor();
        register.registerSingletonBean(factory, ThreadPooledExecutor.class, "threadPooledExecutor");

        debbieTaskFactory.registerTask();
        register.registerSingletonBean(debbieTaskFactory, TaskFactory.class, "taskFactory");
        beanFactoryHandler.refreshBeans();
    }
}
