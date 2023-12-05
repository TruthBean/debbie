/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.bean;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.core.ApplicationContext;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.util.HashSet;
import java.util.Set;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.0
 * Created on 2021-02-24 23:05
 */
public class PhantomReferenceBeanFactory<T> implements BeanFactory<T> {

    private final PhantomReference<T> reference;
    private final Class<?> beanType;

    private final ReferenceQueue<T> queue;
    private final Set<String> names = new HashSet<>();

    public PhantomReferenceBeanFactory(T bean) {
        this.beanType = bean.getClass();
        this.queue = new ReferenceQueue<>();
        this.reference = new PhantomReference<>(bean, queue);
        names.add(bean.getClass().getName());

        new Thread(() -> {
            while (true) {
                if (queue != null) {
                    PhantomReference<T> reference = null;
                    try {
                        reference = (PhantomReference<T>) queue.remove();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (reference != null) {
                        LOGGER.info("bean(" + beanType + ") did gc");
                    }
                }
            }
        }).start();
    }

    @Override
    public Set<String> getAllName() {
        return names;
    }

    @Override
    public void destruct(ApplicationContext applicationContext) {
        reference.clear();
        names.clear();
        try {
            queue.remove(5000);
        } catch (InterruptedException e) {
            LOGGER.error("", e);
        }
    }

    @Override
    public T factoryBean(ApplicationContext applicationContext) {
        return reference.get();
    }

    @Override
    public boolean isCreated() {
        return false;
    }

    @Override
    public T getCreatedBean() {
        if (reference != null) {
            return reference.get();
        }
        return null;
    }

    @Override
    public Class<?> getBeanClass() {
        return beanType;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(PhantomReferenceBeanFactory.class);
}
