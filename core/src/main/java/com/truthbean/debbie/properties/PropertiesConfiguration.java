package com.truthbean.debbie.properties;

import com.truthbean.debbie.bean.*;

import java.lang.annotation.*;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/05/18 09:30.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@BeanComponent
public @interface PropertiesConfiguration {

    /**
     * event bean name
     * @return router bean name
     */
    @BeanAliceForValue
    @BeanAliceFor(name = "value")
    String value() default "";

    /**
     * router always be singleton
     * @return BeanType
     */
    @BeanAliceForType
    @BeanAliceFor(name = "type")
    BeanType type() default BeanType.SINGLETON;

    String keyPrefix() default "";
}
