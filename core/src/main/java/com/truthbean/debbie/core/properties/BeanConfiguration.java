package com.truthbean.debbie.core.properties;

import com.truthbean.debbie.core.bean.BeanComponent;

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
public @interface BeanConfiguration {

    String keyPrefix() default "";
}
