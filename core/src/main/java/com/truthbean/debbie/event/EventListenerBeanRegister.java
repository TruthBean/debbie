/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.event;

import com.truthbean.debbie.bean.*;
import com.truthbean.debbie.concurrent.ThreadPooledExecutor;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

/**
 * @author TruthBean
 * @since 0.0.2
 */
public class EventListenerBeanRegister {

    private final DebbieApplicationContext applicationContext;

    public EventListenerBeanRegister(DebbieApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @SuppressWarnings("unchecked")
    public void register() {
        BeanInitialization beanInitialization = applicationContext.getBeanInitialization();
        GlobalBeanFactory globalBeanFactory = applicationContext.getGlobalBeanFactory();
        ThreadPooledExecutor executor = globalBeanFactory.factory("threadPooledExecutor");
        DefaultEventPublisher eventPublisher = new DefaultEventPublisher(executor);
        // 先注册系统相关的
        SingletonBeanRegister register = new SingletonBeanRegister(applicationContext);
        register.registerSingletonBean(eventPublisher, DebbieEventPublisher.class, "eventPublisher");
        // 然后处理用户自定义的
        Set<DebbieBeanInfo<?>> classInfoSet = beanInitialization.getAnnotatedClass(EventBeanListener.class);
        for (DebbieBeanInfo debbieBeanInfo : classInfoSet) {
            Class<?> beanType = debbieBeanInfo.getBeanClass();
            if (DebbieEventListener.class.isAssignableFrom(beanType)) {
                List<Type> actualTypes = debbieBeanInfo.getActualTypes();
                BeanFactory<DebbieEventListener<? extends AbstractDebbieEvent>> listenerBeanFactory = new DebbieBeanFactory<>(debbieBeanInfo);
                listenerBeanFactory.setGlobalBeanFactory(globalBeanFactory);
                eventPublisher.addEventListener((Class<? extends AbstractDebbieEvent>) actualTypes.get(0), listenerBeanFactory);
            }
        }
        // 处理 EventMethodListener
        Set<DebbieBeanInfo<?>> beanInfoList = beanInitialization.getAnnotatedMethodBean(EventMethodListener.class);
        if (beanInfoList != null && beanInfoList.isEmpty()) {
            for (DebbieBeanInfo debbieBeanInfo : beanInfoList) {
                Class<?> beanType = debbieBeanInfo.getBeanClass();
                Object bean = globalBeanFactory.factory(beanType);
                Set<Method> methods = debbieBeanInfo.getMethods();
                for (Method method : methods) {
                    EventMethodListener annotation = method.getAnnotation(EventMethodListener.class);
                    if (annotation != null) {
                        int count = method.getParameterCount();
                        if (count == 1) {
                            Parameter parameter = method.getParameters()[0];
                            Class<?> type = parameter.getType();
                            if (AbstractDebbieEvent.class.isAssignableFrom(type)) {
                                var listener = new EventMethodListenerFactory(bean, type, method);
                                listener.setAsync(annotation.async());

                                @SuppressWarnings("unchecked")
                                DebbieBeanInfo listenerBeanInfo = new DebbieBeanInfo<>(EventMethodListenerFactory.class);
                                listenerBeanInfo.setBean(listener);

                                DebbieBeanFactory<? extends DebbieEventListener<? extends AbstractDebbieEvent>> listenerBeanFactory = new DebbieBeanFactory<>();
                                listenerBeanFactory.setGlobalBeanFactory(globalBeanFactory);
                                listenerBeanFactory.setBeanInfo(listenerBeanInfo);

                                eventPublisher.addEventListener((Class<? extends AbstractDebbieEvent>) type, listenerBeanFactory);
                            }
                        }
                    }
                }
            }
        }
    }
}
