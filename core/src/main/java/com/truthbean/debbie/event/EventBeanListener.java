package com.truthbean.debbie.event;

import com.truthbean.debbie.bean.*;

import java.lang.annotation.*;

/**
 * @author TruthBean
 * @since 0.0.2
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@BeanComponent
public @interface EventBeanListener {
    /**
     * event bean name
     * @return router bean name
     */
    @BeanAliceForName
    @BeanAliceFor(name = "name")
    String name() default "";

    /**
     * router always be singleton
     * @return BeanType
     */
    @BeanAliceForType
    @BeanAliceFor(name = "type")
    BeanType type() default BeanType.SINGLETON;
}
