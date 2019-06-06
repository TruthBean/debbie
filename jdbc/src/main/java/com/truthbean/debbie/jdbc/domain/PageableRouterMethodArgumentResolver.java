package com.truthbean.debbie.jdbc.domain;

import com.truthbean.debbie.data.validate.DataValidateFactory;
import com.truthbean.debbie.reflection.ExecutableArgument;
import com.truthbean.debbie.reflection.ExecutableArgumentResolver;
import com.truthbean.debbie.reflection.TypeHelper;

import java.util.List;
import java.util.Map;

/**
 * @author TruthBean
 * @since 0.0.2
 */
public class PageableRouterMethodArgumentResolver implements ExecutableArgumentResolver {
    @Override
    public boolean supportsParameter(ExecutableArgument parameter) {
        var annotation = parameter.getAnnotation(Pageable.class);
        return annotation != null;
    }

    @Override
    public boolean resolveArgument(ExecutableArgument parameter, Object originValues, DataValidateFactory validateFactory) {
        Map<String, List> map = (Map<String, List>) originValues;

        var annotation = parameter.getAnnotation(Pageable.class);
        Pageable pageable = (Pageable) annotation;

        Integer page = null;
        Integer size = null;

        if (map != null && !map.isEmpty()) {
            for (Map.Entry<String, List> entry : map.entrySet()) {
                var key = entry.getKey();
                if ("page".equals(key)) {
                    page = TypeHelper.valueOf(Integer.class, (String) entry.getValue().get(0));
                } else if ("size".equals(key)) {
                    size = TypeHelper.valueOf(Integer.class, (String) entry.getValue().get(0));
                }
            }
        }

        if (page == null) {
            page = pageable.page();
        }

        if (size == null) {
            size = pageable.size();
        }

        parameter.setValue(PageRequest.of(page, size));

        return true;
    }
}
