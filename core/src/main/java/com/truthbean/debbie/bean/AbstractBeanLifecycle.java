/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * <a href="http://license.coscl.org.cn/MulanPSL2">http://license.coscl.org.cn/MulanPSL2</a>
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.bean;

import com.truthbean.Logger;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.core.ApplicationContextAware;
import com.truthbean.debbie.environment.EnvironmentAware;
import com.truthbean.debbie.environment.EnvironmentDepositoryHolder;
import com.truthbean.debbie.environment.EnvironmentHolderAware;
import com.truthbean.debbie.event.DebbieEventPublisherAware;

import static com.truthbean.debbie.environment.EnvironmentDepositoryHolder.DEFAULT_PROFILE;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.2.0
 * Created on 2020-12-14 15:07
 */
public abstract class AbstractBeanLifecycle implements BeanLifecycle {
    /**
     * invoke ConstructPost#postConstruct method by target bean
     * @param bean target bean
     */
    protected void doConstructPost(Object bean) {
        if (bean instanceof ConstructPost) {
            ((ConstructPost) bean).postConstruct();
        }
    }

    /**
     * invoke aware set method by target bean
     * @param applicationContext application context
     * @param object target bean
     */
    protected void resolveAwareValue(ApplicationContext applicationContext, Object object) {
        if (applicationContext.isExiting()) {
            return;
        }
        if (object instanceof ClassLoaderAware) {
            ((ClassLoaderAware) object).setClassLoader(applicationContext.getClassLoader());
        } else if (object instanceof ApplicationContextAware) {
            ((ApplicationContextAware) object).setApplicationContext(applicationContext);
        } else if (object instanceof GlobalBeanFactoryAware) {
            ((GlobalBeanFactoryAware) object).setGlobalBeanFactory(applicationContext.getGlobalBeanFactory());
        } else if (object instanceof DebbieEventPublisherAware) {
            ((DebbieEventPublisherAware) object).setEventPublisher(applicationContext);
        } else {
            EnvironmentDepositoryHolder environmentHolder = applicationContext.getEnvironmentHolder();
            if (object instanceof EnvironmentHolderAware) {
                ((EnvironmentHolderAware) object).setEnvironmentHolder(environmentHolder);
            } else if (object instanceof EnvironmentAware) {
                ((EnvironmentAware) object).setEnvironment(environmentHolder.getEnvironmentIfPresent(environmentHolder.getDefaultProfile()));
            }
        }
    }

    /**
     * invoke CreatePost#postCreate method by target bean
     * @param bean target bean
     */
    protected void doCreatedPost(Object bean) {
        if (bean instanceof CreatePost) {
            ((CreatePost) bean).postCreate();
        }
    }

    protected abstract Logger getLogger();
}
