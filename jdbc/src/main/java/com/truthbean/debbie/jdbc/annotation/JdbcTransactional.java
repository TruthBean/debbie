/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.jdbc.annotation;

import com.truthbean.debbie.jdbc.transaction.TransactionalMethodProxyHandler;
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
@MethodProxy(order = 10, proxyHandler = TransactionalMethodProxyHandler.class)
public @interface JdbcTransactional {

    String profile() default "default";

    String databaseId() default "default";

    boolean readonly() default true;

    boolean forceCommit() default false;

    Class<? extends Throwable> rollbackFor() default Exception.class;
}
