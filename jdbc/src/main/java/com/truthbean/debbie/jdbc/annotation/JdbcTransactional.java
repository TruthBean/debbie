package com.truthbean.debbie.jdbc.annotation;

import com.truthbean.debbie.core.proxy.MethodProxy;
import com.truthbean.debbie.jdbc.transaction.TransactionalMethodProxyHandler;

import java.lang.annotation.*;

/**
 * jdbc connection transactional
 * @author TruthBean
 * @since 0.0.1
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@MethodProxy(proxyHandler = TransactionalMethodProxyHandler.class)
public @interface JdbcTransactional {

    boolean readonly() default false;
}
