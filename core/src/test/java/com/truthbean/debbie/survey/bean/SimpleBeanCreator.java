package com.truthbean.debbie.survey.bean;

import com.truthbean.debbie.core.ApplicationContext;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SimpleBeanCreator implements BeanCreator {
    private final BeanDefinition definition;
    private final ApplicationContext applicationContext;

    /**
     * key: scope
     * value: bean
     */
    private final ConcurrentMap<String, Object> scopedBean = new ConcurrentHashMap<>();

    public SimpleBeanCreator(ApplicationContext applicationContext, BeanDefinition beanDefinition) {
        this.definition = beanDefinition;
        this.applicationContext = applicationContext;
    }

    @Override
    public BeanDefinition getBeanDefinition() {
        return definition;
    }

    @Override
    public <T> T create(String scope) {
        if (definition.isSingleton()) {
            return createIfAbsent(scope);
        } else if (definition.isNoScope()) {
            return create();
        } else {
            // todo 判断是否在scope内
            return createIfAbsent(scope);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T createIfAbsent(String scope) {
        if (!scopedBean.containsKey(scope)) {
            // todo
            // scopedBean.put(scope, definition.getValue());
            return null;
        } else {
            return (T) scopedBean.get(scope);
        }
    }

    private <T> T create() {
        // todo
        return null;
    }
}
