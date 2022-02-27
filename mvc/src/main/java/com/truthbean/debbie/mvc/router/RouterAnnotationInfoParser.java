/**
 * Copyright (c) 2022 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mvc.router;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.Set;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.0
 * Created on 2021-04-01 15:35
 */
public class RouterAnnotationInfoParser {
    public static RouterAnnotationInfo getRouterAnnotation(Method method, ClassLoader classLoader) {
        Router router = method.getAnnotation(Router.class);
        if (router != null) {
            return new RouterAnnotationInfo(router);
        }

        GetRouter getRouter = method.getAnnotation(GetRouter.class);
        if (getRouter != null) {
            return new RouterAnnotationInfo(getRouter);
        }

        PostRouter postRouter = method.getAnnotation(PostRouter.class);
        if (postRouter != null) {
            return new RouterAnnotationInfo(postRouter);
        }

        PutRouter putRouter = method.getAnnotation(PutRouter.class);
        if (putRouter != null) {
            return new RouterAnnotationInfo(putRouter);
        }

        DeleteRouter deleteRouter = method.getAnnotation(DeleteRouter.class);
        if (deleteRouter != null) {
            return new RouterAnnotationInfo(deleteRouter);
        }

        OptionsRouter optionsRouter = method.getAnnotation(OptionsRouter.class);
        if (optionsRouter != null) {
            return new RouterAnnotationInfo(optionsRouter);
        }

        HeadRouter headRouter = method.getAnnotation(HeadRouter.class);
        if (headRouter != null) {
            return new RouterAnnotationInfo(headRouter);
        }

        PatchRouter patchRouter = method.getAnnotation(PatchRouter.class);
        if (patchRouter != null) {
            return new RouterAnnotationInfo(patchRouter);
        }

        TraceRouter traceRouter = method.getAnnotation(TraceRouter.class);
        if (traceRouter != null) {
            return new RouterAnnotationInfo(traceRouter);
        }

        ConnectRouter connectRouter = method.getAnnotation(ConnectRouter.class);
        if (connectRouter != null) {
            return new RouterAnnotationInfo(connectRouter);
        }

        Set<RouterAnnotationParser> parsers = loadRouterAnnotationParserProviderSet(classLoader);
        for (RouterAnnotationParser parser : parsers) {
            RouterAnnotationInfo info = parser.parse(method);
            if (info != null) {
                return info;
            }
        }

        return null;
    }

    private static Set<RouterAnnotationParser> loadRouterAnnotationParserProviderSet(ClassLoader classLoader) {
        Set<RouterAnnotationParser> result = new HashSet<>();
        ServiceLoader<RouterAnnotationParser> serviceLoader;
        try {
            serviceLoader = ServiceLoader.load(RouterAnnotationParser.class, classLoader);
        } catch (Throwable e) {
            LOGGER.error("", e);
            return result;
        }
        for (RouterAnnotationParser s : serviceLoader) {
            result.add(s);
        }
        return result;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(RouterAnnotationInfoParser.class);
}
