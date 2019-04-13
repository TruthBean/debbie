package com.truthbean.code.debbie.httpclient;

import com.truthbean.code.debbie.core.proxy.AbstractMethodExecutor;
import com.truthbean.code.debbie.core.reflection.InvokedParameter;
import com.truthbean.code.debbie.httpclient.annotation.HttpClientRouter;
import com.truthbean.code.debbie.mvc.request.RequestParam;
import com.truthbean.code.debbie.mvc.request.RequestParamType;
import com.truthbean.code.debbie.mvc.router.Router;
import com.truthbean.code.debbie.mvc.router.RouterPathSplicer;

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

                    RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
                    if (requestParam != null) {
                        var type = parameter.getType();
                        var name = requestParam.name();
                        if ("".equals(name.trim())) {
                            name = parameter.getName();
                        }
                        invokedParameter.setName(name);
                        invokedParameter.setIndex(i);
                        invokedParameter.setAnnotation(requestParam);
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
        List result = new ArrayList();
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
                if (annotation instanceof RequestParam) {
                    RequestParam requestParam = (RequestParam) annotation;
                    var type = requestParam.paramType();
                    if (type == RequestParamType.HEAD) {
                        var headerName = requestParam.name();
                        if (arg instanceof String) {
                            request.addHeader(headerName, List.of((String) arg));
                        } else if (arg instanceof List) {
                            request.addHeader(headerName, (List<String>) arg);
                        } else if (arg instanceof Map) {
                            request.addHeaders((Map<String, List<String>>) arg);
                        } else {
                            request.addHeader(headerName, List.of(arg.toString()));
                        }
                    } else if (type == RequestParamType.COOKIE) {
                        var cookieName = requestParam.name();
                        var cookie = new HttpCookie(cookieName, arg.toString());
                        request.addCookie(cookie);
                    } else if (type == RequestParamType.QUERY) {
                        var queryName = requestParam.name();
                        if (arg instanceof String) {
                            request.addQueries(queryName, List.of((String) arg));
                        } else if (arg instanceof List) {
                            request.addHeader(queryName, (List<String>) arg);
                        } else if (arg instanceof Map) {
                            request.addQueries((Map<String, List<String>>) arg);
                        } else {
                            request.addHeader(queryName, List.of(arg.toString()));
                        }
                    } else if (type == RequestParamType.PARAM) {
                        var paramName = requestParam.name();
                        if (arg instanceof List) {
                            request.addParameters(paramName, (List) arg);
                        } else if (arg instanceof Map) {
                            request.addParameters((Map<String, List>) arg);
                        } else {
                            request.addParameters(paramName, List.of(arg));
                        }
                    } else if (type == RequestParamType.BODY) {
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
