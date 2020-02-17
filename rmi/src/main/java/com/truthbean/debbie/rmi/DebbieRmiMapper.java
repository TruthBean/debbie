package com.truthbean.debbie.rmi;

import java.lang.annotation.*;

/**
 * @author truthbean
 * @since 0.0.2
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DebbieRmiMapper {

    /**
     * @return rmi service name
     */
    String value() default "";
}
