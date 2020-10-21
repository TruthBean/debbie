/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mvc.router;

import com.truthbean.debbie.spi.SpiLoader;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-09-21 13:58
 */
public interface RouterAnnotationParser {

    default RouterAnnotationInfo parse(Method method) {
        return null;
    }

    default boolean isSupported(Annotation annotation) {
        return false;
    }

    default RouterAnnotationInfo parse(Annotation annotation) {
        return null;
    }

    static RouterAnnotationInfo getRouterAnnotation(Method method) {
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

        Set<RouterAnnotationParser> parsers = SpiLoader.loadProviderSet(RouterAnnotationParser.class);
        for (RouterAnnotationParser parser : parsers) {
            RouterAnnotationInfo info = parser.parse(method);
            if (info != null) {
                return info;
            }
        }

        return null;
    }
}
