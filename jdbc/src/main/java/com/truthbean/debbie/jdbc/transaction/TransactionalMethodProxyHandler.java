package com.truthbean.debbie.jdbc.transaction;

import com.truthbean.debbie.bean.DebbieApplicationContext;
import com.truthbean.debbie.bean.BeanInitialization;
import com.truthbean.debbie.jdbc.annotation.JdbcTransactional;
import com.truthbean.debbie.jdbc.datasource.DataSourceConfiguration;
import com.truthbean.debbie.jdbc.datasource.DataSourceFactory;
import com.truthbean.debbie.jdbc.datasource.DriverConnection;
import com.truthbean.debbie.properties.DebbieConfigurationFactory;
import com.truthbean.debbie.proxy.MethodProxyHandler;
import com.truthbean.Logger;
import com.truthbean.logger.LoggerFactory;

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

    private DebbieApplicationContext applicationContext;
    private boolean autoCommit;

    public TransactionalMethodProxyHandler() {
        this.transactionInfo = new TransactionInfo();
    }

    @Override
    public void setApplicationContext(DebbieApplicationContext applicationContext) {
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
        transactionInfo.setMethod(method);
    }

    @Override
    public void before() {
        LOGGER.debug(() -> "running before method (" + transactionInfo.getMethod() + ") invoke ..");
        BeanInitialization beanInitialization = applicationContext.getBeanInitialization();
        DebbieConfigurationFactory configurationFactory = applicationContext.getConfigurationFactory();
        DataSourceConfiguration configuration = configurationFactory.factory(DataSourceConfiguration.class, applicationContext);

        DataSourceFactory factory = beanInitialization.getRegisterBean(DataSourceFactory.class);
        DriverConnection driverConnection = new DriverConnection();
        driverConnection.setDriverName(configuration.getDriverName());
        driverConnection.setConnection(factory.getConnection());
        transactionInfo.setConnection(driverConnection);

        if (jdbcTransactional == null && classJdbcTransactional == null) {
            throw new MethodNoJdbcTransactionalException();
        } else if (jdbcTransactional == null && !classJdbcTransactional.readonly()) {
            transactionInfo.setAutoCommit(false);
            autoCommit = false;
            transactionInfo.setForceCommit(classJdbcTransactional.forceCommit());
            transactionInfo.setRollbackFor(classJdbcTransactional.rollbackFor());
        } else if (jdbcTransactional != null && !jdbcTransactional.readonly()) {
            transactionInfo.setAutoCommit(false);
            autoCommit = false;
            transactionInfo.setForceCommit(jdbcTransactional.forceCommit());
            transactionInfo.setRollbackFor(jdbcTransactional.rollbackFor());
        } else {
            transactionInfo.setAutoCommit(true);
            autoCommit = true;
        }
        TransactionManager.offer(transactionInfo);
    }

    @Override
    public void after() {
        LOGGER.debug(() -> "running after method (" + transactionInfo.getMethod() + ") invoke ..");
        if (!autoCommit)
            transactionInfo.commit();
    }

    @Override
    public void catchException(Throwable e) throws Throwable {
        LOGGER.debug(() -> "running when method (" + transactionInfo.getMethod() + ") invoke throw exception and " +
                "catched ..");
        if (!autoCommit) {
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
        TransactionManager.remove();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionalMethodProxyHandler.class);
}
