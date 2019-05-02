package com.truthbean.debbie.httpclient;

import com.truthbean.debbie.core.proxy.AbstractMethodExecutor;
import com.truthbean.debbie.core.reflection.InvokedParameter;
import com.truthbean.debbie.httpclient.annotation.HttpClientRouter;
import com.truthbean.debbie.mvc.request.RequestParameter;
import com.truthbean.debbie.mvc.request.RequestParameterType;
import com.truthbean.debbie.mvc.router.Router;
import com.truthbean.debbie.mvc.router.RouterPathSplicer;

import java.io.File;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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

        Router router = method.getAnnotation(Router.class);
        if (router == null) {
            throw new IllegalArgumentException(method.getName() + " have no @Router annotation ");
        }
        this.httpClientAction = new HttpClientAction(this.configuration);

        var urls = RouterPathSplicer.splicePaths(Arrays.asList(routerBaseUrl), router);
        for (String url : urls) {
            var request = new HttpClientRequest();
            request.setMethod(router.method());
            request.setUrl(url);
            request.setContentType(router.requestType());
            request.setResponseType(router.responseType());

            var parameters = method.getParameters();
            if (parameters != null) {
                for (int i = 0; i < parameters.length; i++) {
                    var parameter = parameters[i];
                    var invokedParameter = new InvokedParameter();

                    RequestParameter requestParameter = parameter.getAnnotation(RequestParameter.class);
                    if (requestParameter != null) {
                        var type = parameter.getType();
                        var name = requestParameter.name();
                        if (name.isBlank()) {
                            name = parameter.getName();
                        }
                        invokedParameter.setName(name);
                        invokedParameter.setIndex(i);
                        invokedParameter.setAnnotation(requestParameter);
                        invokedParameter.setType(type);
                    }

                    request.setInvokedParameter(invokedParameter);
                }
            }
            requests.add(request);
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

                Annotation annotation = parameter.getAnnotation();
                if (annotation instanceof RequestParameter) {
                    RequestParameter requestParameter = (RequestParameter) annotation;
                    var type = requestParameter.paramType();
                    if (type == RequestParameterType.HEAD) {
                        var headerName = requestParameter.name();
                        if (arg instanceof String) {
                            request.addHeader(headerName, List.of((String) arg));
                        } else if (arg instanceof List) {
                            request.addHeader(headerName, (List<String>) arg);
                        } else if (arg instanceof Map) {
                            request.addHeaders((Map<String, List<String>>) arg);
                        } else {
                            request.addHeader(headerName, List.of(arg.toString()));
                        }
                    } else if (type == RequestParameterType.COOKIE) {
                        var cookieName = requestParameter.name();
                        var cookie = new HttpCookie(cookieName, arg.toString());
                        request.addCookie(cookie);
                    } else if (type == RequestParameterType.QUERY) {
                        var queryName = requestParameter.name();
                        if (arg instanceof String) {
                            request.addQueries(queryName, List.of((String) arg));
                        } else if (arg instanceof List) {
                            request.addHeader(queryName, (List<String>) arg);
                        } else if (arg instanceof Map) {
                            request.addQueries((Map<String, List<String>>) arg);
                        } else {
                            request.addHeader(queryName, List.of(arg.toString()));
                        }
                    } else if (type == RequestParameterType.PARAM) {
                        var paramName = requestParameter.name();
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
