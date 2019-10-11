package com.truthbean.debbie.rmi;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DebbieRmiMapper {

    /**
     * @return rmi service name
     */
    String value() default "";
}
