package com.truthbean.debbie.httpclient;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.bean.BeanInitialization;
import com.truthbean.debbie.bean.DebbieBeanInfo;
import com.truthbean.debbie.bean.SingletonBeanRegister;
import com.truthbean.debbie.httpclient.annotation.HttpClientRouter;

import java.util.Set;

/**
 * @author TruthBean
 * @since 0.0.2
 */
public class HttpClientBeanRegister extends SingletonBeanRegister {
    private HttpClientFactory handler;
    private BeanFactoryHandler beanFactoryHandler;
    private BeanInitialization initialization;

    public HttpClientBeanRegister(BeanFactoryHandler beanFactoryHandler) {
        super(beanFactoryHandler);
        handler = new HttpClientFactory();
        this.beanFactoryHandler = beanFactoryHandler;
        initialization = beanFactoryHandler.getBeanInitialization();
    }

    public void registerHttpClient() {
        Set<DebbieBeanInfo> annotatedClass = initialization.getAnnotatedClass(HttpClientRouter.class);
        if (annotatedClass != null && !annotatedClass.isEmpty()) {
            for (DebbieBeanInfo<?> httpClientRouter : annotatedClass) {
                httpClientRouter.setBeanFactory(new HttpClientBeanFactory<>(httpClientRouter.getBeanClass(), handler));
                initialization.refreshBean(httpClientRouter);
                beanFactoryHandler.refreshBeans();
            }
        }
    }

    public void registerHttpClientFactory() {
        DebbieBeanInfo<HttpClientFactory> httpClientFactoryBeanInfo = initialization.getRegisterRawBean(HttpClientFactory.class);
        if (httpClientFactoryBeanInfo == null) {
            registerSingletonBean(handler, HttpClientFactory.class, "httpClientFactory");
        } else if (httpClientFactoryBeanInfo.getBean() == null) {
            httpClientFactoryBeanInfo.setBeanName("httpClientFactory");
            httpClientFactoryBeanInfo.setBean(handler);
            registerSingletonBean(httpClientFactoryBeanInfo);
        }
    }
}
