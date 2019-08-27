package com.truthbean.debbie.event;

import com.truthbean.debbie.bean.BeanComponent;
import com.truthbean.debbie.bean.BeanType;

import java.lang.annotation.*;

/**
 * @author TruthBean
 * @since 0.0.2
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@BeanComponent(type = BeanType.SINGLETON)
public @interface EventBeanListener {
    /**
     * event bean name
     * @return router bean name
     */
    String name() default "";

    /**
     * router always be singleton
     * @return BeanType
     */
    BeanType type() default BeanType.SINGLETON;
}
