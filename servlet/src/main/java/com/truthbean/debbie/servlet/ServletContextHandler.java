/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.servlet;

import com.truthbean.debbie.bean.BeanInitialization;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.mvc.csrf.CsrfFilter;
import com.truthbean.debbie.mvc.filter.*;
import com.truthbean.debbie.mvc.router.MvcRouterRegister;
import com.truthbean.debbie.servlet.filter.RouterFilterWrapper;

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

    private final ApplicationContext applicationContext;
    private final BeanInitialization beanInitialization;

    private final ClassLoader classLoader;

    public ServletContextHandler(ServletContext servletContext, ApplicationContext handler) {
        setServletConfiguration();

        beanInitialization = handler.getBeanInitialization();
        this.applicationContext = handler;

        this.classLoader = this.applicationContext.getClassLoader();

        handleServletContext(servletContext);
    }

    private ServletConfiguration servletConfiguration;

    private void setServletConfiguration() {
        if (ServletProperties.isPropertiesEmpty()) {
            // TODO 提供properties无法加载的方案
            servletConfiguration = new ServletConfiguration(classLoader);
        } else {
            servletConfiguration = ServletProperties.toConfiguration(classLoader);
        }
    }

    private void handleServletContext(ServletContext servletContext) {
        // staticResourcesServlet
        StaticResourcesServlet staticResourcesServlet = new StaticResourcesServlet(servletConfiguration, applicationContext);
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
        DispatcherServlet dispatcherServlet = new DispatcherServlet(servletConfiguration, applicationContext);

        // servlet <url-pattern> should start with / or * and cannot contain **
        var dispatcherMapping = servletConfiguration.getDispatcherMapping().replace("**", "*");
        servletContext.addServlet("dispatcherHandler", dispatcherServlet)
                .addMapping(dispatcherMapping);
    }

    public void registerRouter() {
        MvcRouterRegister.registerRouter(servletConfiguration, applicationContext);
    }

    public void registerFilter(ServletContext servletContext) {
        RouterFilterManager.registerFilter(servletConfiguration, beanInitialization);

        // CharacterEncoding
        EnumSet<DispatcherType> dispatcherTypes = EnumSet.of(
                DispatcherType.FORWARD, DispatcherType.INCLUDE, DispatcherType.REQUEST,
                DispatcherType.ASYNC, DispatcherType.ERROR
        );

        var charsetEncodingFilter = new RouterFilterWrapper(
                new CharacterEncodingFilter().setMvcConfiguration(servletConfiguration),
                servletConfiguration.getDefaultContentType());
        servletContext.addFilter("characterEncodingFilter", charsetEncodingFilter)
                .addMappingForUrlPatterns(dispatcherTypes, true, "/*");

        // cors filter
        var corsFilter = new RouterFilterWrapper(new CorsFilter().setMvcConfiguration(servletConfiguration),
                servletConfiguration.getDefaultContentType());
        servletContext.addFilter("corsFilter", corsFilter)
                .addMappingForUrlPatterns(dispatcherTypes, true, "/*");

        // csrf filter
        if (servletConfiguration.isEnableCrsf()) {
            var csrfFilter = new RouterFilterWrapper(new CsrfFilter().setMvcConfiguration(servletConfiguration),
                    servletConfiguration.getDefaultContentType());
            servletContext.addFilter("csrfFilter", csrfFilter)
                    .addMappingForUrlPatterns(dispatcherTypes, true, "/*");
        }

        // security
        if (servletConfiguration.isEnableSecurity()) {
            var securityFilter = new RouterFilterWrapper(new SecurityFilter().setMvcConfiguration(servletConfiguration),
                    servletConfiguration.getDefaultContentType());
            servletContext.addFilter("securityFilter", securityFilter)
                    .addMappingForUrlPatterns(dispatcherTypes, true, "/*");
        }

        Set<RouterFilterInfo> filters = RouterFilterManager.getFilters();
        filters.forEach(filter -> {
            RouterFilterWrapper filterWrapper = new RouterFilterWrapper(filter.getRouterFilterType(), applicationContext,
                    servletConfiguration.getDefaultContentType());
            servletContext.addFilter(filter.getName(), filterWrapper)
                    .addMappingForUrlPatterns(dispatcherTypes, true, filter.getUrlPatterns());
        });
    }
}