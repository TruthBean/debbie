package com.truthbean.debbie.mvc.router;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.io.MediaType;
import com.truthbean.debbie.io.MediaTypeInfo;
import com.truthbean.debbie.net.uri.UriUtils;
import com.truthbean.debbie.mvc.MvcConfiguration;
import com.truthbean.debbie.mvc.request.HttpMethod;
import com.truthbean.debbie.mvc.request.RouterRequest;
import com.truthbean.debbie.mvc.response.RouterErrorResponseHandler;
import com.truthbean.debbie.mvc.response.RouterResponse;
import com.truthbean.debbie.mvc.response.provider.ResponseHandlerProviderEnum;
import com.truthbean.debbie.mvc.url.RouterPathFragments;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-02-14 15:04.
 */
public class MvcRouterHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(MvcRouterHandler.class);

    public static RouterInfo getMatchedRouter(RouterRequest routerRequest, MvcConfiguration configuration) {
        var url = routerRequest.getUrl();
        LOGGER.debug("router uri: {}", url);
        RouterInfo result = null;
        var set = MvcRouterRegister.getRouterInfoSet();
        var routerInfos = matchRouterPath(url, set, routerRequest);

        Set<RouterInfo> rawPathEqualed = new HashSet<>();
        for (var routerInfo : routerInfos) {
            var paths = routerInfo.getPaths();
            for (var path : paths) {
                if (path.getRawPath().equals(UriUtils.getPathsWithoutMatrix(url))) {
                    rawPathEqualed.add(routerInfo);
                }
            }
        }

        Set<RouterInfo> targetRouterInfoSet;
        if (rawPathEqualed.isEmpty()) {
            targetRouterInfoSet = routerInfos;
        } else {
            targetRouterInfoSet = rawPathEqualed;
        }

        for (var routerInfo : targetRouterInfoSet) {
            var paths = routerInfo.getPaths();
            LOGGER.debug("match uri " + paths);

            var requestMethod = routerInfo.getRequestMethod();
            var matchMethod = requestMethod.contains(routerRequest.getMethod()) || requestMethod.contains(HttpMethod.ALL);
            if (!matchMethod) {
                continue;
            }
            LOGGER.debug("match method: " + requestMethod);

            // response type
            var response = routerInfo.getResponse();
            var responseType = response.getResponseType();
            LOGGER.debug("responseType: " + responseType);
            var responseTypeInRequestHeader = routerRequest.getResponseType();
            LOGGER.debug("responseTypeInRequestHeader: " + responseTypeInRequestHeader);
            Set<MediaTypeInfo> defaultResponseTypes = configuration.getDefaultResponseTypes();
            var matchResponseType = responseTypeInRequestHeader.isAny() ||
                    (!responseType.isAny() && responseType.isSameMediaType(responseTypeInRequestHeader)) ||
                    (responseType.isAny() && MediaTypeInfo.contains(defaultResponseTypes, responseTypeInRequestHeader))
                    || (configuration.isAllowClientResponseType() && !responseTypeInRequestHeader.isAny());
            if (!matchResponseType) {
                continue;
            }
            if (responseType.isAny() && responseTypeInRequestHeader.isAny()) {
                response.setResponseType(defaultResponseTypes.iterator().next());
                response.setHandler(ResponseHandlerProviderEnum.getByResponseType(response.getResponseType().toMediaType()));
            } else {
                if (responseType.isAny() &&
                        MediaTypeInfo.contains(defaultResponseTypes, responseTypeInRequestHeader)) {
                    response.setResponseType(responseTypeInRequestHeader);
                    response.setHandler(ResponseHandlerProviderEnum.getByResponseType(response.getResponseType().toMediaType()));
                } else if (configuration.isAllowClientResponseType() && !responseTypeInRequestHeader.isAny()) {
                    response.setResponseType(responseTypeInRequestHeader);
                    response.setHandler(ResponseHandlerProviderEnum.getByResponseType(response.getResponseType().toMediaType()));
                }
            }
            LOGGER.debug("match response type: " + responseType);

            // content type
            var requestType = routerInfo.getRequestType();
            LOGGER.debug("requestType: " + requestType);
            var contextType = routerRequest.getContentType();
            LOGGER.debug("contextType: " + contextType);
            Set<MediaTypeInfo> defaultContentTypes = configuration.getDefaultContentTypes();
            var matchRequestType = contextType.isAny() ||
                    (!MediaType.ANY.isSame(requestType) && requestType.isSame(contextType))
                    || (!MediaType.ANY.isSame(requestType) && !contextType.isAny() && requestType.isSame(contextType.toMediaType()))
                    || (
                    (MediaType.ANY.isSame(requestType) && !contextType.isAny() &&
                            (MediaTypeInfo.contains(defaultContentTypes, contextType) || configuration.isAcceptClientContentType()))
            );
            if (!matchRequestType) {
                continue;
            }
            var flag = (MediaType.ANY.isSame(requestType) && !MediaType.ANY.isSame(contextType) &&
                    (MediaTypeInfo.contains(defaultContentTypes, contextType) || configuration.isAcceptClientContentType()));
            if (flag) {
                routerInfo.setRequestType(contextType.toMediaType());
            }
            LOGGER.debug("match request type: " + requestType.info());

            result = routerInfo;
            break;
        }

        if (result == null) {
            LOGGER.warn("router uri(" + url + ") not found!");
            return RouterErrorResponseHandler.resourceNotFound(routerRequest);
        } else {
            result.setRequest(routerRequest);
            LOGGER.debug(result.toString());
            return result;
        }
    }

    public static Set<RouterInfo> matchRouterPath(String url, Set<RouterInfo> routerInfos, RouterRequest routerRequest) {
        Set<RouterInfo> result = new HashSet<>();
        //TODO: 优先匹配静态资源...

        for (RouterInfo routerInfo : routerInfos) {
            List<RouterPathFragments> paths = routerInfo.getPaths();
            // if has no matched, then match no variable path
            for (var pattern : paths) {
                if (!pattern.hasVariable()) {
                    if (pattern.getRawPath().equalsIgnoreCase(url)) {
                        result.add(routerInfo.clone());
                    }
                }
            }
        }


        for (RouterInfo routerInfo : routerInfos) {
            List<RouterPathFragments> paths = routerInfo.getPaths();
            // if has no matched, then match variable path
            for (var pattern : paths) {
                if (pattern.hasVariable()) {
                    if (pattern.getPattern().matcher(url).find()) {
                        var pathAttributes = RouterPathSplicer.getPathVariable(pattern.getRawPath(), url);
                        routerRequest.getPathAttributes().putAll(pathAttributes);
                        result.add(routerInfo.clone());
                    }
                }
            }
        }

        // if has no matched, then remove matrix
        url = UriUtils.getPathsWithoutMatrix(url);
        for (RouterInfo routerInfo : routerInfos) {
            List<RouterPathFragments> paths = routerInfo.getPaths();
            var matched = matchRouterPath(url, paths, routerRequest, true);
            if (matched) {
                result.add(routerInfo.clone());
            }
        }
        return result;
    }

    public static boolean matchRouterPath(String url, List<RouterPathFragments> paths, RouterRequest routerRequest,
                                          boolean withoutMatrix) {
        var matchUrl = false;
        //TODO: 优先匹配静态资源...

        if (!matchUrl) {
            // if has no matched, then match no variable path
            for (var pattern : paths) {
                if (!pattern.hasVariable()) {
                    if (pattern.getRawPath().equalsIgnoreCase(url)) {
                        matchUrl = true;
                        break;
                    }
                }
            }

            // if has no matched, then match variable path
            if (!matchUrl) {
                for (var pattern : paths) {
                    if (pattern.hasVariable()) {
                        if (pattern.getPattern().matcher(url).find()) {
                            matchUrl = true;
                            var pathAttributes = RouterPathSplicer.getPathVariable(pattern.getRawPath(), url);
                            routerRequest.getPathAttributes().putAll(pathAttributes);
                            break;
                        }
                    }
                }
            }

            // if has no matched, then remove matrix
            if (!matchUrl && !withoutMatrix) {
                url = UriUtils.getPathsWithoutMatrix(url);
                matchUrl = matchRouterPath(url, paths, routerRequest, true);
            }
        }
        return matchUrl;
    }

    public static RouterResponse handleRouter(RouterInfo routerInfo, BeanFactoryHandler handler) {
        RouterResponse routerResponse = routerInfo.getResponse();

        routerResponse.setResponseType(routerInfo.getResponse().getResponseType());

        Object responseValue;
        if (routerInfo.getErrorInfo() != null) {
            responseValue = routerInfo.getErrorInfo();
            routerResponse.setContent(responseValue);
            return routerResponse;
        }
        try {
            RouterInvoker invoker = new RouterInvoker(routerInfo);
            responseValue = invoker.action(handler);
        } catch (Exception e) {
            LOGGER.error("", e);
            var exception = RouterErrorResponseHandler.exception(routerInfo.getRequest(), e);
            responseValue = exception.getErrorInfo();
        }

        routerResponse.setContent(responseValue);
        return routerResponse;
    }
}