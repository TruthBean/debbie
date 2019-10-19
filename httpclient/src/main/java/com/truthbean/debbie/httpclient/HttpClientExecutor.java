package com.truthbean.debbie.httpclient;

import com.truthbean.debbie.mvc.request.RequestParameterInfo;
import com.truthbean.debbie.mvc.router.RouterAnnotationInfo;
import com.truthbean.debbie.proxy.AbstractMethodExecutor;
import com.truthbean.debbie.reflection.ExecutableArgument;
import com.truthbean.debbie.httpclient.annotation.HttpClientRouter;
import com.truthbean.debbie.mvc.request.HttpMethod;
import com.truthbean.debbie.mvc.request.RequestParameterType;
import com.truthbean.debbie.mvc.router.RouterPathSplicer;

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

    private HttpClientConfiguration configuration;
    private HttpClientAction httpClientAction;

    private List<HttpClientRequest> requests = new ArrayList<>();

    public HttpClientExecutor(Class<T> interfaceType, Method method, Object configuration) {
        super(interfaceType, method, configuration);

        String[] routerBaseUrl = new String[0];
        HttpClientRouter httpClientRouter = interfaceType.getAnnotation(HttpClientRouter.class);
        if (httpClientRouter != null) {
            routerBaseUrl = httpClientRouter.baseUrl();
        }

        if (configuration instanceof HttpClientConfiguration) {
            this.configuration = (HttpClientConfiguration) configuration;
        } else {
            this.configuration = new HttpClientConfiguration();
        }

        RouterAnnotationInfo router = RouterAnnotationInfo.getRouterAnnotation(method);
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

        var routerMethod = router.method();
        for (String url : urls) {
            for (HttpMethod httpMethod : routerMethod) {
                var request = new HttpClientRequest();
                request.setMethod(httpMethod);
                request.setUrl(url);
                request.setContentType(router.requestType().info());
                request.setResponseType(router.responseType().info());

                var parameters = method.getParameters();
                if (parameters != null) {
                    for (int i = 0; i < parameters.length; i++) {
                        var parameter = parameters[i];
                        var invokedParameter = new ExecutableArgument();

                        RequestParameterInfo requestParameter = RequestParameterInfo.fromParameterAnnotation(parameter);
                        if (requestParameter != null) {
                            var type = parameter.getType();
                            var name = requestParameter.value();
                            if (name.isBlank()) {
                                name = requestParameter.name();
                            }
                            if (name.isBlank()) {
                                name = parameter.getName();
                            }
                            invokedParameter.setName(name);
                            invokedParameter.setIndex(i);
                            invokedParameter.setAnnotation(requestParameter.getAnnotation());
                            invokedParameter.setType(type);
                        }

                        request.setInvokedParameter(invokedParameter);
                    }
                }
                requests.add(request);
            }
        }
    }

    @Override
    public Object execute(Object object, Object... args) {
        List<Object> result = new ArrayList<>();
        for (var request : requests) {
            var parameter = request.getInvokedParameter();
            if (args == null) {
                result.add(httpClientAction.action(request));
                continue;
            }
            for (int i = 0; i < args.length; i++) {
                var arg = args[i];
                if (parameter.getIndex() != i) {
                    continue;
                }

                RequestParameterInfo requestParameter = RequestParameterInfo.fromExecutableArgumentAnnotation(parameter);
                if (requestParameter != null) {
                    var type = requestParameter.paramType();
                    var header = request.getHeader();
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
                        var cookie = new HttpCookie(cookieName, arg.toString());
                        request.addCookie(cookie);
                    } else if (type == RequestParameterType.QUERY) {
                        var queryName = requestParameter.value();
                        if (queryName.isBlank()) {
                            queryName = requestParameter.name();
                        }
                        if (arg instanceof String) {
                            request.addQueries(queryName, List.of((String) arg));
                        } else if (arg instanceof List) {
                            header.addHeader(queryName, (List<String>) arg);
                        } else if (arg instanceof Map) {
                            request.addQueries((Map<String, List<String>>) arg);
                        } else {
                            header.addHeader(queryName, List.of(arg.toString()));
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
                            request.setTextBody(arg.toString());
                        }
                    }
                }
            }
            result.add(httpClientAction.action(request));
        }
        return result;
    }
}
