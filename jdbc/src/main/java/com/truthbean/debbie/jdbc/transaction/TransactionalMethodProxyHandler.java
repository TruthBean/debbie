package com.truthbean.debbie.jdbc.transaction;

import com.truthbean.debbie.core.proxy.MethodProxyHandler;
import com.truthbean.debbie.jdbc.annotation.JdbcTransactional;
import com.truthbean.debbie.jdbc.datasource.DataSourceFactory;
import com.truthbean.debbie.jdbc.datasource.DataSourceProperties;
import com.truthbean.debbie.jdbc.datasource.connection.ConnectionBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class TransactionalMethodProxyHandler implements MethodProxyHandler<JdbcTransactional> {

    private ConnectionBinder connectionBinder = new ConnectionBinder();

    private JdbcTransactional jdbcTransactional;

    @Override
    public void setMethodAnnotation(JdbcTransactional methodAnnotation) {
        this.jdbcTransactional = methodAnnotation;
    }

    @Override
    public void before() {
        LOGGER.debug("runing before method invoke ..");
        var config = DataSourceProperties.toConfiguration();
        DataSourceFactory factory = connectionBinder.getDataSourceFactory(config);
        factory.factory(config);
        connectionBinder.bind(factory.getConnection());
        if (jdbcTransactional.readonly()) {
            connectionBinder.setAutoCommit(false);
        }
    }

    @Override
    public void after() {
        LOGGER.debug("runing after method invoke ..");
        connectionBinder.commit();
    }

    @Override
    public void whenExceptionCached(Throwable e) {
        LOGGER.debug("runing when method invoke throw exception and cached ..");
        if (jdbcTransactional.forceCommit()) {
            LOGGER.debug("force commit ..");
            connectionBinder.commit();
        } else {
            if (jdbcTransactional.rollbackFor().isInstance(e)) {
                connectionBinder.rollback();
                LOGGER.debug("rollback ..");
            } else {
                LOGGER.debug("not rollback for this exception(" + e.getClass().getName() + "), it committed");
                connectionBinder.commit();
            }
        }
    }

    @Override
    public void finallyRun() {
        LOGGER.debug("runing when method invoke throw exception and run to finally ..");
        connectionBinder.close();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionalMethodProxyHandler.class);
}
