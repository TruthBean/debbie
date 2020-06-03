/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mvc.router;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.io.MediaType;
import com.truthbean.debbie.mvc.response.AbstractResponseContentHandler;
import com.truthbean.debbie.mvc.response.ResponseTypeException;
import com.truthbean.debbie.mvc.response.RouterResponse;
import com.truthbean.debbie.mvc.response.provider.NothingResponseHandler;
import com.truthbean.debbie.mvc.response.provider.ResponseContentHandlerProviderEnum;
import com.truthbean.debbie.mvc.response.view.AbstractTemplateView;
import com.truthbean.debbie.mvc.response.view.NoViewRender;
import com.truthbean.debbie.mvc.response.view.StaticResourcesView;
import com.truthbean.debbie.reflection.ExecutableArgument;
import com.truthbean.debbie.reflection.ReflectionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/23 10:55.
 */
public class RouterInvoker {

    private final RouterInfo routerInfo;

    public RouterInvoker(RouterInfo routerInfo) {
        this.routerInfo = routerInfo;
    }

    public void action(RouterResponse routerResponse, BeanFactoryHandler beanFactoryHandler) throws Throwable {
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

        var method = routerInfo.getMethod();
        if (method == null) {
            // todo
            return;
        }

        var parameters = new RouterRequestValues(httpRequest, httpResponse);

        var handler = new RouterMethodArgumentHandler();
        var args = handler.handleMethodParams(parameters, routerInfo.getMethodParams(), routerInfo.getRequestType());

        var values = args.toArray();

        var type = routerInfo.getRouterClass();

        var instance = routerInfo.getRouterInstance();
        if (instance == null) {
            instance = beanFactoryHandler.factory(type);
            routerInfo.setRouterInstance(instance);
        }

        Object any = ReflectionHelper.invokeMethod(true, instance, method, values);

        for (ExecutableArgument methodParam : routerInfo.getMethodParams()) {
            methodParam.setValue(null);
        }

        resolveResponse(any, httpRequest.getResponseType().toMediaType(), routerResponse);
    }

    @SuppressWarnings({"unchecked"})
    private void resolveResponse(Object methodResult, MediaType responseType, RouterResponse routerResponse) {
        var response = routerInfo.getResponse();
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
                routerResponse.setContent(methodResult);
            } else {
                if (handleResponse(methodResult, routerResponse, response)) return;
                routerResponse.setContent(methodResult);
            }
        } else {
            if (handleResponse(methodResult, routerResponse, response)) return;

            if (methodResult == null) return;

            var provider = ResponseContentHandlerProviderEnum.getByResponseType(routerInfo.getResponse().getResponseType().toMediaType());
            var filter = provider.transform(methodResult);
            if (filter == null) {
                throw new ResponseTypeException(methodResult.toString() + " to " + responseType.getValue() + " error");
            }
            LOGGER.debug(filter.toString());
            routerResponse.setContent(filter);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private boolean handleResponse(Object methodResult, RouterResponse routerResponse, RouterResponse response) {
        AbstractResponseContentHandler handler = response.getHandler();
        if (handler != null && handler.getClass() != NothingResponseHandler.class) {
            handler.handleResponse(routerResponse, methodResult);
            return true;
        }
        return false;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(RouterInvoker.class);
}
