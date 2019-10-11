package com.truthbean.debbie.rmi;

import com.truthbean.debbie.bean.BeanComponent;
import com.truthbean.debbie.bean.BeanType;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@BeanComponent
public @interface DebbieRmiService {

    /**
     * @return service name
     */
    String value();

    BeanType type() default BeanType.NO_LIMIT;
}
