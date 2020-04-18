package com.truthbean.debbie.httpclient.annotation;

import com.truthbean.debbie.bean.AnnotationRegister;
import com.truthbean.debbie.bean.BeanInitialization;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/5/22 21:31.
 */
public class HttpClientRouterRegister implements AnnotationRegister<HttpClientRouter> {
    private final BeanInitialization initialization;

    public HttpClientRouterRegister(BeanInitialization beanInitialization) {
        this.initialization = beanInitialization;
    }

    @Override
    public void register() {
        register(HttpClientRouter.class);
    }

    @Override
    public BeanInitialization getBeanInitialization() {
        return initialization;
    }
}
