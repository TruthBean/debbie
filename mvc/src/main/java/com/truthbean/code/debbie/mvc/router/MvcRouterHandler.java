package com.truthbean.code.debbie.mvc.router;

import com.truthbean.code.debbie.core.bean.BeanInvoker;
import com.truthbean.code.debbie.core.io.MediaType;
import com.truthbean.code.debbie.core.reflection.InvokedParameter;
import com.truthbean.code.debbie.mvc.request.HttpMethod;
import com.truthbean.code.debbie.mvc.request.RouterRequest;
import com.truthbean.code.debbie.mvc.response.RouterErrorResponseHandler;
import com.truthbean.code.debbie.mvc.response.provider.ResponseHandlerProviderEnum;
import com.truthbean.code.debbie.mvc.response.view.StaticResourcesView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-02-14 15:04.
 */
public class MvcRouterHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(MvcRouterHandler.class);

    public static RouterInfo getMatchedRouter(RouterRequest routerRequest, List<MediaType> defaultType) {
        var url = routerRequest.getUrl();
        LOGGER.debug("router url: {}", url);
        RouterInfo result = null;
        var set = MvcRouterRegister.getRouterInfoSet();
        for (var routerInfo : set) {
            var paths = routerInfo.getPaths();
            var matchUrl = false;
            for (var pattern : paths) {
                if (pattern.matcher(url).find()) {
                    matchUrl = true;
                    break;
                }
            }

            LOGGER.debug("match url " + matchUrl);

            var matchMethod = routerInfo.getRequestMethod() == routerRequest.getMethod()
                    || routerInfo.getRequestMethod() == HttpMethod.ALL;
            LOGGER.debug("match method: " + matchMethod);

            var responseType = routerInfo.getResponse().getResponseType();
            var matchResponseType = routerRequest.getResponseTypeInHeader() == MediaType.ANY ||
                    (responseType != MediaType.ANY && responseType == routerRequest.getResponseTypeInHeader()) ||
                    (responseType == MediaType.ANY && defaultType.contains(responseType));
            LOGGER.debug("match response type: " + matchResponseType);

            var requestType = routerRequest.getContentType();

            if (matchUrl && matchMethod && matchResponseType) {
                result = routerInfo;
                if (responseType == MediaType.ANY) {
                    routerInfo.getResponse().setResponseType(defaultType.get(0));
                }
                break;
            }
        }

        if (result == null) {
            LOGGER.warn("router url(" + url + ") not found!");
            return RouterErrorResponseHandler.resourceNotFound(routerRequest);
        } else {
            result.setRequest(routerRequest);
            LOGGER.debug(result.toString());
            return result;
        }
    }

    public static void handleRouter(RouterInfo routerInfo) {
        Object responseValue;
        if (routerInfo.getErrorInfo() != null) {
            responseValue = routerInfo.getErrorInfo();
            routerInfo.getResponse().setData(responseValue);
            return;
        }
        try {
            responseValue = action(routerInfo);
        } catch (Exception e) {
            LOGGER.error("", e);
            var exception = RouterErrorResponseHandler.exception(routerInfo.getRequest(), e);
            responseValue = exception.getErrorInfo();
        }

        routerInfo.getResponse().setData(responseValue);
    }

    private static Object action(RouterInfo routerInfo) {
        if (routerInfo == null) {
            return null;
        }

        var httpRequest = routerInfo.getRequest();
        if (httpRequest == null) {
            throw new NullPointerException("httpRequest is null");
        }

        LOGGER.debug("params: " + httpRequest.getParameters());
        LOGGER.debug("query: " + httpRequest.getQueries());

        var parameters = new RouterRequestValues(httpRequest);

        var handler = new MvcRouterInvokedParameterHandler();
        var args = handler.handleMethodParams(parameters, routerInfo.getMethodParams());
        LOGGER.debug("args: " + args);

        var values = args.toArray();
        LOGGER.debug("values: " + Arrays.toString(values));

        var beanInvoker = new BeanInvoker(routerInfo.getRouterClass());
        var method = routerInfo.getMethod();
        var any = beanInvoker.invokeMethod(method, values);
        if (any == null) {
            throw new NullPointerException(method.getName() + " return null");
        }

        for (InvokedParameter methodParam : routerInfo.getMethodParams()) {
            methodParam.setValue(null);
        }

        if (routerInfo.hasTemplate()) {
            if (any instanceof StaticResourcesView) {
                return ((StaticResourcesView) any).render();
            }
            return any;
        } else {
            var provider = ResponseHandlerProviderEnum.getByResponseType(routerInfo.getResponse().getResponseType());
            var filter = provider.transform(any);
            if (filter == null) {
                throw new RuntimeException(any.toString() + " to " + httpRequest.getResponseTypeInHeader().getValue() + " error");
            }
            LOGGER.debug(filter.toString());
            return filter;
        }
    }
}