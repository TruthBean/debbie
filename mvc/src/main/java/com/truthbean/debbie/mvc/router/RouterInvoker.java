package com.truthbean.debbie.mvc.router;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.io.MediaType;
import com.truthbean.debbie.reflection.ExecutableArgument;
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

    public Object action(BeanFactoryHandler beanFactoryHandler) {
        if (routerInfo == null) {
            return null;
        }

        var httpRequest = routerInfo.getRequest();
        if (httpRequest == null) {
            throw new NullPointerException("httpRequest is null");
        }

        var httpResponse = routerInfo.getResponse();
        if (httpResponse == null) {
            throw new NullPointerException("httpResponse is null");
        }

        var parameters = new RouterRequestValues(httpRequest, httpResponse);

        var handler = new RouterMethodArgumentHandler();
        var args = handler.handleMethodParams(parameters, routerInfo.getMethodParams(), routerInfo.getRequestType());

        var values = args.toArray();

        var type = routerInfo.getRouterClass();
        var method = routerInfo.getMethod();
        Object any = beanFactoryHandler.factoryAndInvokeMethod(type, method, values);
        /*if (any == null) {
            throw new NullPointerException(method.getName() + " return null");
        }*/

        for (ExecutableArgument methodParam : routerInfo.getMethodParams()) {
            methodParam.setValue(null);
        }

        return resolveResponse(any, httpRequest.getResponseType().toMediaType());
    }

    private Object resolveResponse(Object methodResult, MediaType responseType) {
        var response = routerInfo.getResponse();
        if (response.hasTemplate()) {
            if (methodResult instanceof StaticResourcesView) {
                return ((StaticResourcesView) methodResult).render();
            }
            return methodResult;
        } else {
            if (methodResult == null) return null;

            var provider = ResponseHandlerProviderEnum.getByResponseType(routerInfo.getResponse().getResponseType().toMediaType());
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
