package com.truthbean.debbie.mvc.router;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.io.MediaType;
import com.truthbean.debbie.mvc.response.AbstractResponseContentHandler;
import com.truthbean.debbie.mvc.response.RouterResponse;
import com.truthbean.debbie.mvc.response.provider.NothingResponseHandler;
import com.truthbean.debbie.mvc.response.view.AbstractTemplateView;
import com.truthbean.debbie.mvc.response.view.NoViewRender;
import com.truthbean.debbie.reflection.ExecutableArgument;
import com.truthbean.debbie.mvc.response.provider.ResponseContentHandlerProviderEnum;
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

    public void action(RouterResponse routerResponse, BeanFactoryHandler beanFactoryHandler) {
        if (routerInfo == null) {
            return;
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

        for (ExecutableArgument methodParam : routerInfo.getMethodParams()) {
            methodParam.setValue(null);
        }

        resolveResponse(any, httpRequest.getResponseType().toMediaType(), routerResponse);
    }

    private void resolveResponse(Object methodResult, MediaType responseType, RouterResponse routerResponse) {
        var response = routerInfo.getResponse();
        AbstractResponseContentHandler handler = response.getHandler();
        if (handler != null && handler.getClass() != NothingResponseHandler.class) {
            handler.handleResponse(routerResponse, methodResult);
            return;
        }
        if (response.hasTemplate()) {
            if (methodResult instanceof StaticResourcesView) {
                var content = ((StaticResourcesView) methodResult).render();
                routerResponse.setContent(content);
            } else if (methodResult instanceof AbstractTemplateView) {
                try {
                    Object result = ((AbstractTemplateView) methodResult).render();
                    if (!(result instanceof NoViewRender)) {
                        routerResponse.setContent(result);
                        return;
                    }
                } catch (Exception e) {
                    LOGGER.error("render error. ", e);
                }
            }
            routerResponse.setContent(methodResult);
        } else {
            if (methodResult == null) return;

            var provider = ResponseContentHandlerProviderEnum.getByResponseType(routerInfo.getResponse().getResponseType().toMediaType());
            var filter = provider.transform(methodResult);
            if (filter == null) {
                throw new RuntimeException(methodResult.toString() + " to " + responseType.getValue() + " error");
            }
            LOGGER.debug(filter.toString());
            routerResponse.setContent(filter);
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(RouterInvoker.class);
}
