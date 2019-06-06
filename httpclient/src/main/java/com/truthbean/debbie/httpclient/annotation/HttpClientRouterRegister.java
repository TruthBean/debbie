package com.truthbean.debbie.httpclient.annotation;

import com.truthbean.debbie.bean.AnnotationRegister;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/5/22 21:31.
 */
public class HttpClientRouterRegister implements AnnotationRegister<HttpClientRouter> {
    @Override
    public void register() {
        register(HttpClientRouter.class);
    }
}
