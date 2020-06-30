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

import com.truthbean.debbie.bean.BeanFactoryContext;
import com.truthbean.debbie.bean.BeanInitialization;
import com.truthbean.debbie.bean.DebbieBeanInfo;
import com.truthbean.debbie.io.MediaType;
import com.truthbean.debbie.mvc.MvcConfiguration;
import com.truthbean.debbie.mvc.request.BodyParameter;
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
import com.truthbean.logger.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.*;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class MvcRouterRegister {
    private static final Set<RouterInfo> ROUTER_INFO_SET = new HashSet<>();

    public static void registerRouter(MvcConfiguration webConfiguration, BeanFactoryContext applicationContext) {
        BeanInitialization beanInitialization = applicationContext.getBeanInitialization();
        Set<DebbieBeanInfo<?>> classInfoSet = beanInitialization.getAnnotatedClass(Router.class);
        for (DebbieBeanInfo<?> classInfo : classInfoSet) {
            Map<Class<? extends Annotation>, Annotation> classAnnotations = classInfo.getClassAnnotations();
            Watcher watcher = (Watcher) classAnnotations.get(Watcher.class);
            if (watcher == null || watcher.type() == WatcherType.HTTP) {
                registerRouter(classAnnotations, classInfo, webConfiguration, applicationContext);
            }
        }
    }

    public static void registerRouter(RouterInfo routerInfo) {
        ROUTER_INFO_SET.add(routerInfo);
    }

    private static void registerRouter(Map<Class<? extends Annotation>, Annotation> classAnnotations,
                                       ClassInfo<?> classInfo, MvcConfiguration webConfiguration,
                                       BeanFactoryContext applicationContext) {
        Router prefixRouter = (Router) classAnnotations.get(Router.class);
        var methods = classInfo.getMethods();
        var clazz = classInfo.getClazz();
        for (var method : methods) {
            RouterAnnotationInfo router = RouterAnnotationInfo.getRouterAnnotation(method);
            List<ExecutableArgument> methodParams = RouterMethodArgumentHandler.typeOf(method, clazz);
            if (router != null) {
                var routerInfo = new RouterInfo();
                routerInfo.setRouterClass(classInfo.getClazz());
                routerInfo.setMethod(method);

                var stackMethod = classInfo.getClazz().getName() + "." + method.getName();
                LOGGER.debug(() -> "register router method: " + stackMethod);

                List<RouterPathFragments> routerPathFragments =
                        RouterPathSplicer.splicePathFragment(webConfiguration.getDispatcherMapping(), prefixRouter, router);
                routerInfo.setPaths(routerPathFragments);

                routerInfo.setRequestMethod(Arrays.asList(router.method()));

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
                    if (methodParams != null && !methodParams.isEmpty()) {
                        for (ExecutableArgument methodParam : methodParams) {
                            Map<Class<? extends Annotation>, Annotation> annotations = methodParam.getAnnotations();
                            if (annotations != null && !annotations.isEmpty()) {
                                annotations.forEach((key, value) -> {
                                    boolean isBody = false;
                                    if (key == RequestParameter.class) {
                                        RequestParameter requestParameter = (RequestParameter) value;
                                        if (requestParameter.paramType() == RequestParameterType.BODY) {
                                            isBody = true;
                                        }
                                    } else if (key == BodyParameter.class) {
                                        isBody = true;
                                    }
                                    if (isBody) {
                                        if (!defaultContentTypes.isEmpty()) {
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
                routerInfo.setMethodParams(methodParams);
                LOGGER.debug(() -> "register router: " + routerInfo);
                ROUTER_INFO_SET.add(routerInfo);
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
        return Collections.unmodifiableSet(ROUTER_INFO_SET);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(MvcRouterRegister.class);
}
