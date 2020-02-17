package com.truthbean.debbie.bean;

import java.lang.annotation.*;

/**
 * @author TruthBean
 * @since 0.0.2
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BeanDestroy {
}
