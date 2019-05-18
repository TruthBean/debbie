package com.truthbean.debbie.core.properties;

import com.truthbean.debbie.core.data.transformer.DataTransformer;
import com.truthbean.debbie.core.data.transformer.text.DefaultTextTransformer;

import java.lang.annotation.*;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/05/18 10:37.
 */
@Target({ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PropertyInject {

    /**
     * property name
     * @return property name
     */
    String value() default "";

    Class<? extends DataTransformer<?, String>> transformer() default DefaultTextTransformer.class;
}
