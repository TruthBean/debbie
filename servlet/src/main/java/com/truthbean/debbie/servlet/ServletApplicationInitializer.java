package com.truthbean.debbie.servlet;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.boot.DebbieApplicationFactory;
import com.truthbean.debbie.watcher.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;
import java.util.Set;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-01-07 22:30.
 */
@HandlesTypes(value = {Watcher.class})
public class ServletApplicationInitializer extends DebbieApplicationFactory implements ServletContainerInitializer {

    private BeanFactoryHandler beanFactoryHandler;

    public ServletApplicationInitializer() {
        super(ServletApplicationInitializer.class);
        if (debbieApplication == null) {
            beanFactoryHandler = getBeanFactoryHandler();
            super.config();
            super.callStarter();
        } else {
            beanFactoryHandler = debbieApplicationFactory;
        }
    }

    @Override
    public void onStartup(Set<Class<?>> classes, ServletContext ctx) throws ServletException {
        LOGGER.info("ServletContainerInitializer onStartup ...");
        var handler = new ServletContextHandler(ctx, beanFactoryHandler);
        handler.registerRouter();
        handler.registerFilter(ctx);

        // if run with war package
        if (debbieApplication == null)
            beanFactoryHandler.autoCreateBeans();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ServletApplicationInitializer.class);
}
