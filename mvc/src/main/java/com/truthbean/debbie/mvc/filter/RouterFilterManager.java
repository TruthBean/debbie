/**
 * Copyright (c) 2024 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mvc.filter;

import com.truthbean.debbie.bean.BeanInfo;
import com.truthbean.debbie.bean.ClassBeanInfo;
import com.truthbean.debbie.bean.BeanInfoManager;
import com.truthbean.debbie.mvc.MvcConfiguration;
import com.truthbean.debbie.mvc.csrf.CsrfFilter;
import com.truthbean.debbie.mvc.exception.RouterFilterMappingFormatException;
import com.truthbean.debbie.reflection.ClassInfo;
import com.truthbean.Logger;
import com.truthbean.LoggerFactory;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class RouterFilterManager {
    private static final Set<RouterFilterInfo> FILTERS = new TreeSet<>();

    private static final Pattern ALL = Pattern.compile("\\w");

    public static void registerFilter(MvcConfiguration webConfiguration, BeanInfoManager beanInfoManager) {
        Set<ClassBeanInfo<? extends RouterFilter>> classInfoSet = change(beanInfoManager.getAnnotatedClass(Filter.class));
        for (var classInfo : classInfoSet) {
            registerFilter(classInfo, webConfiguration);
        }
    }

    public static void registerFilter(RouterFilterInfo filterInfo, String... urlPatterns) {
        setFilterUrlPatterns(filterInfo, urlPatterns);

        LOGGER.debug(() -> "register filter: " + filterInfo);
        FILTERS.add(filterInfo);
    }

    public static void registerCharacterEncodingFilter(MvcConfiguration configuration, String... urlPatterns) {
        RouterFilterInfo info = new RouterFilterInfo();
        info.setConfiguration(configuration);
        info.setRouterFilterType(CharacterEncodingFilter.class);
        info.setFilterInstance(new CharacterEncodingFilter().setMvcConfiguration(configuration));
        info.setName("characterEncodingFilter");
        info.setOrder(-5);
        registerFilter(info, urlPatterns);
    }

    public static void registerCorsFilter(MvcConfiguration configuration, String... urlPatterns) {
        RouterFilterInfo info = new RouterFilterInfo();
        info.setConfiguration(configuration);
        info.setRouterFilterType(CorsFilter.class);
        info.setFilterInstance(new CorsFilter().setMvcConfiguration(configuration));
        info.setName("corsFilter");
        info.setOrder(-4);
        registerFilter(info, urlPatterns);
    }

    public static void registerSecurityFilter(MvcConfiguration configuration, String... urlPatterns) {
        RouterFilterInfo info = new RouterFilterInfo();
        info.setConfiguration(configuration);
        info.setRouterFilterType(SecurityFilter.class);
        info.setFilterInstance(new SecurityFilter().setMvcConfiguration(configuration));
        info.setName("securityFilter");
        info.setOrder(-3);
        registerFilter(info, urlPatterns);
    }

    public static void registerCsrfFilter(MvcConfiguration configuration, String... urlPatterns) {
        RouterFilterInfo info = new RouterFilterInfo();
        info.setConfiguration(configuration);
        info.setRouterFilterType(CsrfFilter.class);
        info.setFilterInstance(new CsrfFilter().setMvcConfiguration(configuration));
        info.setName("csrfFilter");
        info.setOrder(-2);
        registerFilter(info, urlPatterns);
    }

    @SuppressWarnings("unchecked")
    private static Set<ClassBeanInfo<? extends RouterFilter>> change(Set<BeanInfo<?>> raw) {
        Set<ClassBeanInfo<? extends RouterFilter>> result = new LinkedHashSet<>();
        if (raw != null && !raw.isEmpty()) {
            for (var beanInfo : raw) {
                Class<?> beanClass = beanInfo.getBeanClass();
                if (RouterFilter.class.isAssignableFrom(beanClass) && beanInfo instanceof ClassBeanInfo) {
                    result.add((ClassBeanInfo<? extends RouterFilter>) beanInfo);
                }
            }
        }
        return result;
    }

    private static void registerFilter(ClassInfo<? extends RouterFilter> classInfo, MvcConfiguration configuration) {
        var clazz = classInfo.getClazz();

        RouterFilterInfo filterInfo = new RouterFilterInfo();
        filterInfo.setRouterFilterType(clazz);
        filterInfo.setConfiguration(configuration);

        var classAnnotations = classInfo.getClassAnnotations();
        Filter filter = (Filter) classAnnotations.get(Filter.class).getOrigin();
        var name = filter.name();
        if (name.isBlank()) {
            filterInfo.setName(clazz.getName());
        } else {
            filterInfo.setName(name);
        }

        filterInfo.setOrder(filter.order());

        var urlPatterns = filter.urlPatterns();
        if (urlPatterns.length == 0) {
            urlPatterns = filter.value();
        }
        setFilterUrlPatterns(filterInfo, urlPatterns);

        LOGGER.debug(() -> "register filter: " + filterInfo);
        FILTERS.add(filterInfo);
    }

    private static void setFilterUrlPatterns(RouterFilterInfo filterInfo, String[] urlPatterns) {
        if (urlPatterns.length == 0) {
            throw new RouterFilterMappingFormatException("Filter (" + filterInfo.getRouterFilterType().getName() + ") urlRegex cannot be empty. ");
        }
        for (String pattern: urlPatterns) {
            if (pattern.contains("*")) {
                if ("/*".equals(pattern) || "/**".equals(pattern)) {
                    filterInfo.addUrlPattern(ALL);
                } else {
                    String newPattern = pattern.replaceAll("\\*", "\\w");
                    filterInfo.addUrlPattern(Pattern.compile(newPattern));
                }
                filterInfo.addRawUrlPattern(pattern);
            } else {
                filterInfo.addRawUrlPattern(pattern);
            }
        }
    }

    public static Set<RouterFilterInfo> getFilters() {
        return Collections.unmodifiableSet(FILTERS);
    }

    public static List<RouterFilterInfo> getReverseOrderFilters() {
        var filters = getFilters();
        return filters.stream().sorted((o1, o2) -> Integer.compare(o2.getOrder(), o1.getOrder()))
                .collect(Collectors.toList());
    }

    public static void reset() {
        FILTERS.clear();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(RouterFilterManager.class);
}
