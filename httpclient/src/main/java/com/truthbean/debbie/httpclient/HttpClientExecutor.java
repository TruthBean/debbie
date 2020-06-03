/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.httpclient;

import com.truthbean.debbie.data.transformer.DataTransformerFactory;
import com.truthbean.debbie.httpclient.annotation.HttpClientRouter;
import com.truthbean.debbie.io.MediaType;
import com.truthbean.debbie.io.MediaTypeInfo;
import com.truthbean.debbie.mvc.request.HttpMethod;
import com.truthbean.debbie.mvc.request.RequestParameterInfo;
import com.truthbean.debbie.mvc.request.RequestParameterType;
import com.truthbean.debbie.mvc.router.RouterAnnotationInfo;
import com.truthbean.debbie.mvc.router.RouterPathSplicer;
import com.truthbean.debbie.proxy.AbstractMethodExecutor;
import com.truthbean.debbie.reflection.ExecutableArgument;
import com.truthbean.debbie.reflection.TypeHelper;
import com.truthbean.debbie.util.JacksonUtils;
import com.truthbean.debbie.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.HttpCookie;
import java.util.*;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class HttpClientExecutor<T> extends AbstractMethodExecutor {

    private final HttpClientConfiguration configuration;
    private final HttpClientAction httpClientAction;

    private final List<HttpClientRequest> requests = new ArrayList<>();

    public HttpClientExecutor(final Class<T> interfaceType, final Method method, final Object configuration) {
        super(interfaceType, method, configuration);

        String[] routerBaseUrl = new String[0];
        final HttpClientRouter httpClientRouter = interfaceType.getAnnotation(HttpClientRouter.class);
        if (httpClientRouter != null) {
            routerBaseUrl = httpClientRouter.baseUrl();
        }

        if (configuration instanceof HttpClientConfiguration) {
            this.configuration = (HttpClientConfiguration) configuration;
        } else {
            this.configuration = new HttpClientConfiguration();
        }

        final RouterAnnotationInfo router = RouterAnnotationInfo.getRouterAnnotation(method);
        if (router == null) {
            throw new IllegalArgumentException(method.getName() + " have no Router annotation ");
        }
        this.httpClientAction = new HttpClientAction(this.configuration);

        Set<String> urls;
        if (routerBaseUrl.length > 0) {
            urls = RouterPathSplicer.splicePaths(Arrays.asList(routerBaseUrl), router);
        } else {
            urls = RouterPathSplicer.splicePaths(router);
        }

        final var routerMethod = router.method();
        for (final String url : urls) {
            for (final HttpMethod httpMethod : routerMethod) {
                final var request = new HttpClientRequest();
                request.setMethod(httpMethod);
                request.setUrl(url);
                request.setContentType(router.requestType().info());
                request.setResponseType(router.responseType().info());

                final var parameters = method.getParameters();
                if (parameters != null) {
                    for (int i = 0; i < parameters.length; i++) {
                        final var parameter = parameters[i];
                        final Class<?>[] parameterTypes = method.getParameterTypes();
                        final var invokedParameter = new ExecutableArgument();

                        final RequestParameterInfo requestParameter = RequestParameterInfo.fromParameterAnnotation(parameter);
                        if (requestParameter != null) {
                            final var type = parameterTypes[i];
                            var name = requestParameter.value();
                            if (name.isBlank()) {
                                name = requestParameter.name();
                            }
                            if (name.isBlank()) {
                                name = parameter.getName();
                            }
                            if (!name.isBlank()) {
                                invokedParameter.setName(name);
                            }
                            invokedParameter.setIndex(i);
                            invokedParameter.setAnnotation(requestParameter.getAnnotation());
                            invokedParameter.setType(type);
                        }
                        String name = invokedParameter.getName();
                        if (!StringUtils.hasText(name)) {
                            if (parameter.isNamePresent()) {
                                invokedParameter.setName(parameter.getName());
                            }
                        }

                        invokedParameter.setStack("param(" + invokedParameter.getType() + "[" + i + "]" + name + ") \nin method(" + method.toString() + ")\n");

                        request.addInvokedParameter(invokedParameter);
                    }
                }
                request.sortInvokedParameters();
                requests.add(request);
            }
        }
    }

    private void setParameterValue(final Object... args) {
        for (final var request : requests) {
            for (int i = 0; i < args.length; i++) {
                final var parameter = request.getInvokedParameter(i);
                final var arg = args[i];
                if (parameter != null && parameter.getRawType().isInstance(arg)) {
                    parameter.setValue(arg);
                }
            }
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public <T> T execute(final Object object, final Class<T> returnType, final Object... args) {
        final List<HttpClientResponse> result = new ArrayList<>();
        MediaTypeInfo responseType = MediaType.TEXT_ANY_UTF8.info();

        setParameterValue(args);

        for (final var request : requests) {
            responseType = request.getResponseType();
            if (responseType.isAny()) {
                responseType = MediaType.TEXT_ANY_UTF8.info();
            }

            if (args == null) {
                result.add(httpClientAction.action(request, responseType));
                continue;
            }

            final var parameters = request.getInvokedParameters();
            for (final ExecutableArgument parameter : parameters) {
                final var arg = parameter.getValue();
                if (arg == null) continue;

                final RequestParameterInfo requestParameter = RequestParameterInfo.fromExecutableArgumentAnnotation(parameter);
                if (requestParameter != null) {
                    final var type = requestParameter.paramType();
                    final var header = request.getHeader();
                    if (type == RequestParameterType.HEAD) {
                        var headerName = requestParameter.value();
                        if (headerName.isBlank()) {
                            headerName = requestParameter.name();
                        }
                        if (arg instanceof String) {
                            header.addHeader(headerName, List.of((String) arg));
                        } else if (arg instanceof List) {
                            header.addHeader(headerName, (List<String>) arg);
                        } else if (arg instanceof Map) {
                            header.addHeaders((Map<String, List<String>>) arg);
                        } else {
                            header.addHeader(headerName, List.of(arg.toString()));
                        }
                    } else if (type == RequestParameterType.COOKIE) {
                        var cookieName = requestParameter.value();
                        if (cookieName.isBlank()) {
                            cookieName = requestParameter.name();
                        }
                        final var cookie = new HttpCookie(cookieName, arg.toString());
                        request.addCookie(cookie);
                    } else if (type == RequestParameterType.QUERY) {
                        var queryName = requestParameter.value();
                        if (queryName.isBlank()) {
                            queryName = requestParameter.name();
                        }
                        if (arg instanceof String) {
                            request.addQueries(queryName, List.of((String) arg));
                        } else if (arg instanceof List) {
                            request.addQueries(queryName, (List<String>) arg);
                        } else if (arg instanceof Map) {
                            request.addQueries((Map<String, List<String>>) arg);
                        } else {
                            request.addQueries(queryName, List.of(arg.toString()));
                        }
                    } else if (type == RequestParameterType.PARAM) {
                        var paramName = requestParameter.value();
                        if (paramName.isBlank()) {
                            paramName = requestParameter.name();
                        }
                        if (arg instanceof List) {
                            request.addParameters(paramName, (List) arg);
                        } else if (arg instanceof Map) {
                            request.addParameters((Map<String, List>) arg);
                        } else {
                            request.addParameters(paramName, List.of(arg));
                        }
                    } else if (type == RequestParameterType.BODY) {
                        if (arg instanceof File) {
                            request.setFileBody((File) arg);
                        } else if (arg instanceof InputStream) {
                            request.setInputStreamBody((InputStream) arg);
                        } else if (arg instanceof String) {
                            request.setTextBody((String) arg);
                        } else {
                            final MediaType mediaType = requestParameter.bodyType();
                            if (mediaType.isSame(MediaType.APPLICATION_JSON_UTF8)) {
                                request.setTextBody(JacksonUtils.toJson(arg));
                            } else if (mediaType.isSame(MediaType.APPLICATION_XML_UTF8)) {
                                request.setTextBody(JacksonUtils.toXml(arg));
                            } else {
                                try {
                                    request.setTextBody(DataTransformerFactory.transform(arg, String.class));
                                } catch (final Exception e) {
                                    request.setTextBody(arg.toString());
                                }
                            }
                        }
                    }
                }
            }

            result.add(httpClientAction.action(request, responseType));
        }
        return getResult(result, returnType, responseType);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private <T> T getSingleResult(final HttpClientResponse response, Class<T> returnType, final MediaTypeInfo responseType) {
        if (response != null) {
            final Object o = response.getBody();
            if (o instanceof String) {
                final String str = (String) o;
                if (responseType.isSameMediaType(MediaType.APPLICATION_JSON_UTF8)) {
                    return JacksonUtils.jsonToBean(str, returnType);
                }
                if (responseType.isSameMediaType(MediaType.APPLICATION_XML_UTF8)) {
                    return JacksonUtils.xmlToBean(str, returnType);
                }
                if (TypeHelper.isRawBaseType(returnType)) {
                    returnType = (Class<T>) TypeHelper.getWrapperClass(returnType);
                }
                return DataTransformerFactory.transform(str, returnType);
            } else if (o instanceof InputStream) {
                return (T) o;
            } else {
                throw new IllegalArgumentException("not support " + responseType + " yet! ");
            }
        } else {
            LOGGER.warn("response is null");
            return null;
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private <T> T getResult(final List<HttpClientResponse> responseList, final Class<T> returnType, final MediaTypeInfo responseType) {
        if (returnType == null || returnType == Void.class || responseList.isEmpty()) {
            return null;
        }

        if (responseList.size() == 1) {
            final HttpClientResponse response = responseList.get(0);
            return getSingleResult(response, returnType, responseType);
        } else if (Iterable.class.isAssignableFrom(returnType) || returnType == Object.class) {
            final List<T> result = new ArrayList<>();
            for (final HttpClientResponse response : responseList) {
                result.add(getSingleResult(response, returnType, responseType));
            }
            return (T) result;
        } else {
            throw new IllegalArgumentException("not support " + responseType + " yet! ");
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientExecutor.class);
}
