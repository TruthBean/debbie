package com.truthbean.debbie.mvc.router;

import com.truthbean.debbie.core.bean.BeanInvoker;
import com.truthbean.debbie.core.io.MediaType;
import com.truthbean.debbie.core.reflection.InvokedParameter;
import com.truthbean.debbie.mvc.response.provider.ResponseHandlerProviderEnum;
import com.truthbean.debbie.mvc.response.view.StaticResourcesView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/23 10:55.
 */
public class RouterInvoker {

    private RouterInfo routerInfo;

    public RouterInvoker(RouterInfo routerInfo) {
        this.routerInfo = routerInfo;
    }

    public Object action() {
        if (routerInfo == null) {
            return null;
        }

        var httpRequest = routerInfo.getRequest();
        if (httpRequest == null) {
            throw new NullPointerException("httpRequest is null");
        }

        var parameters = new RouterRequestValues(httpRequest);

        var handler = new MvcRouterInvokedParameterHandler();
        var args = handler.handleMethodParams(parameters, routerInfo.getMethodParams());

        var values = args.toArray();

        var beanInvoker = new BeanInvoker<>(routerInfo.getRouterClass());
        var method = routerInfo.getMethod();
        var any = beanInvoker.invokeMethod(method, values);
        if (any == null) {
            throw new NullPointerException(method.getName() + " return null");
        }

        for (InvokedParameter methodParam : routerInfo.getMethodParams()) {
            methodParam.setValue(null);
        }

        return resolveResponse(any, httpRequest.getResponseType());
    }

    private Object resolveResponse(Object methodResult, MediaType responseType) {
        if (routerInfo.hasTemplate()) {
            if (methodResult instanceof StaticResourcesView) {
                return ((StaticResourcesView) methodResult).render();
            }
            return methodResult;
        } else {
            var provider = ResponseHandlerProviderEnum.getByResponseType(routerInfo.getResponse().getResponseType());
            var filter = provider.transform(methodResult);
            if (filter == null) {
                throw new RuntimeException(methodResult.toString() + " to " + responseType.getValue() + " error");
            }
            LOGGER.debug(filter.toString());
            return filter;
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(RouterInvoker.class);
}
