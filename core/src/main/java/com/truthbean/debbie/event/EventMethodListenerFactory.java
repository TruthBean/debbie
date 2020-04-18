package com.truthbean.debbie.event;

import com.truthbean.debbie.reflection.ReflectionHelper;

import java.lang.reflect.Method;

/**
 * @author truthbean/Rogar·Q
 * @since 0.0.2
 * Created on 2020-04-02 18:18.
 */
public class EventMethodListenerFactory<EVENT extends AbstractDebbieEvent> implements GenericEventListener<EVENT> {

    private final Class<EVENT> eventType;
    private final Object targetBean;
    private final Method eventMethod;

    private boolean async = false;

    public EventMethodListenerFactory(Object targetBean, Class<EVENT> eventType, Method eventMethod) {
        this.eventType = eventType;
        this.targetBean = targetBean;
        this.eventMethod = eventMethod;
    }

    @Override
    public boolean async() {
        return this.async;
    }

    public void setAsync(boolean async) {
        this.async = async;
    }

    @Override
    public boolean supportsSourceType(Class sourceType) {
        return sourceType == this.eventType;
    }

    @Override
    public void onEvent(EVENT event) {
        ReflectionHelper.invokeMethod(targetBean, eventMethod, event);
    }
}
