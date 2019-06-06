package com.truthbean.debbie.jdbc.annotation;

import com.truthbean.debbie.proxy.MethodProxy;

import java.lang.annotation.*;

/**
 * jdbc connection transactional
 * <p>
 * 1. If forceCommit is true, whether or not it throws exception, connection will commit
 * 2. As forceCommit is false, if rollbackFor is instanceOf the exception it will rollback
 *
 * @author TruthBean
 * @since 0.0.1
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@MethodProxy(order = 10)
public @interface JdbcTransactional {

    boolean readonly() default true;

    boolean forceCommit() default false;

    Class<? extends Throwable> rollbackFor() default Exception.class;
}
