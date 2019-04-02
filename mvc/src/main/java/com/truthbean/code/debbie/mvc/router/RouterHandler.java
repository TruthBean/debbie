package com.truthbean.code.debbie.mvc.router;

import com.truthbean.code.debbie.core.bean.BeanInitializationHandler;
import com.truthbean.code.debbie.core.bean.BeanInvoker;
import com.truthbean.code.debbie.core.io.MediaType;
import com.truthbean.code.debbie.core.reflection.ClassInfo;
import com.truthbean.code.debbie.core.reflection.InvokedParameter;
import com.truthbean.code.debbie.core.watcher.Watcher;
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
        Set<ClassInfo> classInfoSet = BeanInitializationHandler.getAnnotatedMethodBean(Router.class);
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
                    routerInfo.setPaths(setPathRegex(prefixRouter, router));
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

    private List<String> getPrefixPathRegex(Router prefixRouter) {
        if (prefixRouter != null) {
            var prefixPathRegex = prefixRouter.value();

            if (isEmptyPaths(prefixPathRegex)) {
                prefixPathRegex = prefixRouter.path();
            }
            if (!isEmptyPaths(prefixPathRegex)) {
                var prefixPath = trimPaths(prefixPathRegex);
                List<String> newPaths = new ArrayList<>();
                for (var s : prefixPath) {
                    if (!s.startsWith("/")) {
                        newPaths.add("/" + s);
                    } else {
                        newPaths.add(s);
                    }
                }
                return newPaths;
            }
        }
        return null;
    }

    private boolean isEmptyPaths(String[] paths) {
        if (paths == null || paths.length == 0) {
            return true;
        }

        for (String path: paths) {
            if (path != null && !"".equals(path.trim())) {
                return false;
            }
        }

        return true;
    }

    private List<String> trimPaths(String[] paths) {
        List<String> copy = Arrays.asList(paths);
        for (String path: paths) {
            if (path == null || "".equals(path.trim())) {
                copy.remove(path);
            }
        }
        return copy;
    }

    private List<String> getPathRegex(String apiPrefix, Router prefixRouter, Router router) {
        var apiPrefixAfterTrim = apiPrefix;
        if (!"".equals(apiPrefix.trim()) && apiPrefix.endsWith("/")) {
            apiPrefixAfterTrim = apiPrefix.substring(0, apiPrefix.length() - 1);
        }
        var paths = splicePathRegex(prefixRouter, router);

        List<String> list = new ArrayList<>();
        for (String s: paths) {
            list.add(apiPrefixAfterTrim + s);
        }

        return list;
    }

    private List<Pattern> setPathRegex(Router prefixRouter, Router router) {
        var paths = splicePathRegex(prefixRouter, router);
        List<Pattern> patterns = new ArrayList<>();
        for (String path: paths) {
            patterns.add(Pattern.compile(path));
        }
        return patterns;
    }

    private Set<String> splicePathRegex(Router prefixRouter, Router router) {
        var prefixPathRegex = getPrefixPathRegex(prefixRouter);
        var pathRegex = router.value();
        if (isEmptyPaths(pathRegex)) {
            pathRegex = router.path();
        }

        Set<String> newPaths = new HashSet<>();

        if (prefixPathRegex != null) {
            if (!isEmptyPaths(pathRegex)) {
                for (var p: prefixPathRegex) {
                    for (var s : pathRegex) {
                        if (s == null || "".equals(s.trim())) {
                            newPaths.add(p);
                        } else {
                            newPaths.add(p + s);
                        }
                    }
                }
            } else {
                newPaths.addAll(prefixPathRegex);
            }
        } else {
            if (!isEmptyPaths(pathRegex)) {
                var paths = trimPaths(pathRegex);
                for (var s : paths) {
                    if (!s.startsWith("/")) {
                        newPaths.add("/" + s);
                    } else {
                        newPaths.add(s);
                    }
                }
            } else {
                throw new RuntimeException("router value or pathRegex cannot be empty");
            }
        }
        return newPaths;
    }

    public RouterInfo getMatchedRouter(RouterRequest routerRequest, List<MediaType> defaultType) {
        var url = routerRequest.getUrl();
        LOGGER.debug("router url: {}", url);
        RouterInfo result = null;
        var set = Collections.unmodifiableSet(ROUTER_INFO_SET);
        for (var routerInfo : set) {
            var paths = routerInfo.getPaths();
            var matchUrl = false;
            for (var pattern: paths) {
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

        LOGGER.debug("params: " + httpRequest.getParameters());
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