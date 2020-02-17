package com.truthbean.debbie.rmi;

import com.truthbean.debbie.bean.*;

import java.lang.annotation.*;

/**
 * @author truthbean
 * @since 0.0.2
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@BeanComponent
public @interface DebbieRmiService {

    /**
     * @return service name
     */
    @BeanAliceForValue
    @BeanAliceFor(name = "value")
    String value();

    @BeanAliceForType
    @BeanAliceFor(name = "type")
    BeanType type() default BeanType.NO_LIMIT;
}
