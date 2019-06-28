package com.truthbean.debbie.servlet;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.bean.BeanInitialization;
import com.truthbean.debbie.mvc.filter.CharacterEncodingFilter;
import com.truthbean.debbie.mvc.filter.CorsFilter;
import com.truthbean.debbie.mvc.filter.RouterFilterInfo;
import com.truthbean.debbie.mvc.filter.RouterFilterManager;
import com.truthbean.debbie.mvc.router.MvcRouterRegister;
import com.truthbean.debbie.servlet.filter.RouterFilterWrapper;
import com.truthbean.debbie.mvc.csrf.CsrfFilter;

import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * @author TruthBean
 * @since 0.0.1
 * @since 2018-02-18 22:39
 */
public class ServletContextHandler {

    private final BeanFactoryHandler beanFactoryHandler;
    private final BeanInitialization beanInitialization;

    public ServletContextHandler(ServletContext servletContext, BeanFactoryHandler handler) {
        setServletConfiguration();

        beanInitialization = handler.getBeanInitialization();
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
        // staticResourcesServlet
        StaticResourcesServlet staticResourcesServlet = new StaticResourcesServlet(servletConfiguration, beanFactoryHandler);
        // servlet <url-pattern> should start with / or * and cannot contain **
        Map<String, String> staticResourcesMapping = servletConfiguration.getStaticResourcesMapping();
        String[] staticResourcesMappings = new String[staticResourcesMapping.size()];
        String[] keySet = staticResourcesMapping.keySet().toArray(new String[0]);
        for (int i = 0, length = keySet.length; i < length; i++) {
            staticResourcesMappings[i] = keySet[i].replace("**", "*");
        }
        servletContext.addServlet("staticResourcesServlet", staticResourcesServlet)
                .addMapping(staticResourcesMappings);

        // dispatcherServlet
        DispatcherServlet dispatcherServlet = new DispatcherServlet(servletConfiguration, beanFactoryHandler);

        // servlet <url-pattern> should start with / or * and cannot contain **
        var dispatcherMapping = servletConfiguration.getDispatcherMapping().replace("**", "*");
        servletContext.addServlet("dispatcherHandler", dispatcherServlet)
                .addMapping(dispatcherMapping);
    }

    public void registerRouter() {
        MvcRouterRegister.registerRouter(servletConfiguration, beanFactoryHandler);
    }

    public void registerFilter(ServletContext servletContext) {
        RouterFilterManager.registerFilter(servletConfiguration, beanInitialization);

        // CharacterEncoding
        EnumSet<DispatcherType> dispatcherTypes = EnumSet.of(
                DispatcherType.FORWARD, DispatcherType.INCLUDE, DispatcherType.REQUEST,
                DispatcherType.ASYNC, DispatcherType.ERROR
        );

        var charsetEncodingFilter = new RouterFilterWrapper(new CharacterEncodingFilter().setMvcConfiguration(servletConfiguration));
        servletContext.addFilter("characterEncodingFilter", charsetEncodingFilter)
                .addMappingForUrlPatterns(dispatcherTypes, true, "/*");

        // cors filter
        if (servletConfiguration.isEnableCors()) {
            var corsFilter = new RouterFilterWrapper(new CorsFilter().setMvcConfiguration(servletConfiguration));
            servletContext.addFilter("corsFilter", corsFilter)
                    .addMappingForUrlPatterns(dispatcherTypes, true, "/*");
        }

        // csrf filter
        if (servletConfiguration.isEnableCrsf()) {
            var csrfFilter = new RouterFilterWrapper(new CsrfFilter().setMvcConfiguration(servletConfiguration));
            servletContext.addFilter("csrfFilter", csrfFilter)
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