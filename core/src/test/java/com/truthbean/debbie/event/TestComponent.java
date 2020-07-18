package com.truthbean.debbie.event;

import com.truthbean.debbie.bean.BeanComponent;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author TruthBean
 * @since 0.1.0
 * Created on 2020/7/13 18:46.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@BeanComponent
public @interface TestComponent {
}
