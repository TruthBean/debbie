package com.truthbean.debbie.bean;

import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.proxy.BeanProxyType;

import java.lang.reflect.Proxy;
import java.util.Set;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.5
 */
public interface ProxiedBeanFactory<Bean> extends BeanFactory<Bean> {
    /**
     * if isCreated() and isProxiedBean()
     *   return getCreatedBean();
     * else
     *   factory and proxy
     * @param beanInterface Bean's interface
     * @param applicationContext debbie's applicationContext
     * @return BEAN's proxy
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    default Bean factoryProxiedBean(Class beanInterface, ApplicationContext applicationContext) {
        Bean bean;
        if (!isCreated()) {
            bean = factoryBean(applicationContext);
        } else {
            bean = getCreatedBean();
        }
        if (isCreated() && beanInterface != null && beanInterface.isInterface() && beanInterface.isInstance(bean)) {
            bean = getCreatedBean();
            if (!(bean instanceof Proxy)) {
                Set<BeanLifecycle> beanLifecycles = applicationContext.getBeanLifecycle();
                for (BeanLifecycle beanLifecycle : beanLifecycles) {
                    if (beanLifecycle.support(getBeanClass()) && beanLifecycle.support(this)) {
                        Bean proxy = (Bean) beanLifecycle.doPreCreated(this, bean, beanInterface, BeanProxyType.JDK);
                        if (proxy instanceof Proxy) {
                            return proxy;
                        }
                    }
                }
            }
            return bean;
        }
        return bean;
    }
}
