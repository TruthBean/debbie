package com.truthbean.debbie.mvc.router;

import com.truthbean.debbie.core.io.MediaType;
import com.truthbean.debbie.core.net.uri.UriUtils;
import com.truthbean.debbie.core.reflection.InvokedParameter;
import com.truthbean.debbie.mvc.MvcConfiguration;
import com.truthbean.debbie.mvc.request.HttpMethod;
import com.truthbean.debbie.mvc.request.RequestParameter;
import com.truthbean.debbie.mvc.request.RequestParameterType;
import com.truthbean.debbie.mvc.request.RouterRequest;
import com.truthbean.debbie.mvc.response.RouterErrorResponseHandler;
import com.truthbean.debbie.mvc.response.RouterResponse;
import com.truthbean.debbie.mvc.response.provider.ResponseHandlerProviderEnum;
import com.truthbean.debbie.mvc.url.RouterPathFragments;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
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
        for (var routerInfo : set) {
            var paths = routerInfo.getPaths();
            var matchUrl = matchRouterPath(url, paths, routerRequest, false);
            if (!matchUrl) {
                continue;
            }
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
            Set<MediaType> defaultResponseTypes = configuration.getDefaultResponseTypes();
            var matchResponseType = MediaType.ANY.isSame(responseTypeInRequestHeader) ||
                    (!MediaType.ANY.isSame(responseType) && responseType.isSame(responseTypeInRequestHeader)) ||
                    (MediaType.ANY.isSame(responseType) && MediaType.contains(defaultResponseTypes, responseTypeInRequestHeader))
                    || (configuration.isAllowClientResponseType() && !MediaType.ANY.isSame(responseTypeInRequestHeader));
            if (!matchResponseType) {
                continue;
            } else {
                if (MediaType.ANY.isSame(responseType) && MediaType.ANY.isSame(responseTypeInRequestHeader)) {
                    response.setResponseType(defaultResponseTypes.iterator().next());
                    response.setHandler(ResponseHandlerProviderEnum.getByResponseType(response.getResponseType()));
                } else {
                    if (MediaType.ANY.isSame(responseType) &&
                            MediaType.contains(defaultResponseTypes, responseTypeInRequestHeader)) {
                        response.setResponseType(responseTypeInRequestHeader);
                        response.setHandler(ResponseHandlerProviderEnum.getByResponseType(response.getResponseType()));
                    } else if (configuration.isAllowClientResponseType() && !MediaType.ANY.isSame(responseTypeInRequestHeader)) {
                        response.setResponseType(responseTypeInRequestHeader);
                        response.setHandler(ResponseHandlerProviderEnum.getByResponseType(response.getResponseType()));
                    }
                }
            }
            LOGGER.debug("match response type: " + responseType);

            // content type
            var requestType = routerInfo.getRequestType();
            LOGGER.debug("requestType: " + requestType);
            var contextType = routerRequest.getContentType();
            LOGGER.debug("contextType: " + contextType);
            Set<MediaType> defaultContentTypes = configuration.getDefaultContentTypes();
            var matchRequestType = MediaType.ANY.isSame(contextType) ||
                    (!MediaType.ANY.isSame(requestType) && requestType.isSame(contextType))
                    || (
                    (MediaType.ANY.isSame(requestType) && !MediaType.ANY.isSame(contextType) &&
                            (MediaType.contains(defaultContentTypes, contextType) || configuration.isAcceptClientContentType()))
            );
            if (!matchRequestType) {
                continue;
            } else {
                var flag = (MediaType.ANY.isSame(requestType) && !MediaType.ANY.isSame(contextType) &&
                        (MediaType.contains(defaultContentTypes, contextType) || configuration.isAcceptClientContentType()));
                if (flag) {
                    routerInfo.setRequestType(contextType);
                }
            }
            LOGGER.debug("match request type: " + requestType);

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

    public static RouterResponse handleRouter(RouterInfo routerInfo) {
        RouterResponse routerResponse = new RouterResponse();

        routerResponse.setHasTemplate(routerInfo.hasTemplate());
        routerResponse.setTemplateSuffix(routerInfo.getTemplateSuffix());
        routerResponse.setTemplatePrefix(routerInfo.getTemplatePrefix());

        routerResponse.setResponseType(routerInfo.getResponse().getResponseType());

        Object responseValue;
        if (routerInfo.getErrorInfo() != null) {
            responseValue = routerInfo.getErrorInfo();
            routerResponse.setContent(responseValue);
            routerInfo.getResponse().setData(responseValue);
            return routerResponse;
        }
        try {
            RouterInvoker invoker = new RouterInvoker(routerInfo);
            responseValue = invoker.action();
        } catch (Exception e) {
            LOGGER.error("", e);
            var exception = RouterErrorResponseHandler.exception(routerInfo.getRequest(), e);
            responseValue = exception.getErrorInfo();
        }

        routerInfo.getResponse().setData(responseValue);
        routerResponse.setContent(responseValue);
        return routerResponse;
    }
}