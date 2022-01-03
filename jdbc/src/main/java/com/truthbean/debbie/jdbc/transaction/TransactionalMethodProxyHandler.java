/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.jdbc.transaction;

import com.truthbean.debbie.bean.GlobalBeanFactory;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.jdbc.annotation.JdbcTransactional;
import com.truthbean.debbie.jdbc.datasource.DataSourceConfiguration;
import com.truthbean.debbie.jdbc.datasource.DataSourceFactory;
import com.truthbean.debbie.proxy.MethodProxyHandler;
import com.truthbean.Logger;
import com.truthbean.LoggerFactory;

import java.lang.reflect.Method;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class TransactionalMethodProxyHandler implements MethodProxyHandler<JdbcTransactional> {

    private final TransactionInfo transactionInfo;

    private JdbcTransactional jdbcTransactional;
    private JdbcTransactional classJdbcTransactional;

    private int order;

    private ApplicationContext applicationContext;
    private boolean autoCommit;

    public TransactionalMethodProxyHandler() {
        this.transactionInfo = new TransactionInfo();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public boolean sync() {
        return false;
    }

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public void setMethodAnnotation(JdbcTransactional methodAnnotation) {
        this.jdbcTransactional = methodAnnotation;
    }

    @Override
    public void setClassAnnotation(JdbcTransactional classAnnotation) {
        this.classJdbcTransactional = classAnnotation;
    }

    @Override
    public void setMethod(Method method) {
        transactionInfo.bindMethod(method);
    }

    @Override
    public void before() {
        if (applicationContext.isExiting()) {
            return;
        }
        LOGGER.debug(() -> "running before method (" + transactionInfo.getMethod() + ") invoke ..");
        GlobalBeanFactory globalBeanFactory = applicationContext.getGlobalBeanFactory();
        DataSourceConfiguration configuration = applicationContext.factory(DataSourceConfiguration.class);

        if (jdbcTransactional == null && classJdbcTransactional == null) {
            throw new MethodNoJdbcTransactionalException();
        } else if (jdbcTransactional == null && !classJdbcTransactional.readonly()) {
            String databaseId = classJdbcTransactional.databaseId();
            DataSourceFactory factory = globalBeanFactory.factory(databaseId + "DataSourceFactory", DataSourceFactory.class, true);
            transactionInfo.setDriverName(configuration.getDriverName());
            transactionInfo.setConnection(factory.getConnection());
            transactionInfo.setAutoCommit(false);
            autoCommit = false;
            transactionInfo.setForceCommit(classJdbcTransactional.forceCommit());
            transactionInfo.setRollbackFor(classJdbcTransactional.rollbackFor());
        } else if (jdbcTransactional != null && !jdbcTransactional.readonly()) {
            String databaseId = jdbcTransactional.databaseId();
            DataSourceFactory factory = globalBeanFactory.factory(databaseId + "DataSourceFactory", DataSourceFactory.class, true);
            transactionInfo.setConnection(factory.getConnection());
            transactionInfo.setAutoCommit(false);
            autoCommit = false;
            transactionInfo.setForceCommit(jdbcTransactional.forceCommit());
            transactionInfo.setRollbackFor(jdbcTransactional.rollbackFor());
        } else {
            DataSourceFactory factory = globalBeanFactory.factory( "defaultDataSourceFactory", DataSourceFactory.class, true);
            transactionInfo.setConnection(factory.getConnection());
            transactionInfo.setAutoCommit(true);
            transactionInfo.setForceCommit(false);
            transactionInfo.setRollbackFor(Exception.class);
            autoCommit = true;
        }
        TransactionManager.offer(transactionInfo);
    }

    @Override
    public void after() {
        if (applicationContext.isExiting()) {
            return;
        }
        LOGGER.debug(() -> "running after method (" + transactionInfo.getMethod() + ") invoke ..");
        if (!autoCommit) {
            transactionInfo.commit();
        }
    }

    @Override
    public void catchException(Throwable e) throws Throwable {
        LOGGER.debug(() -> "running when method (" + transactionInfo.getMethod() + ") invoke throw exception and " +
                "catched ..");
        if (!autoCommit) {
            if (applicationContext.isExiting()) {
                return;
            }
            if (transactionInfo.isForceCommit()) {
                LOGGER.debug(() -> "force commit ..");
                transactionInfo.commit();
            } else {
                if (transactionInfo.getRollbackFor().isInstance(e)) {
                    transactionInfo.rollback();
                    LOGGER.debug(() -> "rollback ..");
                } else {
                    LOGGER.debug(() -> "not rollback for this exception(" + e.getClass().getName() + "), it committed");
                    transactionInfo.commit();
                }
            }
        }
        throw e;
    }

    @Override
    public void finallyRun() {
        LOGGER.debug(() -> "running when method (" + transactionInfo.getMethod() + ") invoked and run to finally ..");
        transactionInfo.close();
        TransactionManager.remove(transactionInfo);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionalMethodProxyHandler.class);
}
