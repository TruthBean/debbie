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

import com.truthbean.debbie.bean.GlobalBeanFactory;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.io.MediaType;
import com.truthbean.debbie.io.MediaTypeInfo;
import com.truthbean.debbie.io.ResourcesHandler;
import com.truthbean.debbie.mvc.MvcConfiguration;
import com.truthbean.debbie.mvc.request.HttpMethod;
import com.truthbean.debbie.mvc.request.RouterRequest;
import com.truthbean.debbie.mvc.response.ErrorResponseCallback;
import com.truthbean.debbie.mvc.response.HttpStatus;
import com.truthbean.debbie.mvc.response.RouterErrorResponseHandler;
import com.truthbean.debbie.mvc.response.RouterResponse;
import com.truthbean.debbie.mvc.response.provider.NothingResponseHandler;
import com.truthbean.debbie.mvc.response.provider.ResponseContentHandlerProviderEnum;
import com.truthbean.debbie.mvc.url.RouterPathFragments;
import com.truthbean.debbie.net.uri.UriPathFragment;
import com.truthbean.debbie.net.uri.UriUtils;
import com.truthbean.Logger;
import com.truthbean.logger.LoggerFactory;

import java.util.*;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-02-14 15:04.
 */
public class MvcRouterHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(MvcRouterHandler.class);

    public static byte[] handleStaticResources(RouterRequest routerRequest, Map<String, String> mappingAndLocation) {
        final var url = routerRequest.getUrl();
        LOGGER.trace(() -> "is static resource router uri: " + url + " ?");
        for (Map.Entry<String, String> entry : mappingAndLocation.entrySet()) {
            var mapping = entry.getKey();
            var location = entry.getValue();
            mapping = mapping.replace("**", "");
            if (url.startsWith(mapping)) {
                String resource = location + url.substring(mapping.length());
                var result = ResourcesHandler.handleStaticBytesResource(resource);
                if (result != null) {
                    LOGGER.debug(() -> "match static resource uri: " + url);
                    return result;
                }
            }
        }
        return null;
    }

    public static RouterInfo getMatchedRouter(RouterRequest routerRequest, MvcConfiguration configuration) {
        var url = routerRequest.getUrl();
        LOGGER.trace(() -> "is dynamic router uri: " + url + " ?");
        RouterInfo result = null;
        var set = MvcRouterRegister.getRouterInfoSet();
        var routerInfos = matchRouterPath(url, set, routerRequest);

        for (var routerInfo : routerInfos) {
            var paths = routerInfo.getPaths();
            LOGGER.trace(() -> "match uri " + paths);

            var requestMethod = routerInfo.getRequestMethod();
            var matchMethod = requestMethod.contains(routerRequest.getMethod()) || requestMethod.contains(HttpMethod.ALL);
            if (!matchMethod) {
                continue;
            }
            LOGGER.trace(() -> "match method: " + requestMethod);

            // response type
            var response = routerInfo.getResponse();
            var responseType = response.getResponseType();
            LOGGER.trace(() -> "responseType: " + responseType);
            var responseTypeInRequestHeader = routerRequest.getResponseType();
            LOGGER.trace(() -> "responseTypeInRequestHeader: " + responseTypeInRequestHeader);
            Set<MediaTypeInfo> defaultResponseTypes = configuration.getDefaultResponseTypes();
            var matchResponseType = responseTypeInRequestHeader.isAny() ||
                    (!responseType.isAny() && responseType.isSameMediaType(responseTypeInRequestHeader)) ||
                    (responseType.isAny() && MediaTypeInfo.contains(defaultResponseTypes, responseTypeInRequestHeader))
                    || (configuration.isAllowClientResponseType() && !responseTypeInRequestHeader.isAny());
            if (!matchResponseType) {
                continue;
            }
            if (response.getHandler() == null || response.getHandler().getClass() == NothingResponseHandler.class) {
                if (responseType.isAny() && responseTypeInRequestHeader.isAny()) {
                    if (defaultResponseTypes.isEmpty()) {
                        LOGGER.error("responseTypes has not default value, set application/json as default!");
                        defaultResponseTypes.add(MediaType.APPLICATION_JSON_UTF8.info());
                    }
                    response.setResponseType(defaultResponseTypes.iterator().next());
                    response.setHandler(ResponseContentHandlerProviderEnum.getByResponseType(response.getResponseType().toMediaType()));
                } else {
                    if (responseType.isAny() &&
                            MediaTypeInfo.contains(defaultResponseTypes, responseTypeInRequestHeader)) {
                        response.setResponseType(responseTypeInRequestHeader);
                        response.setHandler(ResponseContentHandlerProviderEnum.getByResponseType(response.getResponseType().toMediaType()));
                    } else if (configuration.isAllowClientResponseType() && !responseTypeInRequestHeader.isAny()) {
                        response.setResponseType(responseTypeInRequestHeader);
                        response.setHandler(ResponseContentHandlerProviderEnum.getByResponseType(response.getResponseType().toMediaType()));
                    }
                }
            }
            LOGGER.trace(() -> "match response type: " + responseType);

            // content type
            var requestType = routerInfo.getRequestType();
            LOGGER.trace(() -> "requestType: " + requestType);
            var contentType = routerRequest.getContentType();
            LOGGER.trace(() -> "contentType: " + contentType);
            Set<MediaTypeInfo> defaultContentTypes = configuration.getDefaultContentTypes();
            var matchRequestType = contentType.isAny() || requestType.info().includes(contentType) ||
                    (!MediaType.ANY.isSame(requestType) && requestType.isSame(contentType))
                    || (!MediaType.ANY.isSame(requestType) && !contentType.isAny() && requestType.isSame(contentType.toMediaType()))
                    || (
                    (MediaType.ANY.isSame(requestType) && !contentType.isAny() &&
                            (MediaTypeInfo.contains(defaultContentTypes, contentType) || configuration.isAcceptClientContentType()))
            );
            if (!matchRequestType) {
                continue;
            }
            var flag = (MediaType.ANY.isSame(requestType) && !MediaType.ANY.isSame(contentType) &&
                    (MediaTypeInfo.contains(defaultContentTypes, contentType) || configuration.isAcceptClientContentType()));
            if (flag) {
                routerInfo.setRequestType(contentType.toMediaType());
            }
            LOGGER.trace(() -> "match request type: " + requestType.info());
            var request = routerInfo.getRequest();
            if (request != null)
                routerRequest.setPathAttributes(request.getPathAttributes());
            result = routerInfo;
            break;
        }

        if (result == null) {
            LOGGER.warn(() -> "router uri(" + url + ") not found!");
            result = RouterErrorResponseHandler.resourceNotFound(routerRequest);
        } else {
            result.setRequest(routerRequest);
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("matched router info: " + result.toString());
        }
        result.setDefaultResponseTypes(configuration.getDefaultResponseTypes());
        return result;
    }

    /*private static void filterOverloadMethod(Set<RouterInfo> routerInfoSet) {
        for (RouterInfo routerInfo : routerInfoSet) {
            routerInfo.getMethod();
        }
        var routerArray = routerInfoSet.toArray(new RouterInfo[0]);
        for (int i = 0; i < routerInfoSet.size(); i++) {
            var routerI = routerArray[i];
            Method methodI = routerI.getMethod();
            for (int j = i; j < routerInfoSet.size(); j++) {
                var routerJ = routerArray[i];
                Method methodJ = routerI.getMethod();
            }
        }
    }*/

    public static Set<RouterInfo> matchRouterPath(String url, Set<RouterInfo> routerInfos, RouterRequest routerRequest) {
        Set<RouterInfo> result = new HashSet<>();

        // if no path variable and no matrix
        for (RouterInfo routerInfo : routerInfos) {
            if (matchedRawPath(routerInfo, url, false)) {
                var copy = routerRequest.copy();
                if (copy == null) continue;
                routerInfo.setRequest(copy);
                result.add(routerInfo.clone());
            }
        }

        if (!result.isEmpty())
            return result;

        // if has no matched, then match variable path
        for (RouterInfo routerInfo : routerInfos) {
            RouterRequest copy = routerRequest.copy();
            if (copy == null) continue;
            if (matchedSameLengthVariablePath(routerInfo, url, false, copy)) {
                result.add(routerInfo.clone());
            }
        }

        if (!result.isEmpty())
            return result;

        for (RouterInfo routerInfo : routerInfos) {
            RouterRequest copy = routerRequest.copy();
            if (copy == null) continue;
            if (matchedNotSameLengthVariablePath(routerInfo, url, false, copy)) {
                result.add(routerInfo.clone());
            }
        }

        if (!result.isEmpty())
            return result;

        // if has no matched, then remove matrix
        url = UriUtils.getPathsWithoutMatrix(url);
        for (RouterInfo routerInfo : routerInfos) {
            var matched = matchRouterPath(url, routerInfo, routerRequest, true);
            if (matched) {
                result.add(routerInfo.clone());
            }
        }
        return result;
    }

    /*public static boolean matchRouterPath(String url, RouterInfo routerInfo, boolean withoutMatrix) {
        var matchUrl = false;

        // if has no matched, then match no variable path
        matchUrl = matchedRawPath(routerInfo, url, withoutMatrix);
    }*/

    public static boolean matchRouterPath(String url, RouterInfo routerInfo, RouterRequest routerRequest,
                                          boolean withoutMatrix) {

        List<RouterPathFragments> paths = routerInfo.getPaths();

        var matchUrl = false;
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
            RouterRequest copy = routerRequest.copy();
            if (copy != null)
                matchUrl = matchedSameLengthVariablePath(routerInfo, url, withoutMatrix, copy);
        }

        if (!matchUrl) {
            RouterRequest copy = routerRequest.copy();
            if (copy != null)
                matchUrl = matchedNotSameLengthVariablePath(routerInfo, url, withoutMatrix, copy);
        }

        // if has no matched, then remove matrix
        if (!matchUrl && !withoutMatrix) {
            url = UriUtils.getPathsWithoutMatrix(url);
            matchUrl = matchRouterPath(url, routerInfo, routerRequest, true);
        }
        return matchUrl;
    }


    private static boolean matchedSameLengthVariablePath(RouterInfo routerInfo, String url, boolean withoutMatrix,
                                                         RouterRequest routerRequest) {
        if (withoutMatrix) {
            url = UriUtils.getPathsWithoutMatrix(url);
        }

        List<UriPathFragment> urlPathFragments = UriUtils.getPathFragment(url);

        int urlPathFragmentsSize = urlPathFragments.size();
        // if has no matched, then match variable path

        List<RouterPathFragments> paths = routerInfo.getPaths();

        out:
        for (var pattern : paths) {
            if (pattern.hasVariable()) {
                List<UriPathFragment> pathFragments = pattern.getPathFragments();
                // same length
                if (urlPathFragmentsSize == pathFragments.size()) {
                    Map<String, List<String>> requestPathAttributes = new HashMap<>();
                    for (int i = 0; i < urlPathFragmentsSize; i++) {
                        UriPathFragment iUriPathFragment = pathFragments.get(i);
                        UriPathFragment jUriPathFragment = urlPathFragments.get(i);
                        if (!iUriPathFragment.hasVariable()) {
                            if (!iUriPathFragment.getFragment().equals(jUriPathFragment.getFragment()))
                                continue out;
                        } else {
                            if (iUriPathFragment.getPattern().matcher(url).find()) {
                                var pathAttributes = RouterPathSplicer.getPathVariable(iUriPathFragment, jUriPathFragment.getFragment());
                                if (pathAttributes.isEmpty() || pathAttributes.size() != iUriPathFragment.getUriPathVariable().size()) {
                                    continue out;
                                }
                                mergePathAttributes(requestPathAttributes, pathAttributes);
                            } else {
                                continue out;
                            }
                        }

                    }
                    routerRequest.getPathAttributes().putAll(requestPathAttributes);
                    routerInfo.setRequest(routerRequest);
                    return true;
                }
            }
        }
        return false;
    }

    private static void mergePathAttributes(Map<String, List<String>> requestPathAttributes, Map<String, List<String>> pathAttributes) {
        Set<String> keys = pathAttributes.keySet();
        for (String key : keys) {
            if (requestPathAttributes.containsKey(key)) {
                List<String> value = requestPathAttributes.get(key);
                value.addAll(pathAttributes.get(key));
            } else {
                requestPathAttributes.put(key, pathAttributes.get(key));
            }
        }
    }

    private static boolean matchedNotSameLengthVariablePath(RouterInfo routerInfo, String url,
                                                            boolean withoutMatrix, RouterRequest routerRequest) {
        if (withoutMatrix) {
            url = UriUtils.getPathsWithoutMatrix(url);
        }

        // if has no matched, then match variable path
        List<RouterPathFragments> paths = routerInfo.getPaths();
        for (var pattern : paths) {
            if ((pattern.hasVariable() || pattern.isDynamic()) && pattern.matchUrl(url)) {
                Map<String, List<String>> requestPathAttributes = new HashMap<>();
                for (UriPathFragment pathFragment : pattern.getPathFragments()) {
                    if (pathFragment.hasVariable()) {
                        var pathAttributes = RouterPathSplicer.getPathVariable(pathFragment, url);
                        if (pathAttributes.isEmpty()) {
                            continue;
                        }
                        mergePathAttributes(requestPathAttributes, pathAttributes);
                    }
                }
                routerRequest.getPathAttributes().putAll(requestPathAttributes);
                routerInfo.setRequest(routerRequest);
                return true;
            }
        }
        return false;
    }

    // if no path variable and no matrix
    private static boolean matchedRawPath(RouterInfo routerInfo, String url, boolean withoutMatrix) {
        if (withoutMatrix) {
            url = UriUtils.getPathsWithoutMatrix(url);
        }
        List<RouterPathFragments> paths = routerInfo.getPaths();
        // if has no matched, then match no variable path
        for (var pattern : paths) {
            if (!pattern.hasVariable()) {
                if (pattern.getRawPath().equalsIgnoreCase(url)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static RouterResponse handleRouter(RouterInfo routerInfo, ApplicationContext applicationContext) {
        RouterResponse routerResponse = routerInfo.getResponse();
        GlobalBeanFactory globalBeanFactory = applicationContext.getGlobalBeanFactory();
        ErrorResponseCallback callback = globalBeanFactory.factoryIfPresent(ErrorResponseCallback.class);
        if (routerResponse.isError()) {
            return RouterErrorResponseHandler.handleError(routerInfo, callback);
        }
        try {
            HttpStatus status = routerResponse.getStatus();
            if (status == null)
                routerResponse.setStatus(HttpStatus.OK);

            routerResponse.setResponseType(routerInfo.getResponse().getResponseType());
            RouterInvoker invoker = new RouterInvoker(routerInfo);
            invoker.action(routerResponse, globalBeanFactory, applicationContext.getClassLoader());
            return routerResponse;
        } catch (Throwable e) {
            LOGGER.error("", e);
            var exception = RouterErrorResponseHandler.exception(routerInfo.getRequest(), e);
            return RouterErrorResponseHandler.handleError(exception, callback);
        }
    }
}