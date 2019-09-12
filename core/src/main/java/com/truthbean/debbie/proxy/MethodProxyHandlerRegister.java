package com.truthbean.debbie.proxy;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MethodProxyHandlerRegister {

    private Map<Class<? extends Annotation>, List<Class<? extends MethodProxyHandler>>> classListMap;

    public MethodProxyHandlerRegister() {
        classListMap = new LinkedHashMap<>();
    }

    public void register(Class<? extends Annotation> annotation, Class<? extends MethodProxyHandler> methodProxyHandler) {
        List<Class<? extends MethodProxyHandler>> classes;

        boolean exits = classListMap.containsKey(annotation);
        if (exits) {
            classes = classListMap.get(annotation);
            if (classes == null) {
                classes = new ArrayList<>();
            }
        } else {
            classes = new ArrayList<>();
        }

        classes.add(methodProxyHandler);
        classListMap.put(annotation, classes);
    }

    public Map<Class<? extends Annotation>, List<Class<? extends MethodProxyHandler>>> getAllMethodProxyHandlers() {
        return classListMap;
    }

}
