package com.truthbean.debbie.boot;

import com.truthbean.debbie.bean.*;

import java.lang.annotation.*;

/**
 * @author truthbean/Rogar·Q
 * @since 0.0.2
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@BeanComponent
public @interface DebbieBootApplication {
    /**
     * event bean name
     * @return router bean name
     */
    @BeanAliceForValue
    @BeanAliceFor(name = "value")
    String value() default "";

    /**
     * event bean name
     * @return router bean name
     */
    @BeanAliceForName
    @BeanAliceFor(name = "name")
    String name() default "";

    DebbieScan scan() default @DebbieScan();
}
