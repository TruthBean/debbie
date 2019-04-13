package com.truthbean.code.debbie.mvc.router;

import com.truthbean.code.debbie.core.bean.BeanInitializationHandler;
import com.truthbean.code.debbie.core.io.MediaType;
import com.truthbean.code.debbie.core.reflection.ClassInfo;
import com.truthbean.code.debbie.core.reflection.InvokedParameter;
import com.truthbean.code.debbie.mvc.MvcConfiguration;
import com.truthbean.code.debbie.mvc.response.RouterInvokeResult;
import com.truthbean.code.debbie.mvc.response.provider.ResponseHandlerProviderEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class MvcRouterRegister {
    private static final Set<RouterInfo> ROUTER_INFO_SET = new HashSet<>();

    public static void registerRouter(MvcConfiguration webConfiguration) {
        Set<ClassInfo> classInfoSet = BeanInitializationHandler.getAnnotatedMethodBean(Router.class);
        for (var classInfo : classInfoSet) {
            Router prefixRouter = (Router) classInfo.getClassAnnotations().get(Router.class);
            var methods = classInfo.getMethods();
            for (var method : methods) {
                Router router = method.getAnnotation(Router.class);
                List<InvokedParameter> methodParams = MvcRouterInvokedParameterHandler.typeOf(method.getParameters());
                if (router != null) {
                    var routerInfo = new RouterInfo();
                    routerInfo.setRouterClass(classInfo.getClazz());
                    routerInfo.setMethod(method);
                    routerInfo.setPaths(RouterPathSplicer.splicePathRegex(prefixRouter, router));
                    routerInfo.setRequestMethod(router.method());

                    routerInfo.setHasTemplate(router.hasTemplate());
                    if ("".equals(router.templatePrefix().trim())) {
                        routerInfo.setTemplatePrefix(webConfiguration.getTemplatePrefix());
                    } else {
                        routerInfo.setTemplatePrefix(router.templatePrefix());
                    }
                    if ("".equals(router.templateSuffix().trim())) {
                        routerInfo.setTemplateSuffix(webConfiguration.getTemplateSuffix());
                    } else {
                        routerInfo.setTemplateSuffix(router.templateSuffix());
                    }

                    routerInfo.setRequestType(router.requestType());

                    var response = new RouterInvokeResult();
                    var defaultType = webConfiguration.getDefaultTypes();
                    var responseType = router.responseType();
                    if (router.hasTemplate()) {
                        response.setHandler(ResponseHandlerProviderEnum.TEMPLATE_VIEW.getProvider());
                        response.setResponseType(responseType);
                    } else {
                        response.setHandler(router.handlerFilter().getProvider());
                        if (responseType != MediaType.ANY) {
                            response.setResponseType(responseType);
                        } else if (!defaultType.isEmpty()) {
                            response.setResponseType(defaultType.iterator().next());
                        } else {
                            throw new RuntimeException("responseType cannot be MediaType.ANY. Or config default response type!");
                        }
                    }

                    routerInfo.setResponse(response);
                    routerInfo.setMethodParams(methodParams);
                    LOGGER.debug("register router:" + routerInfo);
                    ROUTER_INFO_SET.add(routerInfo);
                }
            }
        }
    }

    public static Set<RouterInfo> getRouterInfoSet() {
        return Collections.unmodifiableSet(ROUTER_INFO_SET);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(MvcRouterRegister.class);
}
