package com.truthbean.debbie.mvc.router;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.io.MediaType;
import com.truthbean.debbie.io.MediaTypeInfo;
import com.truthbean.debbie.io.ResourcesHandler;
import com.truthbean.debbie.mvc.response.provider.NothingResponseHandler;
import com.truthbean.debbie.net.uri.UriPathFragment;
import com.truthbean.debbie.net.uri.UriUtils;
import com.truthbean.debbie.mvc.MvcConfiguration;
import com.truthbean.debbie.mvc.request.HttpMethod;
import com.truthbean.debbie.mvc.request.RouterRequest;
import com.truthbean.debbie.mvc.response.RouterErrorResponseHandler;
import com.truthbean.debbie.mvc.response.RouterResponse;
import com.truthbean.debbie.mvc.response.provider.ResponseContentHandlerProviderEnum;
import com.truthbean.debbie.mvc.url.RouterPathFragments;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        LOGGER.debug("router uri: {}", url);
        for (Map.Entry<String, String> entry : mappingAndLocation.entrySet()) {
            var mapping = entry.getKey();
            var location = entry.getValue();
            mapping = mapping.replace("**", "");
            if (url.startsWith(mapping)) {
                String resource = location + url.substring(mapping.length());
                var result = ResourcesHandler.handleStaticBytesResource(resource);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    public static RouterInfo getMatchedRouter(RouterRequest routerRequest, MvcConfiguration configuration) {
        var url = routerRequest.getUrl();
        LOGGER.debug("router uri: {}", url);
        RouterInfo result = null;
        var set = MvcRouterRegister.getRouterInfoSet();
        var routerInfos = matchRouterPath(url, set, routerRequest);

        for (var routerInfo : routerInfos) {
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
            if (response.getHandler() == null || response.getHandler().getClass() == NothingResponseHandler.class) {
                if (responseType.isAny() && responseTypeInRequestHeader.isAny()) {
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

            routerRequest.setPathAttributes(routerInfo.getRequest().getPathAttributes());
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

        // if no path variable and no matrix
        for (RouterInfo routerInfo : routerInfos) {
            if (matchedRawPath(routerInfo, url, false)) {
                routerInfo.setRequest(routerRequest.copy());
                result.add(routerInfo.clone());
            }
        }

        if (!result.isEmpty())
            return result;

        // if has no matched, then match variable path
        for (RouterInfo routerInfo : routerInfos) {
            if (matchedSameLengthVariablePath(routerInfo, url, false, routerRequest.copy())) {
                result.add(routerInfo.clone());
            }
        }

        if (!result.isEmpty())
            return result;

        for (RouterInfo routerInfo : routerInfos) {
            if (matchedNotSameLengthVariablePath(routerInfo, url, false, routerRequest.copy())) {
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
            matchUrl = matchedSameLengthVariablePath(routerInfo, url, withoutMatrix, routerRequest.copy());
        }

        if (!matchUrl) {
            matchUrl = matchedNotSameLengthVariablePath(routerInfo, url, withoutMatrix, routerRequest.copy());
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
            if (pattern.getPattern().matcher(url).find()) {
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
            invoker.action(routerResponse, handler);
            return routerResponse;
        } catch (Exception e) {
            LOGGER.error("", e);
            var exception = RouterErrorResponseHandler.exception(routerInfo.getRequest(), e);
            responseValue = exception.getErrorInfo();
        }

        routerResponse.setContent(responseValue);
        return routerResponse;
    }
}