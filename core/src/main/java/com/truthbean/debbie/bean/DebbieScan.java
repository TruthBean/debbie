package com.truthbean.debbie.bean;

import com.truthbean.debbie.properties.ClassesScanProperties;

import java.lang.annotation.*;

/**
 * @see ClassesScanProperties
 *
 * @author truthbean/Rogar·Q
 * @since 0.0.2
 * Created on 2020-01-19 10:49.
 */
@Target({ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DebbieScan {

    String[] basePackages() default {};

    Class<?>[] classes() default {};

    String[] excludePackages() default {};

    Class<?>[] excludeClasses() default {};
}
