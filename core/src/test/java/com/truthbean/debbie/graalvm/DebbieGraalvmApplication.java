package com.truthbean.debbie.graalvm;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.bean.*;
import com.truthbean.debbie.bean.inter.*;
import com.truthbean.debbie.boot.DebbieApplication;
import com.truthbean.debbie.boot.DebbieBootApplication;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.core.ApplicationFactory;
import com.truthbean.debbie.graalvm.controller.AccountController;
import com.truthbean.debbie.graalvm.listener.DebbieReadyEventListener;
import com.truthbean.debbie.graalvm.reflect.MixComponent;
import com.truthbean.debbie.graalvm.repository.AccountRepository;
import com.truthbean.debbie.graalvm.service.AccountService;
import com.truthbean.debbie.graalvm.service.AccountServiceImpl;
import com.truthbean.debbie.graalvm.service.LogService;
import com.truthbean.debbie.jdbc.entity.EntityResolver;
import com.truthbean.debbie.task.DebbieTaskConfig;
import com.truthbean.debbie.task.TaskInfo;
import com.truthbean.debbie.task.TaskRegister;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.logging.log4j.LogManager;

/**
 * @author TruthBean
 * @since 0.5.3
 */
@DebbieBootApplication(scan = @DebbieScan(basePackages = "com.truthbean.debbie"))
public class DebbieGraalvmApplication {

    @BeanInject(require = false)
    private LogService logService;

    public static void main(String[] args) {
        testJcl();
        testJdk();
        testTruthBean();
        testSlf4j();
        testLog4j2();

        testDebbie(args);
    }

    static void testDebbie(String[] args) {
        EntityResolver entityResolver = EntityResolver.getInstance();

        AccountRepository accountRepository = new AccountRepository(entityResolver);

        ApplicationEntrypoint entrypoint = new ApplicationEntrypoint();

        DebbieApplication factory = ApplicationFactory.newEmpty()
                .preInit(DebbieGraalvmApplication.class, args)
                .init()
                .register(new SimpleBeanFactory<>(entrypoint, ApplicationEntrypoint.class, "applicationEntrypoint"))
                .register(new SimpleBeanFactory<>(new AccountServiceImpl(), AccountService.class))
                .register(new SimpleBeanFactory<>(new LogService()))
                .register(new SimpleBeanFactory<>(new DebbieReadyEventListener()))
                .register(new SimpleBeanFactory<>(new AccountController(), AccountController.class))
                .register(new SimpleBeanFactory<>(accountRepository, AccountRepository.class))
                .config()
                .create()
                .postCreate()
                .build()
                .factory();
        testBean(factory.getApplicationContext(), entrypoint);
        factory.start();
        // testFactoryBean(factory.getApplicationContext());
        // while (true);
        // factory.exit();
        ;
    }

    static void testBean(final ApplicationContext applicationContext, final ApplicationEntrypoint entrypoint) {
        TaskRegister taskRegister = applicationContext.factory(TaskRegister.class);
        taskRegister.registerTask(new TaskInfo(context -> entrypoint.printId(), new DebbieTaskConfig()));
    }

    static void testFactoryBean(final ApplicationContext applicationContext) {
        DemoBeanComponent demoBeanComponent = applicationContext.factory(DemoBeanComponent.class);
        System.out.println(demoBeanComponent.getDemo1());
        System.out.println(demoBeanComponent.getDemo2());
        System.out.println(demoBeanComponent);

        Abc abc = applicationContext.factory(AbcImpl.class);
        System.out.println(abc);

        MixComponent mixComponent = applicationContext.factory(MixComponent.class);
        System.out.println(mixComponent);
    }

    static void testLog4j2() {
        org.apache.logging.log4j.Logger logger = LogManager.getLogger(DebbieGraalvmApplication.class);
        logger.info("log4j2....");
    }

    static void testJdk() {
        java.util.logging.Logger logger = java.util.logging.Logger.getLogger(DebbieGraalvmApplication.class.getName());
        logger.info("jul");
    }

    static void testJcl() {
        Log log = LogFactory.getLog(DebbieGraalvmApplication.class);
        log.info("jcl");
    }

    static void testSlf4j() {
        org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(DebbieGraalvmApplication.class);
        logger.info("slf4j");
        logger.debug("slf4j");
        logger.trace("slf4j");
    }

    static void testTruthBean() {
        logger.info("truthbean");
        java.util.logging.Logger julLogger = java.util.logging.Logger.getLogger(DebbieGraalvmApplication.class.getName());
        julLogger.info("jul");
    }

    private static final Logger logger = LoggerFactory.getLogger(DebbieGraalvmApplication.class);
}
