package com.truthbean.debbie.properties;

import com.truthbean.debbie.bean.BeanComponent;

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

    String keyPrefix() default "";
}
