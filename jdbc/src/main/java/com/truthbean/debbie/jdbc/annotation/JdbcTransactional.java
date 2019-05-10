package com.truthbean.debbie.jdbc.annotation;

import com.truthbean.debbie.core.proxy.MethodProxy;
import com.truthbean.debbie.jdbc.transaction.TransactionalMethodProxyHandler;

import java.lang.annotation.*;

/**
 * jdbc connection transactional
 *
 * 1. If forceCommit is true, whether or not it throws exception, connection will commit
 * 2. As forceCommit is false, if rollbackFor is instanceOf the exception it will rollback
 *
 * @author TruthBean
 * @since 0.0.1
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@MethodProxy(proxyHandler = TransactionalMethodProxyHandler.class)
public @interface JdbcTransactional {

    boolean readonly() default false;

    boolean forceCommit() default false;

    Class<? extends Throwable> rollbackFor() default Exception.class;
}
