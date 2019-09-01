package com.truthbean.debbie.httpclient.annotation;

import com.truthbean.debbie.bean.BeanComponent;
import com.truthbean.debbie.bean.BeanType;

import java.lang.annotation.*;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-04-03 17:12.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@BeanComponent
public @interface HttpClientRouter {
    /**
     * bean name
     * @return bean name
     */
    String value() default "";

    /**
     * bean type
     * @return BeanType
     */
    BeanType type() default BeanType.SINGLETON;

    String[] baseUrl() default {""};
}
