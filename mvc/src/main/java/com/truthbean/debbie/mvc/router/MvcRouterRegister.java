/**
 * Copyright (c) 2022 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mvc.router;

import com.truthbean.debbie.annotation.AnnotationInfo;
import com.truthbean.debbie.bean.BeanInfo;
import com.truthbean.debbie.bean.BeanInfoManager;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.io.MediaType;
import com.truthbean.debbie.mvc.MvcConfiguration;
import com.truthbean.debbie.mvc.request.BodyParameter;
import com.truthbean.debbie.mvc.request.HttpMethod;
import com.truthbean.debbie.mvc.request.RequestParameter;
import com.truthbean.debbie.mvc.request.RequestParameterType;
import com.truthbean.debbie.mvc.response.ResponseContentHandlerFactory;
import com.truthbean.debbie.mvc.response.ResponseTypeException;
import com.truthbean.debbie.mvc.response.RouterResponse;
import com.truthbean.debbie.mvc.response.provider.ResponseContentHandlerProviderEnum;
import com.truthbean.debbie.mvc.url.RouterPathFragments;
import com.truthbean.debbie.reflection.ClassInfo;
import com.truthbean.debbie.reflection.ExecutableArgument;
import com.truthbean.debbie.watcher.Watcher;
import com.truthbean.debbie.watcher.WatcherType;
import com.truthbean.Logger;
import com.truthbean.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.*;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class MvcRouterRegister {
    private final Set<RouterInfo> routerInfoSet = new HashSet<>();

    private static volatile MvcRouterRegister instance;
    private final MvcConfiguration webConfiguration;

    private MvcRouterRegister(MvcConfiguration webConfiguration) {
        this.webConfiguration = webConfiguration;
    }

    public static void registerRouter(MvcConfiguration webConfiguration, ApplicationContext applicationContext) {
        // create instalace
        getInstance(webConfiguration);
        // config
        BeanInfoManager beanInfoManager = applicationContext.getBeanInfoManager();
        Set<BeanInfo<?>> beanInfoSet = beanInfoManager.getAnnotatedClass(Router.class);
        for (BeanInfo<?> beanInfo : beanInfoSet) {
            if (beanInfo instanceof ClassInfo) {
                ClassInfo<?> classInfo = (ClassInfo<?>) beanInfo;
                Map<Class<? extends Annotation>, AnnotationInfo> classAnnotations = classInfo.getClassAnnotations();
                Watcher watcher = classInfo.getClassAnnotation(Watcher.class);
                if (watcher == null || watcher.type() == WatcherType.HTTP) {
                    registerRouter(HttpRouterParser.parse(classAnnotations), classInfo, webConfiguration, applicationContext);
                }
            }
        }
    }

    public static MvcRouterRegister getInstance(MvcConfiguration webConfiguration) {
        if (instance == null) {
            synchronized (MvcRouterRegister.class) {
                if (instance == null) {
                    instance = new MvcRouterRegister(webConfiguration);
                }
            }
        }
        return instance;
    }

    public MvcRouterRegister router(HttpMethod[] method, String[] urlPattern, MvcRouter router) {
        RouterInfo info = new RouterInfo();
        RouterAnnotationInfo annotationInfo = new RouterAnnotationInfo();
        annotationInfo.setMethod(method);
        annotationInfo.setUrlPatterns(urlPattern);
        info.setAnnotationInfo(annotationInfo);

        List<RouterPathFragments> routerPathFragments =
                RouterPathSplicer.splicePathFragment(webConfiguration.getDispatcherMapping(), null, annotationInfo, router.getClass().getName());
        info.setPaths(routerPathFragments);

        RouterResponse response = new RouterResponse();

        setTemplate(response, annotationInfo, webConfiguration);
        info.setResponse(response);

        var defaultResponseTypes = webConfiguration.getDefaultResponseTypes();
        var defaultContentTypes = webConfiguration.getDefaultContentTypes();
        if (!defaultContentTypes.isEmpty()) {
            info.setRequestType(defaultResponseTypes.iterator().next().toMediaType());
        } else {
            if (!webConfiguration.isAcceptClientContentType()) {
                throw new RuntimeException("requestType cannot be MediaType.ANY. Or config default request type. Or accept client content type.");
            } else {
                info.setRequestType(MediaType.ANY);
            }
        }

        if (info.getRequestType() == null) {
            info.setRequestType(MediaType.ANY);
        }

        info.setExecutor(new SimpleRouterExecutor(router));

        LOGGER.debug(() -> "register router: " + info);
        routerInfoSet.add(info);
        return this;
    }

    public MvcRouterRegister get(String urlPattern, MvcRouter router) {
        return this.router(new HttpMethod[]{HttpMethod.GET}, new String[]{urlPattern}, router);
    }

    public MvcRouterRegister post(String urlPattern, MvcRouter router) {
        return this.router(new HttpMethod[]{HttpMethod.POST}, new String[]{urlPattern}, router);
    }

    public MvcRouterRegister connect(String urlPattern, MvcRouter router) {
        return this.router(new HttpMethod[]{HttpMethod.CONNECT}, new String[]{urlPattern}, router);
    }

    public MvcRouterRegister delete(String urlPattern, MvcRouter router) {
        return this.router(new HttpMethod[]{HttpMethod.DELETE}, new String[]{urlPattern}, router);
    }

    public MvcRouterRegister head(String urlPattern, MvcRouter router) {
        return this.router(new HttpMethod[]{HttpMethod.HEAD}, new String[]{urlPattern}, router);
    }

    public MvcRouterRegister options(String urlPattern, MvcRouter router) {
        return this.router(new HttpMethod[]{HttpMethod.OPTIONS}, new String[]{urlPattern}, router);
    }

    public MvcRouterRegister patch(String urlPattern, MvcRouter router) {
        return this.router(new HttpMethod[]{HttpMethod.PATCH}, new String[]{urlPattern}, router);
    }

    public MvcRouterRegister put(String urlPattern, MvcRouter router) {
        return this.router(new HttpMethod[]{HttpMethod.PUT}, new String[]{urlPattern}, router);
    }

    public MvcRouterRegister trace(String urlPattern, MvcRouter router) {
        return this.router(new HttpMethod[]{HttpMethod.TRACE}, new String[]{urlPattern}, router);
    }

    public MvcRouterRegister get(String[] urlPattern, MvcRouter router) {
        return this.router(new HttpMethod[]{HttpMethod.GET}, urlPattern, router);
    }

    public MvcRouterRegister post(String[] urlPattern, MvcRouter router) {
        return this.router(new HttpMethod[]{HttpMethod.POST}, urlPattern, router);
    }

    public MvcRouterRegister connect(String[] urlPattern, MvcRouter router) {
        return this.router(new HttpMethod[]{HttpMethod.CONNECT}, urlPattern, router);
    }

    public MvcRouterRegister delete(String[] urlPattern, MvcRouter router) {
        return this.router(new HttpMethod[]{HttpMethod.DELETE}, urlPattern, router);
    }

    public MvcRouterRegister head(String[] urlPattern, MvcRouter router) {
        return this.router(new HttpMethod[]{HttpMethod.HEAD}, urlPattern, router);
    }

    public MvcRouterRegister options(String[] urlPattern, MvcRouter router) {
        return this.router(new HttpMethod[]{HttpMethod.OPTIONS}, urlPattern, router);
    }

    public MvcRouterRegister patch(String[] urlPattern, MvcRouter router) {
        return this.router(new HttpMethod[]{HttpMethod.PATCH}, urlPattern, router);
    }

    public MvcRouterRegister put(String[] urlPattern, MvcRouter router) {
        return this.router(new HttpMethod[]{HttpMethod.PUT}, urlPattern, router);
    }

    public MvcRouterRegister trace(String[] urlPattern, MvcRouter router) {
        return this.router(new HttpMethod[]{HttpMethod.TRACE}, urlPattern, router);
    }

    public static void registerRouter(RouterInfo routerInfo) {
        instance.routerInfoSet.add(routerInfo);
    }

    private static void registerRouter(HttpRouterInfo httpRouterInfo,
                                       ClassInfo<?> classInfo, MvcConfiguration webConfiguration,
                                       ApplicationContext applicationContext) {
        ClassLoader classLoader = applicationContext.getClassLoader();
        var methods = classInfo.getMethods();
        var clazz = classInfo.getClazz();
        for (var method : methods) {
            RouterAnnotationInfo router = RouterAnnotationInfoParser.getRouterAnnotation(method, classLoader);
            List<ExecutableArgument> methodParams = RouterMethodArgumentHandler.typeOf(method, clazz, classLoader);
            if (router != null) {
                MethodRouterExecutor routerExecutor = new MethodRouterExecutor();
                var routerInfo = new RouterInfo(routerExecutor);
                routerExecutor.setRouterClass(classInfo.getClazz());
                routerInfo.setAnnotationInfo(router);
                routerExecutor.setMethod(method);

                var stackMethod = classInfo.getClazz().getName() + "." + method.getName();
                LOGGER.debug(() -> "register router method: " + stackMethod);

                List<RouterPathFragments> routerPathFragments =
                        RouterPathSplicer.splicePathFragment(webConfiguration.getDispatcherMapping(), httpRouterInfo, router, stackMethod);
                routerInfo.setPaths(routerPathFragments);

                RouterResponse response = new RouterResponse();
                response.setRestResponseClass(method.getReturnType());

                setTemplate(response, router, webConfiguration);
                routerInfo.setResponse(response);

                // response type
                var defaultResponseTypes = webConfiguration.getDefaultResponseTypes();
                var responseType = router.responseType();
                if (router.hasTemplate()) {
                    response.setResponseType(responseType);
                    var handlerFactory = new ResponseContentHandlerFactory(applicationContext);
                    response.setHandler(handlerFactory.factory(router.handlerClass()));
                } else {
                    if (responseType != MediaType.ANY) {
                        response.setResponseType(responseType);
                        response.setHandler(ResponseContentHandlerProviderEnum.getByResponseType(responseType));
                    } else if (!defaultResponseTypes.isEmpty()) {
                        // todo 需要优化
                        response.setResponseType(defaultResponseTypes.iterator().next());
                        response.setHandler(ResponseContentHandlerProviderEnum.getByResponseType(response.getResponseType().toMediaType()));
                    } else {
                        if (!webConfiguration.isAllowClientResponseType()) {
                            throw new ResponseTypeException("\n" + method + "\n responseType cannot be MediaType.ANY. Or config default response type. Or allow client response type.");
                        } else {
                            response.setResponseType(MediaType.ANY);
                            var handlerFactory = new ResponseContentHandlerFactory(applicationContext);
                            response.setHandler(handlerFactory.factory(router.handlerClass()));
                        }
                    }
                }

                // content type
                var defaultContentTypes = webConfiguration.getDefaultContentTypes();
                var requestType = router.requestType();
                if (requestType != MediaType.ANY) {
                    routerInfo.setRequestType(requestType);
                } else {
                    if (!methodParams.isEmpty()) {
                        for (ExecutableArgument methodParam : methodParams) {
                            Map<Class<? extends Annotation>, Annotation> annotations = methodParam.getAnnotations();
                            if (annotations != null && !annotations.isEmpty()) {
                                annotations.forEach((key, value) -> {
                                    boolean isBody = false;
                                    MediaType bodyType = MediaType.ANY;
                                    if (key == RequestParameter.class) {
                                        RequestParameter requestParameter = (RequestParameter) value;
                                        if (requestParameter.paramType() == RequestParameterType.BODY) {
                                            isBody = true;
                                            bodyType = requestParameter.bodyType();
                                        }
                                    } else if (key == BodyParameter.class) {
                                        isBody = true;
                                        BodyParameter bodyParameter = (BodyParameter) value;
                                        bodyType = bodyParameter.type();
                                    }
                                    if (isBody) {
                                        if (bodyType != MediaType.ANY) {
                                            routerInfo.setRequestType(bodyType);
                                        } else if (!defaultContentTypes.isEmpty()) {
                                            routerInfo.setRequestType(defaultResponseTypes.iterator().next().toMediaType());
                                        } else {
                                            if (!webConfiguration.isAcceptClientContentType()) {
                                                throw new RuntimeException("requestType cannot be MediaType.ANY. Or config default request type. Or accept client content type.");
                                            } else {
                                                routerInfo.setRequestType(MediaType.ANY);
                                            }
                                        }
                                    } else {
                                        // TODO
                                    }
                                });

                            }
                        }
                    }
                    if (routerInfo.getRequestType() == null) {
                        routerInfo.setRequestType(MediaType.ANY);
                    }
                }

                routerInfo.setResponse(response);
                routerExecutor.setMethodParams(methodParams);
                LOGGER.debug(() -> "register router: " + routerInfo);
                instance.routerInfoSet.add(routerInfo);
            }
        }
    }

    private static void setTemplate(RouterResponse response, RouterAnnotationInfo router, MvcConfiguration webConfiguration) {
        response.setHasTemplate(router.hasTemplate());
        if (router.templatePrefix().isBlank()) {
            response.setTemplatePrefix(webConfiguration.getTemplatePrefix());
        } else {
            response.setTemplatePrefix(router.templatePrefix());
        }
        if (router.templatePrefix().isBlank()) {
            response.setTemplateSuffix(webConfiguration.getTemplateSuffix());
        } else {
            response.setTemplateSuffix(router.templateSuffix());
        }
    }

    public static Set<RouterInfo> getRouterInfoSet() {
        return Collections.unmodifiableSet(instance.routerInfoSet);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(MvcRouterRegister.class);
}
