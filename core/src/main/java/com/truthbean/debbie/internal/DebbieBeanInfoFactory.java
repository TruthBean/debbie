/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.internal;

import com.truthbean.Logger;
import com.truthbean.debbie.bean.*;
import com.truthbean.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/06/02 16:37.
 */
class DebbieBeanInfoFactory implements BeanInfoFactory {
    public static final Object value = new Object();
    private final Map<BeanInfo<?>, Object> beanServiceInfoSet = new ConcurrentHashMap<>();

    private final BeanInitialization beanInitialization;

    DebbieBeanInfoFactory(final BeanInitialization beanInitialization) {
        this.beanInitialization = beanInitialization;
    }

    @Override
    public BeanInfoFactory refreshBeans() {
        // 重新计算hashcode，因为map的key存的是最开始put进去的值的hashcode，但是key更新的话，hashcode并没有更新
        Map<BeanInfo<?>, Object> copy = new ConcurrentHashMap<>(beanServiceInfoSet);
        beanServiceInfoSet.clear();

        var registeredBeans = beanInitialization.getRegisteredBeans();
        for (BeanInfo<?> registeredBean : registeredBeans) {
            copy.put(registeredBean, value);
        }

        var beanServiceInfoList = beanInitialization.getAnnotatedBeans();

        Set<Class<? extends Annotation>> beanAnnotations = new CopyOnWriteArraySet<>(beanInitialization.getBeanAnnotations());
        beanAnnotations.forEach((annotationType) -> {
            var annotatedClass = beanInitialization.getAnnotatedClass(annotationType);
            for (DebbieClassBeanInfo<?> beanInfo : annotatedClass) {
                copy.put(beanInfo, value);
            }
        });

        beanServiceInfoList.forEach((i) -> {
            var clazz = i.getClazz();
            if (clazz.isAnnotation()) {
                @SuppressWarnings("unchecked") var annotation = (Class<? extends Annotation>) clazz;
                var set = beanInitialization.getAnnotatedClass(annotation);
                for (DebbieClassBeanInfo<?> beanInfo : set) {
                    copy.put(beanInfo, value);
                }

            } else {
                var beanFactory = i.getBeanFactory();
                if (beanFactory != null) {
                    copy.put(i, value);
                } else if (clazz.isInterface()) {
                    var beans = beanInitialization.getBeanByInterface(clazz);
                    for (DebbieClassBeanInfo<?> beanInfo : beans) {
                        copy.put(beanInfo, value);
                    }
                } else if (Modifier.isAbstract(i.getClazz().getModifiers())) {
                    var beans = beanInitialization.getBeanByAbstractSuper(clazz);
                    for (DebbieClassBeanInfo<?> beanInfo : beans) {
                        copy.put(beanInfo, value);
                    }
                } else {
                    copy.put(i, value);
                }
            }
        });
        beanServiceInfoSet.putAll(copy);
        return this;
    }

    @Override
    public void autoCreateSingletonBeans(GlobalBeanFactory beanFactory) {
        beanServiceInfoSet.forEach((i, value) -> {
            boolean lazyCreate = i.isLazyCreate();
            if (!lazyCreate && i.getBeanType() == BeanType.SINGLETON && i instanceof MutableBeanInfo) {
                MutableBeanInfo<?> beanInfo = (MutableBeanInfo<?>) i;
                beanInfo.setBean(() -> beanFactory.factory(i.getServiceName()));
                beanInitialization.refreshBean(beanInfo);
            }
        });
    }

    Set<BeanInfo<?>> getAutoCreatedBean() {
        Set<BeanInfo<?>> result = new HashSet<>();
        for (BeanInfo<?> beanInfo : beanServiceInfoSet.keySet()) {
            boolean lazyCreate = beanInfo.isLazyCreate();
            if (!lazyCreate) {
                result.add(beanInfo);
            }
        }
        return result;
    }

    @Override
    public Set<BeanInfo<?>> getAllDebbieBeanInfo() {
        return Set.copyOf(beanServiceInfoSet.keySet());
    }

    @Override
    public <T, K extends T> List<BeanInfo<K>> getBeanInfoList(Class<T> type, boolean require) {
        return getBeanInfoList(type, require, beanServiceInfoSet.keySet());
    }

    @Override
    public <T> BeanInfo<T> getBeanInfo(String serviceName, Class<T> type, boolean require) {
        try {
            return getBeanInfo(serviceName, type, require, beanServiceInfoSet.keySet(), true);
        } catch (Exception e) {
            if (e instanceof OneMoreBeanRegisteredException) {
                throw e;
            }
            LOGGER.error(e.getMessage());
        }
        return null;
    }

    @Override
    public <T> BeanInfo<T> getBeanInfo(String serviceName, Class<T> type, boolean require, boolean throwException) {
        return getBeanInfo(serviceName, type, require, beanServiceInfoSet.keySet(), throwException);
    }

    @Override
    public void destroy(BeanInfo<?> beanInfo) {
        beanServiceInfoSet.remove(beanInfo);
    }

    protected void releaseBeans() {
        synchronized (DebbieBeanInfoFactory.class) {
            destroyBeans(beanServiceInfoSet.keySet());

            beanServiceInfoSet.clear();
        }
    }

