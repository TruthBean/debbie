/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.bean;

import com.truthbean.Logger;
import com.truthbean.logger.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/06/02 16:37.
 */
public class DebbieBeanInfoFactory {
    private final Set<DebbieBeanInfo<?>> beanServiceInfoSet = Collections.synchronizedSet(new HashSet<>());

    private final BeanInitialization beanInitialization;

    DebbieBeanInfoFactory(BeanInitialization beanInitialization) {
        this.beanInitialization = beanInitialization;
    }

    public void refreshBeans() {
        synchronized (beanServiceInfoSet) {
            // 重新计算hashcode，因为map的key存的是最开始put进去的值的hashcode，但是key更新的话，hashcode并没有更新
            Set<DebbieBeanInfo<?>> copy = new HashSet<>(beanServiceInfoSet);
            beanServiceInfoSet.clear();

            copy.addAll(beanInitialization.getRegisteredBeans());
            var beanServiceInfoList = beanInitialization.getAnnotatedBeans();

            beanServiceInfoList.forEach((i) -> {
                var clazz = i.getClazz();
                if (clazz.isAnnotation()) {
                    @SuppressWarnings("unchecked") var annotation = (Class<? extends Annotation>) clazz;
                    var set = beanInitialization.getAnnotatedClass(annotation);
                    copy.addAll(set);

                } else {
                    var beanFactory = i.getBeanFactory();
                    if (beanFactory != null) {
                        copy.add(i);
                    } else if (clazz.isInterface()) {
                        copy.addAll(beanInitialization.getBeanByInterface(clazz));
                    } else if (Modifier.isAbstract(i.getClazz().getModifiers())) {
                        copy.addAll(beanInitialization.getBeanByAbstractSuper(clazz));
                    } else {
                        copy.add(i);
                    }
                }
            });

            beanServiceInfoSet.addAll(copy);
        }
    }

    public void autoCreateSingletonBeans(GlobalBeanFactory beanFactory) {
        beanServiceInfoSet.forEach(i -> {
            Boolean lazyCreate = i.getLazyCreate();
            if (lazyCreate != null && !lazyCreate && i.getBeanType() == BeanType.SINGLETON) {
                i.setBean(beanFactory.factory(i.getServiceName()));
                beanInitialization.refreshBean(i);
            }
        });
    }

    Set<DebbieBeanInfo<?>> getAutoCreatedBean() {
        Set<DebbieBeanInfo<?>> result = new HashSet<>();
        for (DebbieBeanInfo<?> beanInfo : beanServiceInfoSet) {
            Boolean lazyCreate = beanInfo.getLazyCreate();
            if (lazyCreate != null && !lazyCreate) {
                result.add(beanInfo);
            }
        }
        return result;
    }

    public Set<DebbieBeanInfo<?>> getAllDebbieBeanInfo() {
        return Set.copyOf(beanServiceInfoSet);
    }

    public <T, K extends T> List<DebbieBeanInfo<K>> getBeanInfoList(Class<T> type, boolean require) {
        return getBeanInfoList(type, require, beanServiceInfoSet);
    }

