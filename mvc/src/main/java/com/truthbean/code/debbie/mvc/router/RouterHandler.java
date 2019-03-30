package com.truthbean.code.debbie.mvc.router;

import com.truthbean.code.debbie.core.bean.BeanInitializationHandler;
import com.truthbean.code.debbie.core.bean.BeanInvoker;
import com.truthbean.code.debbie.core.io.MediaType;
import com.truthbean.code.debbie.core.reflection.ClassInfo;
import com.truthbean.code.debbie.core.reflection.InvokedParameter;
import com.truthbean.code.debbie.mvc.MvcConfiguration;
import com.truthbean.code.debbie.mvc.request.HttpMethod;
import com.truthbean.code.debbie.mvc.request.RouterRequest;
import com.truthbean.code.debbie.mvc.response.RouterErrorResponseHandler;
import com.truthbean.code.debbie.mvc.response.RouterInvokeResultData;
import com.truthbean.code.debbie.mvc.response.provider.ResponseHandlerProviderEnum;
import com.truthbean.code.debbie.mvc.response.view.StaticResourcesView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Pattern;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-02-14 15:04.
 */
public class RouterHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(RouterHandler.class);

    private static final Set<RouterInfo> ROUTER_INFO_SET = new HashSet<>();

    public void registerRouter(MvcConfiguration webConfiguration) {
        Set<ClassInfo> classInfoSet = BeanInitializationHandler.getAnnotatedClass(Router.class);
        for (var classInfo : classInfoSet) {
            Router prefixRouter = (Router) classInfo.getClassAnnotations().get(Router.class);
            var methods = classInfo.getMethods();
            for (var method : methods) {
                Router router = method.getAnnotation(Router.class);
                List<InvokedParameter> methodParams = RouterInvokedParameterHandler.typeOf(method.getParameters());
                if (router != null) {
                    var routerInfo = new RouterInfo();
                    routerInfo.setRouterClass(classInfo.getClazz());
                    routerInfo.setMethod(method);
                    routerInfo.setPathRegex(Pattern.compile(getPathRegex(prefixRouter, router)));
                    routerInfo.setRequestMethod(router.method());

                    routerInfo.setHasTemplate(router.hasTemplate());
                    if ("".equals(router.templatePrefix().trim())) {
                        routerInfo.setTemplatePrefix(webConfiguration.getTemplatePrefix());
                    } else {
                        routerInfo.setTemplatePrefix(router.templatePrefix());
                    }
                    if ("".equals(router.templateSuffix().trim())) {
                        routerInfo.setTemplateSuffix(webConfiguration.getTemplateSuffix());
                    } else {
                        routerInfo.setTemplateSuffix(router.templateSuffix());
                    }

                    var response = new RouterInvokeResultData();
                    var defaultType = webConfiguration.getDefaultTypes();
                    if (router.hasTemplate()) {
                        response.setHandler(ResponseHandlerProviderEnum.TEMPLATE_VIEW.getProvider());
                        response.setResponseType(router.responseType());
                    } else {
                        response.setHandler(router.handlerFilter().getProvider());
                        if (router.responseType() == MediaType.ANY && !defaultType.isEmpty()) {
                            response.setResponseType(defaultType.get(0));
                        }
                    }
                    response.setResponseType(router.responseType());

                    routerInfo.setResponse(response);
                    routerInfo.setMethodParams(methodParams);
                    LOGGER.debug("register router:" + routerInfo);
                    ROUTER_INFO_SET.add(routerInfo);
                }
            }
        }
    }

    private String getPrefixPathRegex(Router prefixRouter) {
        String prefixPathRegex = null;
        if (prefixRouter != null) {
            prefixPathRegex = prefixRouter.value();
            if ("".equals(prefixPathRegex.trim())) {
                prefixPathRegex = prefixRouter.pathRegex();
            }
            if (!prefixPathRegex.startsWith("/")) {
                prefixPathRegex = "/" + prefixPathRegex;
            }
        }
        return prefixPathRegex;
    }

    private String getPathRegex(String apiPrefix, Router prefixRouter, Router router) {
        var apiPrefixAfterTrim = apiPrefix;
        if (!"".equals(apiPrefix.trim()) && apiPrefix.endsWith("/")) {
            apiPrefixAfterTrim = apiPrefix.substring(0, apiPrefix.length() - 1);
        }
        String prefixPathRegex = getPrefixPathRegex(prefixRouter);

        String pathRegex;
        if (router != null) {
            pathRegex = router.value();
            if ("".equals(pathRegex.trim())) {
                pathRegex = router.pathRegex();
            }
            if (prefixPathRegex != null) {
                pathRegex = apiPrefixAfterTrim + prefixPathRegex + pathRegex;
            } else {
                pathRegex = apiPrefixAfterTrim + pathRegex;
            }
        } else {
            if (prefixPathRegex != null) {
                pathRegex = apiPrefixAfterTrim + prefixPathRegex;
            } else {
                throw new RuntimeException("router value or pathRegex cannot be empty");
            }
        }

        return pathRegex;
    }

    private String getPathRegex(Router prefixRouter, Router router) {
        String prefixPathRegex = getPrefixPathRegex(prefixRouter);

        String pathRegex;
        if (router != null) {
            pathRegex = router.value();
            if ("".equals(pathRegex.trim())) {
                pathRegex = router.pathRegex();
            }
            if (prefixPathRegex != null) {
                pathRegex = prefixPathRegex + pathRegex;
            }
        } else {
            if (prefixPathRegex != null) {
                pathRegex = prefixPathRegex;
            } else {
                throw new RuntimeException("router value or pathRegex cannot be empty");
            }
        }

        return pathRegex;
    }

    public RouterInfo getMatchedRouter(RouterRequest routerRequest, List<MediaType> defaultType) {
        var url = routerRequest.getUrl();
        LOGGER.debug("router url: {}", url);
        RouterInfo result = null;
        var set = Collections.unmodifiableSet(ROUTER_INFO_SET);
        for (var routerInfo : set) {
            var matchUrlAndMethod = routerInfo.getPathRegex().matcher(routerRequest.getUrl()).find() &&
                    (routerInfo.getRequestMethod() == routerRequest.getMethod()
                            || routerInfo.getRequestMethod() == HttpMethod.ALL);
            LOGGER.debug("match url and method: " + matchUrlAndMethod);

            var responseType = routerInfo.getResponse().getResponseType();
            var matchResponseType = routerRequest.getResponseTypeInHeader() == MediaType.ANY ||
                    (responseType != MediaType.ANY && responseType == routerRequest.getResponseTypeInHeader()) ||
                    (responseType == MediaType.ANY && defaultType.contains(responseType));
            LOGGER.debug("match response type: " + matchResponseType);

            var requestType = routerRequest.getContentType();

            if (matchResponseType && matchUrlAndMethod) {
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

    public void handleRouter(RouterInfo routerInfo) {
        Object responseValue;
        if (routerInfo.getErrorInfo() != null) {
            responseValue = routerInfo.getErrorInfo();
            routerInfo.getResponse().setData(responseValue);
            return;
        }
        try {
            responseValue = action(routerInfo);
        } catch (Exception e){
            LOGGER.error("", e);
            var exception = RouterErrorResponseHandler.exception(routerInfo.getRequest(), e);
            responseValue = exception.getErrorInfo();
        }

        routerInfo.getResponse().setData(responseValue);
    }

    private Object action(RouterInfo routerInfo) {
        if (routerInfo == null) {
            return null;
        }

        var httpRequest = routerInfo.getRequest();
        if (httpRequest == null) {
            throw new NullPointerException("httpRequest is null");
        }

        LOGGER.debug("params: " + httpRequest.getParams());
        LOGGER.debug("query: " + httpRequest.getQueries());

        var parameters = new RouterRequestValues(httpRequest);

        var handler = new RouterInvokedParameterHandler();
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
            } LOGGER.debug(filter.toString());
            return filter;
        }
    }
}