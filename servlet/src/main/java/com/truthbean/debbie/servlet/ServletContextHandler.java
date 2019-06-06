package com.truthbean.debbie.servlet;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.bean.BeanInitialization;
import com.truthbean.debbie.mvc.request.filter.RouterFilterInfo;
import com.truthbean.debbie.mvc.request.filter.RouterFilterManager;
import com.truthbean.debbie.mvc.router.MvcRouterRegister;
import com.truthbean.debbie.servlet.filter.CharacterEncodingFilter;
import com.truthbean.debbie.servlet.filter.CorsFilter;
import com.truthbean.debbie.servlet.filter.RouterFilterWrapper;
import com.truthbean.debbie.servlet.filter.csrf.CsrfFilter;

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

    private final BeanFactoryHandler beanFactoryHandler;
    private final BeanInitialization beanInitialization;

    public ServletContextHandler(ServletContext servletContext, final Set<Class<?>> classes, BeanFactoryHandler handler) {
        setServletConfiguration();

        servletConfiguration.addScanClasses(classes);
        Set<Class<?>> beanClasses = servletConfiguration.getTargetClasses();
        beanInitialization = handler.getBeanInitialization();
        beanInitialization.init(beanClasses);
        handler.refreshBeans();
        this.beanFactoryHandler = handler;

        handleServletContext(servletContext);
    }

    private ServletConfiguration servletConfiguration;

    private void setServletConfiguration() {
        if (ServletProperties.isPropertiesEmpty()) {
            // TODO 提供properties无法加载的方案

        } else {
            servletConfiguration = ServletProperties.toConfiguration();
        }
    }

    private void handleServletContext(ServletContext servletContext) {
        DispatcherServlet dispatcherServlet = new DispatcherServlet(servletConfiguration, beanFactoryHandler);

        // servlet <url-pattern> should start with / or * and cannot contain **
        var dispatcherMapping = servletConfiguration.getDispatcherMapping().replace("**", "*");
        servletContext.addServlet("dispatcherHandler", dispatcherServlet)
                .addMapping(dispatcherMapping);
    }

    public void registerRouter() {
        MvcRouterRegister.registerRouter(servletConfiguration, beanInitialization);
    }

    public void registerFilter(ServletContext servletContext) {
        RouterFilterManager.registerFilter(servletConfiguration, beanInitialization);

        // CharacterEncoding
        EnumSet<DispatcherType> dispatcherTypes = EnumSet.of(
                DispatcherType.FORWARD, DispatcherType.INCLUDE, DispatcherType.REQUEST,
                DispatcherType.ASYNC, DispatcherType.ERROR
        );

        servletContext.addFilter("characterEncodingFilter", new CharacterEncodingFilter())
                .addMappingForUrlPatterns(dispatcherTypes, true, "/*");

        // cors filter
        if (servletConfiguration.isEnableCors()) {
            servletContext.addFilter("corsFilter", new CorsFilter(servletConfiguration))
                    .addMappingForUrlPatterns(dispatcherTypes, true, "/*");
        }

        // csrf filter
        if (servletConfiguration.isEnableCrsf()) {
            servletContext.addFilter("csrfFilter", new CsrfFilter(servletConfiguration))
                    .addMappingForUrlPatterns(dispatcherTypes, true, "/*");
        }

        Set<RouterFilterInfo> filters = RouterFilterManager.getFilters();
        filters.forEach(filter -> {
            RouterFilterWrapper filterWrapper = new RouterFilterWrapper(filter.getRouterFilterType(), beanFactoryHandler);
            servletContext.addFilter(filter.getName(), filterWrapper)
                    .addMappingForUrlPatterns(dispatcherTypes, true, filter.getUrlPatterns());
        });
    }
}