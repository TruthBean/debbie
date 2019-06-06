package com.truthbean.debbie.mvc.request.filter;

import com.truthbean.debbie.bean.BeanInitialization;
import com.truthbean.debbie.bean.DebbieBeanInfo;
import com.truthbean.debbie.reflection.ClassInfo;
import com.truthbean.debbie.mvc.MvcConfiguration;
import com.truthbean.debbie.mvc.exception.RouterFilterMappingFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class RouterFilterManager {
    private static final Set<RouterFilterInfo> FILTERS = new TreeSet<>();

    public static void registerFilter(MvcConfiguration webConfiguration, BeanInitialization beanInitialization) {
        Set<DebbieBeanInfo> classInfoSet = beanInitialization.getAnnotatedClass(Filter.class);
        for (var classInfo : classInfoSet) {
            registerFilter(classInfo, webConfiguration);
        }
    }

    private static void registerFilter(ClassInfo<? extends RouterFilter> classInfo, MvcConfiguration configuration) {
        var clazz = classInfo.getClazz();

        RouterFilterInfo filterInfo = new RouterFilterInfo();
        filterInfo.setRouterFilterType(clazz);

        var classAnnotations = classInfo.getClassAnnotations();
        Filter filter = (Filter) classAnnotations.get(Filter.class);
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
        if (urlPatterns.length == 0) {
            throw new RouterFilterMappingFormatException("Filter (" + clazz.getName() + ") urlRegex cannot be empty. ");
        }
        for (String pattern: urlPatterns) {
            if (pattern.contains("*")) {
                String newPattern = pattern.replaceAll("//*", "\\w");
                filterInfo.addUrlPattern(Pattern.compile(newPattern));
                filterInfo.addRawUrlPattern(pattern);
            } else {
                filterInfo.addRawUrlPattern(pattern);
            }
        }
        LOGGER.debug("register filter: " + filterInfo);
        FILTERS.add(filterInfo);
    }

    public static Set<RouterFilterInfo> getFilters() {
        return Collections.unmodifiableSet(FILTERS);
    }

    public static List<RouterFilterInfo> getReverseOrderFilters() {
        var filters = getFilters();
        return filters.stream().sorted((o1, o2) -> Integer.compare(o2.getOrder(), o1.getOrder()))
                .collect(Collectors.toList());
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(RouterFilterManager.class);
}
