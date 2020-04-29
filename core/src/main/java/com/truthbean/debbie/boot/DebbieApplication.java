package com.truthbean.debbie.boot;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import org.slf4j.Logger;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public interface DebbieApplication {

    String SHUTDOWN_HOOK_THREAD_NAME = "SpringContextShutdownHook";

    void start(String... args);

    void exit(String... args);
}
