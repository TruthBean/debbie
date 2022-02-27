package com.truthbean.debbie.asm.proxy;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.bean.BeanInfo;
import com.truthbean.debbie.bean.ClassBeanInfo;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.proxy.*;

/**
 * @author TruthBean
 * @since 0.5.4
 * Created on 2022/01/10 21:43.
 */
public class AsmBeanProxyHandler extends JdkBeanProxyHandler implements BeanProxyHandler {

    private final ApplicationContext applicationContext;

    public AsmBeanProxyHandler(ApplicationContext applicationContext) {
        super(applicationContext);
        this.applicationContext = applicationContext;
    }

    @Override
    public <T, K extends T> T proxyCreatedBean(BeanInfo<K> beanInfo, K bean, Class<T> clazz, BeanProxyType proxyType) {
        if (proxyType == BeanProxyType.NO || proxyType == BeanProxyType.JDK) {
            return super.proxyCreatedBean(beanInfo, bean, clazz, proxyType);
        } if (proxyType == BeanProxyType.ASM) {
            if (beanInfo instanceof ClassBeanInfo<K> classBeanInfo) {
                if (!classBeanInfo.isMethodParameterContainPrimitiveClass()) {
                    MethodProxyHandlerHandler handler = new MethodProxyHandlerHandler(LOGGER);
                    MethodProxyHandlerProcessor<K> processor =
                            new MethodProxyHandlerProcessor<>(this.applicationContext, handler, classBeanInfo).process();
                    if (processor.hasNoProxy() || classBeanInfo.isMethodParameterMoreThanOne()) {
                        return bean;
                    }

                    try {
                        BeanProxy<K> proxy = new AsmProxy<>(classBeanInfo, this.applicationContext.getClassLoader(), handler, MethodProxy.class);
                        /*if () {
                            proxy = new AsmProxy<>(classBeanInfo, this.applicationContext.getClassLoader(), handler,
                                    MethodProxy.class);
                        } else {
                            proxy = new JavassistProxy<>(classBeanInfo, this.applicationContext.getClassLoader(),
                                    handler, MethodProxy.class);
                        }*/
                        return processor.proxy(proxy);
                    } catch (Exception e) {
                        LOGGER.error("", e);
                    }
                }
            }
        }
        return bean;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(AsmBeanProxyHandler.class);
}
