package com.truthbean.debbie.mvc.router;

import com.truthbean.debbie.bean.BeanInitialization;
import com.truthbean.debbie.bean.DebbieBeanInfo;
import com.truthbean.debbie.io.MediaType;
import com.truthbean.debbie.reflection.ClassInfo;
import com.truthbean.debbie.reflection.ExecutableArgument;
import com.truthbean.debbie.watcher.Watcher;
import com.truthbean.debbie.watcher.WatcherType;
import com.truthbean.debbie.mvc.MvcConfiguration;
import com.truthbean.debbie.mvc.request.RequestParameter;
import com.truthbean.debbie.mvc.request.RequestParameterType;
import com.truthbean.debbie.mvc.response.RouterResponse;
import com.truthbean.debbie.mvc.response.provider.ResponseHandlerProviderEnum;
import com.truthbean.debbie.mvc.url.RouterPathFragments;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.*;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class MvcRouterRegister {
    private static final Set<RouterInfo> ROUTER_INFO_SET = new HashSet<>();

    public static void registerRouter(MvcConfiguration webConfiguration, BeanInitialization beanInitialization) {
        Set<DebbieBeanInfo> classInfoSet = beanInitialization.getAnnotatedClass(Router.class);
        for (var classInfo : classInfoSet) {
            var classAnnotations = classInfo.getClassAnnotations();
            Watcher watcher = (Watcher) classAnnotations.get(Watcher.class);
            if (watcher == null || watcher.type() == WatcherType.HTTP) {
                registerRouter(classAnnotations, classInfo, webConfiguration);
            }
        }
    }

    private static void registerRouter(Map<Class<? extends Annotation>, Annotation> classAnnotations,
                                       ClassInfo<?> classInfo, MvcConfiguration webConfiguration) {
        Router prefixRouter = (Router) classAnnotations.get(Router.class);
        var methods = classInfo.getMethods();
        for (var method : methods) {
            Router router = method.getAnnotation(Router.class);
            List<ExecutableArgument> methodParams = RouterMethodArgumentHandler.typeOf(method.getParameters());
            if (router != null) {
                var routerInfo = new RouterInfo();
                routerInfo.setRouterClass(classInfo.getClazz());
                routerInfo.setMethod(method);

                var stackMethod = classInfo.getClazz().getName() + "." + method.getName();
                LOGGER.debug("register router method: " + stackMethod);

                List<RouterPathFragments> routerPathFragments =
                        RouterPathSplicer.splicePathFragment(webConfiguration.getDispatcherMapping(), prefixRouter, router);
                routerInfo.setPaths(routerPathFragments);

                routerInfo.setRequestMethod(Arrays.asList(router.method()));

                RouterResponse response = new RouterResponse();

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
                routerInfo.setResponse(response);

                // response type
                var defaultResponseTypes = webConfiguration.getDefaultResponseTypes();
                var responseType = router.responseType();
                if (router.hasTemplate()) {
                    response.setHandler(ResponseHandlerProviderEnum.TEMPLATE_VIEW.getProvider());
                    response.setResponseType(responseType);
                } else {
                    response.setHandler(router.handlerFilter().getProvider());
                    if (responseType != MediaType.ANY) {
                        response.setResponseType(responseType);
                        response.setHandler(ResponseHandlerProviderEnum.getByResponseType(responseType));
                    } else if (!defaultResponseTypes.isEmpty()) {
                        // todo 需要优化
                        response.setResponseType(defaultResponseTypes.iterator().next());
                        response.setHandler(ResponseHandlerProviderEnum.getByResponseType(response.getResponseType().toMediaType()));
                    } else {
                        if (!webConfiguration.isAllowClientResponseType()) {
                            throw new RuntimeException("responseType cannot be MediaType.ANY. Or config default response type. Or allow client response type.");
                        } else {
                            response.setResponseType(MediaType.ANY);
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
                                    if (key == RequestParameter.class) {
                                        RequestParameter requestParameter = (RequestParameter) value;
                                        if (requestParameter.paramType() == RequestParameterType.BODY) {
                                            if (!defaultContentTypes.isEmpty()) {
                                                routerInfo.setRequestType(defaultResponseTypes.iterator().next().toMediaType());
                                            } else {
                                                if (!webConfiguration.isAcceptClientContentType()) {
                                                    throw new RuntimeException("requestType cannot be MediaType.ANY. Or config default request type. Or accept client content type.");
                                                } else {
                                                    routerInfo.setRequestType(MediaType.ANY);
                                                }
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
                LOGGER.debug("register router: " + routerInfo);
                ROUTER_INFO_SET.add(routerInfo);
            }
        }
    }

    public static Set<RouterInfo> getRouterInfoSet() {
        return Collections.unmodifiableSet(ROUTER_INFO_SET);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(MvcRouterRegister.class);
}
