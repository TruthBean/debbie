package com.truthbean.code.debbie.servlet;

import com.truthbean.code.debbie.core.bean.BeanInitializationHandler;
import com.truthbean.code.debbie.mvc.router.MvcRouterRegister;
import com.truthbean.code.debbie.servlet.filter.CharacterEncodingFilter;
import com.truthbean.code.debbie.servlet.filter.CorsFilter;

import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import java.util.EnumSet;
import java.util.Set;

/**
 * @author TruthBean
 * @since 0.0.1
 * @since 2018-02-18 22:39
 */
public class ServletContextHandler {

    private ServletContextHandler(Set<Class<?>> beans) {
        BeanInitializationHandler.init(beans);
    }

    private static ServletConfiguration servletConfiguration;

    private static void setServletConfiguration() {
        if (ServletProperties.isPropertiesEmpty()) {
            // TODO 提供properties无法加载的方案

        } else {
            servletConfiguration = ServletProperties.loadProperties();
        }
    }

    private static Set<Class<?>> handleServletContextAndScanClasses
            (ServletContext servletContext, Set<Class<?>> classes) {

        DispatcherServlet dispatcherServlet = new DispatcherServlet(servletConfiguration);
        servletContext.addServlet("dispatcherHandler", dispatcherServlet)
                .addMapping(servletConfiguration.getDispatcherMapping());

        // CharacterEncoding
        EnumSet<DispatcherType> dispatcherTypes = EnumSet.of(
                DispatcherType.FORWARD, DispatcherType.INCLUDE, DispatcherType.REQUEST,
                DispatcherType.ASYNC, DispatcherType.ERROR
        );
        servletContext.addFilter("characterEncodingFilter", new CharacterEncodingFilter())
                .addMappingForUrlPatterns(dispatcherTypes, true, "/*");

        // cors filter

        // TODO: it not work??
        servletContext.addFilter("corsFilter", new CorsFilter(servletConfiguration))
                .addMappingForUrlPatterns(dispatcherTypes, true, "/*");

        servletConfiguration.addScanClasses(classes);
        return servletConfiguration.getTargetClasses();
    }

    public static ServletContextHandler loadPropertiesAndHandle(ServletContext servletContext, Set<Class<?>> classes) {
        setServletConfiguration();
        return new ServletContextHandler(handleServletContextAndScanClasses(servletContext, classes));
    }

    public void registerRouter() {
        MvcRouterRegister.registerRouter(servletConfiguration);
    }
}