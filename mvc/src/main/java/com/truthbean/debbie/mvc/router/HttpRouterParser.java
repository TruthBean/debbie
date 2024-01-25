/**
 * Copyright (c) 2024 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mvc.router;

import com.truthbean.debbie.annotation.AnnotationInfo;
import com.truthbean.debbie.watcher.Watcher;
import com.truthbean.debbie.watcher.WatcherType;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.2.0
 * Created on 2020-11-21 15:11
 */
public class HttpRouterParser {
    public static HttpRouterInfo parse(Map<Class<? extends Annotation>, AnnotationInfo> classAnnotations) {
        boolean containHttpWatcher = false;
        HttpRouterInfo routerInfo = null;
        for (Map.Entry<Class<? extends Annotation>, AnnotationInfo> entry : classAnnotations.entrySet()) {
            Class<? extends Annotation> type = entry.getKey();
            Annotation annotation = entry.getValue().getOrigin();
            if (type == HttpRouter.class) {
                return parse((HttpRouter) annotation);
            } else if (type == RestRouter.class) {
                return parse((RestRouter) annotation);
            } else if (type == Watcher.class && ((Watcher) annotation).type() == WatcherType.HTTP) {
                containHttpWatcher = true;
            } else if (type == Router.class) {
                routerInfo = parse((Router) annotation);
            }
        }
        if (containHttpWatcher && routerInfo != null) {
            return routerInfo;
        }
        return routerInfo;
    }

    private static HttpRouterInfo parse(Router router) {
        HttpRouterInfo info = new HttpRouterInfo();
        info.setBeanName(router.name());
        info.setDesc(router.desc());
        // info.setTitle(router.title());
        info.setUrlPatterns(router.value());
        info.setRest(false);
        return info;
    }

    private static HttpRouterInfo parse(HttpRouter httpRouter) {
        HttpRouterInfo info = new HttpRouterInfo();
        info.setBeanName(httpRouter.name());
        info.setDesc(httpRouter.desc());
        info.setTitle(httpRouter.title());
        info.setUrlPatterns(httpRouter.value());
        info.setRest(false);
        return info;
    }

    private static HttpRouterInfo parse(RestRouter restRouter) {
        HttpRouterInfo info = new HttpRouterInfo();
        info.setBeanName(restRouter.name());
        info.setDesc(restRouter.desc());
        info.setTitle(restRouter.title());
        info.setUrlPatterns(restRouter.value());
        info.setRest(true);
        return info;
    }
}