    public <T> DebbieBeanInfo<T> getBeanInfo(String serviceName, Class<T> type, boolean require) {
        try {
            return getBeanInfo(serviceName, type, require, beanServiceInfoSet, true);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return null;
    }

    public <T> DebbieBeanInfo<T> getBeanInfo(String serviceName, Class<T> type, boolean require, boolean throwException) {
        return getBeanInfo(serviceName, type, require, beanServiceInfoSet, throwException);
    }

    public void destroy(DebbieBeanInfo<?> beanInfo) {
        beanServiceInfoSet.remove(beanInfo);
    }

    protected void releaseBeans() {
        destroyBeans(beanServiceInfoSet);

        beanServiceInfoSet.clear();
    }

    synchronized void destroyBeans(Collection<DebbieBeanInfo<?>> beans) {
        if (beans != null && !beans.isEmpty()) {
            for (DebbieBeanInfo<?> bean : beans) {
                LOGGER.trace(() -> "release bean " + bean.getBeanClass() + " with name " + bean.getServiceName());
                bean.release();
            }
        }
    }

    private <T, K extends T> List<DebbieBeanInfo<K>> getBeanInfoList(Class<T> type, boolean require,
                                                                     Set<DebbieBeanInfo<?>> beanInfoSet) {
        List<DebbieBeanInfo<?>> list = new ArrayList<>();

        if (type != null) {
            for (DebbieBeanInfo<?> debbieBeanInfo : beanInfoSet) {
                var flag = type.getName().equals(debbieBeanInfo.getBeanClass().getName())
                        || (debbieBeanInfo.getBeanInterface() != null
                        && type.getName().equals(debbieBeanInfo.getBeanInterface().getName()));
                if (flag) {
                    list.add(debbieBeanInfo);
                }
            }

            if (list.isEmpty()) {
                for (DebbieBeanInfo<?> debbieBeanInfo : beanInfoSet) {
                    var flag = type.isAssignableFrom(debbieBeanInfo.getBeanClass())
                            || (debbieBeanInfo.getBeanInterface() != null
                            && type.isAssignableFrom(debbieBeanInfo.getBeanInterface()));
                    if (flag) {
                        list.add(debbieBeanInfo);
                    }
                }
            }

            if (list.size() == 0) {
                if (require) {
                    throw new NoBeanException(type.getName() + " not found");
                } else {
                    return null;
                }
            }

            List<DebbieBeanInfo<K>> result = new ArrayList<>();
            for (DebbieBeanInfo<?> beanInfo : list) {
                if (type.isAssignableFrom(beanInfo.getBeanClass())) {
                    @SuppressWarnings("unchecked")
                    DebbieBeanInfo<K> ele = (DebbieBeanInfo<K>) beanInfo;
                    if (beanInfo.getBeanType() == BeanType.SINGLETON) {
                        result.add(ele);
                    } else {
                        result.add(ele.copy());
                    }
                }
            }
            return result;
        }

        return null;
    }

    private <T> DebbieBeanInfo<T> getBeanInfo(String serviceName, Class<T> type, boolean require,
                                              Set<DebbieBeanInfo<?>> beanInfoSet, boolean throwException) {
        List<DebbieBeanInfo<?>> list = new ArrayList<>();
        if (serviceName != null && !serviceName.isBlank()) {
            for (DebbieBeanInfo<?> debbieBeanInfo : beanInfoSet) {
                if (debbieBeanInfo.containName(serviceName)) {
                    list.add(debbieBeanInfo);
                }
            }
        }

        if (list.isEmpty()) {
            if (type != null) {
                for (DebbieBeanInfo<?> debbieBeanInfo : beanInfoSet) {
                    var flag = type.getName().equals(debbieBeanInfo.getBeanClass().getName())
                            || (debbieBeanInfo.getBeanInterface() != null
                            && type.getName().equals(debbieBeanInfo.getBeanInterface().getName()));
                    if (flag) {
                        list.add(debbieBeanInfo);
                    }
                }

                if (list.isEmpty()) {
                    for (DebbieBeanInfo<?> debbieBeanInfo : beanInfoSet) {
                        var flag = type.isAssignableFrom(debbieBeanInfo.getBeanClass())
                                || (debbieBeanInfo.getBeanInterface() != null
                                && type.isAssignableFrom(debbieBeanInfo.getBeanInterface()));
                        if (flag) {
                            list.add(debbieBeanInfo);
                        }
                    }
                }
            }

            if (list.size() == 0) {
                if (require) {
                    if ((serviceName == null || serviceName.isBlank()) && type != null) {
                        serviceName = type.getName();
                    }
                    if (throwException)
                        throw new NoBeanException(serviceName + " not found");
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
            throw new OneMoreBeanRegisteredException(serviceName + " must be only one");
        }

        @SuppressWarnings("unchecked") DebbieBeanInfo<T> beanInfo = (DebbieBeanInfo<T>) list.get(0);
        if (type == null || type.isAssignableFrom(beanInfo.getBeanClass())) {
            if (beanInfo.getBeanType() == BeanType.SINGLETON) {
                return beanInfo;
            } else {
                return beanInfo.copy();
            }
        }
        if (throwException)
            throw new NoBeanException("bean " + type + " not found");
        else
            return null;
    }

    public static final Logger LOGGER = LoggerFactory.getLogger(DebbieBeanInfoFactory.class);
}
