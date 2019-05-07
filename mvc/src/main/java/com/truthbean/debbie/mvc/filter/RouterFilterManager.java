package com.truthbean.debbie.mvc.filter;

import com.truthbean.debbie.core.bean.BeanInitializationHandler;
import com.truthbean.debbie.core.reflection.ClassInfo;
import com.truthbean.debbie.core.reflection.ReflectionHelper;
import com.truthbean.debbie.mvc.MvcConfiguration;
import com.truthbean.debbie.mvc.exception.RouterFilterMappingFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class RouterFilterManager {
    private static final Set<RouterFilterInfo> FILTERS = new TreeSet<>();

    public static void registerFilter(MvcConfiguration webConfiguration) {
        Set<ClassInfo> classInfoSet = BeanInitializationHandler.getAnnotatedClass(Filter.class);
        for (var classInfo : classInfoSet) {
            registerFilter(classInfo, webConfiguration);
        }
    }

    private static void registerFilter(ClassInfo classInfo, MvcConfiguration configuration) {
        var clazz = classInfo.getClazz();
        RouterFilter routerFilter = (RouterFilter) ReflectionHelper.newInstance(clazz);

        RouterFilterInfo filterInfo = new RouterFilterInfo();
        filterInfo.setRouterFilter(routerFilter);

        var classAnnotations = classInfo.getClassAnnotations();
        Filter filter = (Filter) classAnnotations.get(Filter.class);
        var name = filter.name();
        if (name.isBlank()) {
            filterInfo.setName(clazz.getName());
        } else {
            filterInfo.setName(name);
        }

        filterInfo.setOrder(filter.order());

        var urlRegex = filter.urlRegex();
        if (urlRegex.length == 0) {
            urlRegex = filter.value();
        }
        if (urlRegex.length == 0) {
            throw new RouterFilterMappingFormatException("Filter (" + clazz.getName() + ") urlRegex cannot be empty. ");
        }
        for (String regex: urlRegex) {
            filterInfo.addUrlPattern(Pattern.compile(regex));
            filterInfo.addRawUrlPattern(regex);
        }
        LOGGER.debug("register filter: " + filterInfo);
        FILTERS.add(filterInfo);
    }

    public static Set<RouterFilterInfo> getFilters() {
        return Collections.unmodifiableSet(FILTERS);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(RouterFilterManager.class);
}
