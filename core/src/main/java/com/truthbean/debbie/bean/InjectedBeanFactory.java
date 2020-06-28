/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.bean;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-06-23 22:07.
 */
public class InjectedBeanFactory implements BeanFactoryHandlerAware, BeanClosure {
    private BeanFactoryHandler beanFactoryHandler;

    private final Map<DebbieBeanInfo<?>, BeanCreator<?>> prepatations = new LinkedHashMap<>();
    private final Map<DebbieBeanInfo<?>, Object> singletonBean = new LinkedHashMap<>();

    @Override
    public void setBeanFactoryHandler(BeanFactoryHandler beanFactoryHandler) {
        this.beanFactoryHandler = beanFactoryHandler;
    }

    @SuppressWarnings({"unchecked"})
    public <T> T factory(DebbieBeanInfo<T> beanInfo, BeanDependenceProcessor processor) {
        if (singletonBean.containsKey(beanInfo)) {
            return (T) singletonBean.get(beanInfo);
        }
        BeanCreator<T> creator;
        if (prepatations.containsKey(beanInfo)) {
            creator = (BeanCreator<T>) prepatations.get(beanInfo);
        } else
            creator = factoryBeanPreparation(beanInfo, processor, processor.singletonBeanCreatorMap);

        if (creator.isCreated()) {
            return creator.getCreatedBean();
        }

        // TODO 循环递归解决依赖问题，上个版本已经解决了的

        creator.postConstructor();
        creator.postPreparation();
        creator.postCreated();

        ((BeanCreatorImpl<T>) creator).setCreated(true);

        if (beanInfo.isSingleton()) {
            singletonBean.put(beanInfo, creator.create());
        }
        return creator.create();
    }

    @SuppressWarnings({"unchecked"})
    public <T> BeanCreator<T> factoryBeanPreparation(DebbieBeanInfo<T> beanInfo, BeanDependenceProcessor processor,
                                                     Map<DebbieBeanInfo<?>, BeanCreator<?>> singletonBeanCreatorMap) {
        if (singletonBean.containsKey(beanInfo)) {
            T o = (T) singletonBean.get(beanInfo);
            beanInfo.setBean(o);
        }
        if (prepatations.containsKey(beanInfo)) {
            return (BeanCreator<T>) prepatations.get(beanInfo);
        } else {
            BeanCreatorImpl<T> creatorImpl = new BeanCreatorImpl<>(beanInfo, this.beanFactoryHandler);
            if (beanInfo.isSingleton() && beanInfo.isPresent()) {
                creatorImpl.create(beanInfo.getBean());
                creatorImpl.setCreated(true);
                return creatorImpl;
            }
            if (beanInfo.hasBeanFactory()) {
                creatorImpl.create(beanInfo.getBeanFactory().factoryBean());
                creatorImpl.setCreated(true);
                return creatorImpl;
            }
            creatorImpl.setBeanDependenceProcessor(processor);
            creatorImpl.setInjectedBeanFactory(this);
            creatorImpl.createPreparation(singletonBeanCreatorMap);
            this.prepatations.put(beanInfo, creatorImpl);
            return creatorImpl;
        }
    }

    @Override
    public void destroy() {
        prepatations.clear();
        singletonBean.clear();
    }
}
