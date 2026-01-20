package com.truthbean.debbie.jdbc.repository;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.jdbc.transaction.TransactionService;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.0
 */
public class SqlRepository implements TransactionService {
    private final RepositoryHandler handler;

    public SqlRepository() {
        this.handler = new RepositoryHandler();
    }

    public int[] batch(final String sql, final Object[][] args) {
        return handler.batch(LOGGER, getTransaction(), sql, args);
    }

    public boolean execute(final String sql, final Consumer<ResultSet> resultSetConsumer) {
        return handler.execute(LOGGER, getTransaction(), sql, resultSetConsumer);
    }

    public void call(final String sql, final Consumer<CallableStatement> callableStatementConsumer) {
        handler.call(LOGGER, getTransaction(), sql, callableStatementConsumer);
    }

    public <K> K insert(final String sql, final boolean generatedKeys, final Class<K> keyClass, final Object... args) {
        return handler.insert(LOGGER, getTransaction(), sql, generatedKeys, keyClass, args);
    }

    public int update(final String sql, final Object... args) {
        return handler.update(LOGGER, getTransaction(), sql, args);
    }

    public List<Map<String, Object>> queryMap(final String sql, final Object... args) {
        return handler.queryMap(LOGGER, getTransaction(), sql, args);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(SqlRepository.class);
}
