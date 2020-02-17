package com.truthbean.debbie.event;

import java.lang.reflect.Method;

/**
 * @author truthbean
 * @since 0.0.2
 */
public class DefaultEventListenerFactory implements EventListenerFactory {

    private int order = Integer.MAX_VALUE;


    public void setOrder(int order) {
        this.order = order;
    }

    public int getOrder() {
        return this.order;
    }


    @Override
    public boolean supportsMethod(Method method) {
        return true;
    }

    @Override
    public DebbieEventListener<?> createEventListener(String beanName, Class<?> type, Method method) {
        return null;
    }
}
