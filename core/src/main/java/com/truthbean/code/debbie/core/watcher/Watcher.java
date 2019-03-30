package com.truthbean.code.debbie.core.watcher;

import java.lang.annotation.*;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/23 10:44.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Watcher {
    /**
     * watcher name
     * @return bean name
     */
    String name() default "";

    /**
     * watcher id
     * @return watcher id
     */
    String id() default "";

    WatcherType type() default WatcherType.HTTP;

}
