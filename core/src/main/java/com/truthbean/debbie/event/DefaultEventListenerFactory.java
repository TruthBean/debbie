package com.truthbean.debbie.event;

import java.lang.reflect.Method;

public class DefaultEventListenerFactory implements EventListenerFactory {

    private int order = Integer.MAX_VALUE;


    public void setOrder(int order) {
        this.order = order;
    }

    public int getOrder() {
        return this.order;
    }


    public boolean supportsMethod(Method method) {
        return true;
    }

    @Override
    public DebbieEventListener<?> createEventListener(String beanName, Class<?> type, Method method) {
        return null;
    }
}
