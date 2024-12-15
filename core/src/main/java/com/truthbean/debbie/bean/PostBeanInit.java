package com.truthbean.debbie.bean;

import java.lang.annotation.*;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.6
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PostBeanInit {
}