    synchronized void destroyBeans(Collection<BeanInfo<?>> beans) {
        if (beans != null && !beans.isEmpty()) {
            for (BeanInfo<?> bean : beans) {
                LOGGER.trace(() -> "release bean " + bean.getBeanClass() + " with name " + bean.getServiceName());
                bean.release();
            }
        }
    }

    private <T> void getDebbieBeanInfoList(final Class<T> type, final Set<BeanInfo<?>> beanInfoSet,
                                           final List<BeanInfo<?>> list) {
        for (BeanInfo<?> debbieBeanInfo : beanInfoSet) {
            var flag = type.isAssignableFrom(debbieBeanInfo.getBeanClass())
                    || (debbieBeanInfo instanceof DebbieClassBeanInfo
                        && ((DebbieClassBeanInfo)debbieBeanInfo).getBeanInterface() != null
                        && type.isAssignableFrom(((DebbieClassBeanInfo)debbieBeanInfo).getBeanInterface())
                    );
            if (flag) {
                list.add(debbieBeanInfo);
            }
        }

        if (list.isEmpty()) {
            for (BeanInfo<?> debbieBeanInfo : beanInfoSet) {
                var flag = type.isAssignableFrom(debbieBeanInfo.getBeanClass())
                        || (debbieBeanInfo instanceof DebbieClassBeanInfo
                            && ((DebbieClassBeanInfo)debbieBeanInfo).getBeanInterface() != null
                            && type.isAssignableFrom(((DebbieClassBeanInfo)debbieBeanInfo).getBeanInterface())
                        );
                if (flag) {
                    list.add(debbieBeanInfo);
                }
            }
        }
    }

    private <T, K extends T> List<BeanInfo<K>> getBeanInfoList(Class<T> type, boolean require,
                                                                     final Set<BeanInfo<?>> beanInfoSet) {
        List<BeanInfo<?>> list = new ArrayList<>();

        if (type != null) {
            getDebbieBeanInfoList(type, beanInfoSet, list);

            if (list.isEmpty()) {
                if (require) {
                    throw new NoBeanException(type.getName() + " not found");
                } else {
                    return null;
                }
            }

            List<BeanInfo<K>> result = new ArrayList<>();
            for (BeanInfo<?> beanInfo : list) {
                if (type.isAssignableFrom(beanInfo.getBeanClass())) {
                    @SuppressWarnings("unchecked")
                    BeanInfo<K> ele = (BeanInfo<K>) beanInfo;
                    if (beanInfo.getBeanType() == BeanType.SINGLETON) {
                        result.add(ele);
                    } else if (ele instanceof MutableBeanInfo) {
                        result.add(((MutableBeanInfo<K>)ele).copy());
                    }
                }
            }
            return result;
        }

        return null;
    }

    private <T> BeanInfo<T> getBeanInfo(String serviceName, final Class<T> type, boolean require,
                                              final Set<BeanInfo<?>> beanInfoSet, boolean throwException) {
        List<BeanInfo<?>> list = new ArrayList<>();
        if (serviceName != null && !serviceName.isBlank()) {
            for (BeanInfo<?> debbieBeanInfo : beanInfoSet) {
                if (debbieBeanInfo.containName(serviceName)) {
                    if (type != null) {
                        var flag = type.isAssignableFrom(debbieBeanInfo.getBeanClass())
                                || (debbieBeanInfo instanceof DebbieClassBeanInfo
                                    && ((DebbieClassBeanInfo)debbieBeanInfo).getBeanInterface() != null
                                    && type.isAssignableFrom(((DebbieClassBeanInfo)debbieBeanInfo).getBeanInterface())
                                );
                        if (flag) {
                            list.add(debbieBeanInfo);
                        }
                    } else {
                        list.add(debbieBeanInfo);
                    }
                }
            }
        }

        if (list.isEmpty()) {
            if (type != null) {
                getDebbieBeanInfoList(type, beanInfoSet, list);
            }

            if (list.isEmpty()) {
                if (require) {
                    if ((serviceName == null || serviceName.isBlank()) && type != null) {
                        serviceName = type.getName();
                    }
                    if (throwException)
                        throw new NoBeanException("bean(" + serviceName + ") not found");
                    else
                        return null;
                } else {
                    return null;
                }
            }
        }

        if (list.size() > 1) {
            if ((serviceName == null || serviceName.isBlank()) && type != null) {
                serviceName = type.getName();
            }
            List<BeanInfo<?>> copy = new ArrayList<>(list);
            for (BeanInfo<?> next : copy) {
                if (next.getBeanClass() != type || !next.getBeanNames().contains(serviceName)) {
                    list.remove(next);
                }
            }
            if (list.size() != 1 ) {
                throw new OneMoreBeanRegisteredException(serviceName + " must be only one");
            }
        }

        var ele = list.get(0);
        if (type == null || type.isAssignableFrom(ele.getBeanClass())) {
            if (ele.getBeanType() == BeanType.SINGLETON) {
                @SuppressWarnings("unchecked")
                BeanInfo<T> result = (BeanInfo<T>) ele;
                return result;
            } else {
                @SuppressWarnings("unchecked")
                BeanInfo<T> result = (BeanInfo<T>) ele.copy();
                return result;
            }
        }
        if (throwException)
            throw new NoBeanException("bean " + type + " not found");
        else
            return null;
    }

    public static final Logger LOGGER = LoggerFactory.getLogger(DebbieBeanInfoFactory.class);
}
