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

import com.truthbean.debbie.annotation.AliasFor;
import com.truthbean.debbie.bean.BeanComponent;
import com.truthbean.debbie.bean.BeanType;
import com.truthbean.debbie.watcher.Watcher;
import com.truthbean.debbie.watcher.WatcherType;

import java.lang.annotation.*;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-10-18 17:51
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@BeanComponent(type = BeanType.SINGLETON)
@Watcher(type = WatcherType.HTTP)
@Router
public @interface RestRouter {
    /**
     * bean name
     * @return bean name
     */
    @AliasFor(annotation = Watcher.class, attribute = "name")
    String name() default "";

    /**
     * same as urlPatterns
     * @return path regex
     */
    @AliasFor(annotation = Router.class, attribute = "urlPatterns")
    String[] value() default "";

    /**
     * @return router title
     */
    String title() default "";

    /**
     * @return router description
     */
    String desc() default "";

    /**
     * @return router version
     */
    String version() default "";
}
