/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.jdbc.repository;

import com.truthbean.debbie.jdbc.transaction.TransactionInfo;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Repository exposing raw SQL and {@code SqlBuilder}-based query methods
 * (batch, update, insert, delete, select, call) on top of
 * {@link JdbcTransactionRepository}.
 *
 * @param <Entity> the entity type
 * @param <Id>     the primary key type
 * @author TruthBean
 * @since 0.0.1
 */
public class CustomRepository<Entity, Id> extends JdbcRepository<Entity, Id> {

    /** the underlying transaction repository handler */
    private JdbcTransactionRepository<Entity, Id> repositoryHandler;

    /** Creates an empty repository; {@link #setJdbcTransactionRepository} must be called before use. */
    public CustomRepository() {
    }

    /**
     * Creates a repository backed by the given transaction repository.
     *
     * @param repositoryHandler the transaction repository handler
     */
    public CustomRepository(JdbcTransactionRepository<Entity, Id> repositoryHandler) {
        super(repositoryHandler);
        this.repositoryHandler = repositoryHandler;
    }

    @Override
    void setJdbcTransactionRepository(JdbcTransactionRepository<Entity, Id> repositoryHandler) {
        super.setJdbcTransactionRepository(repositoryHandler);
        this.repositoryHandler = repositoryHandler;
    }

    /** Executes a batch update with the given SQL and argument matrix. */
    public int[] batch(String sql, Object[][] args) {
        TransactionInfo transaction = getTransaction();
        return repositoryHandler.batch(getLog(), transaction, sql, args);
    }

    /** Executes an update with raw SQL and positional arguments. */
    public int update(String sql, Object... args) {
        TransactionInfo transaction = getTransaction();
        return repositoryHandler.update(getLog(), transaction, sql, args);
    }

    /** Executes an update built by the given {@code SqlBuilder} function. */
    public int update(Function<DynamicRepository.SqlBuilder, DynamicRepository.SqlBuilder> sqlBuilderFunc) {
        TransactionInfo transaction = getTransaction();
        var sqlBuilder = sqlBuilderFunc.apply(DynamicRepository.sqlBuilder(repositoryHandler.getEntityInfo().getDriverName()));
        return repositoryHandler.update(getLog(), transaction, sqlBuilder.toString(), sqlBuilder.getSqlParamsArray());
    }

    /** Executes an insert with raw SQL and positional arguments. */
    public int insert(String sql, Object... args) {
        TransactionInfo transaction = getTransaction();
        return repositoryHandler.update(getLog(), transaction, sql, args);
    }

    /** Executes an insert built by the given {@code SqlBuilder} function. */
    public int insert(Function<DynamicRepository.SqlBuilder, DynamicRepository.SqlBuilder> sqlBuilderFunc) {
        TransactionInfo transaction = getTransaction();
        var sqlBuilder = sqlBuilderFunc.apply(DynamicRepository.sqlBuilder(repositoryHandler.getEntityInfo().getDriverName()));
        return repositoryHandler.update(getLog(), transaction, sqlBuilder.toString(), sqlBuilder.getSqlParamsArray());
    }

    /** Executes a delete with raw SQL and positional arguments. */
    public int delete(String sql, Object... args) {
        TransactionInfo transaction = getTransaction();
        return repositoryHandler.update(getLog(), transaction, sql, args);
    }

    /** Executes a delete built by the given {@code SqlBuilder} function. */
    public int delete(Function<DynamicRepository.SqlBuilder, DynamicRepository.SqlBuilder> sqlBuilderFunc) {
        TransactionInfo transaction = getTransaction();
        var sqlBuilder = sqlBuilderFunc.apply(DynamicRepository.sqlBuilder(repositoryHandler.getEntityInfo().getDriverName()));
        return repositoryHandler.update(getLog(), transaction, sqlBuilder.toString(), sqlBuilder.getSqlParamsArray());
    }

    /** Executes a SQL statement and processes the result set via the given consumer. */
    public boolean execute(String sql, Consumer<ResultSet> resultSetConsumer) {
        TransactionInfo transaction = getTransaction();
        return repositoryHandler.execute(getLog(), transaction, sql, resultSetConsumer);
    }

    /** Calls a stored procedure and processes the callable statement via the given consumer. */
    public void call(String sql, Consumer<CallableStatement> callableStatementConsumer) {
        TransactionInfo transaction = getTransaction();
        repositoryHandler.call(getLog(), transaction, sql, callableStatementConsumer);
    }

    /** Queries a list of entities with raw SQL and positional arguments. */
    public List<Entity> selectEntityList(String sql, Object... args) {
        TransactionInfo transaction = getTransaction();
        var entityClass = getEntityClass();
        return repositoryHandler.query(transaction, sql, entityClass, args);
    }

    /** Queries a list of entities built by the given {@code SqlBuilder} function. */
    public List<Entity> selectEntityList(Function<DynamicRepository.SqlBuilder, DynamicRepository.SqlBuilder> sqlBuilderFunc) {
        TransactionInfo transaction = getTransaction();
        var entityClass = getEntityClass();
        DynamicRepository.SqlBuilder sqlBuilder = sqlBuilderFunc.apply(selectFromSql());
        return repositoryHandler.query(transaction, sqlBuilder.toString(), entityClass, sqlBuilder.getSqlParamsArray());
    }

    /** Queries a list of rows mapped to the given class with raw SQL. */
    public <T> List<T> select(String sql, Class<T> clazz, Object... args) {
        TransactionInfo transaction = getTransaction();
        return repositoryHandler.query(transaction, sql, clazz, args);
    }

    /** Queries a set of rows mapped to the given class with raw SQL. */
    public <T> Set<T> selectSet(String sql, Class<T> clazz, Object... args) {
        List<T> list = select(sql, clazz, args);
        return new HashSet<>(list);
    }

    /** Queries a list of rows as maps (column name → value) with raw SQL. */
    public List<Map<String, Object>> selectListMap(String sql, Object... args) {
        TransactionInfo transaction = getTransaction();
        return repositoryHandler.queryMap(getLog(), transaction, sql, args);
    }

    /** Queries a single row as a map; throws if more than one row is returned. */
    public Map<String, Object> selectMap(String sql, Object... args) {
        TransactionInfo transaction = getTransaction();
        List<Map<String, Object>> maps = repositoryHandler.queryMap(getLog(), transaction, sql, args);
        if (maps.size() == 1) {
            return maps.get(0);
        } else {
            throw new MoreRowException("Expect one row, but it has" + maps.size() + "rows.");
        }
    }

    /** Queries a single row mapped to the given class with raw SQL. */
    public <T> T selectOne(String sql, Class<T> clazz, Object... args) {
        TransactionInfo transaction = getTransaction();
        return repositoryHandler.queryOne(transaction, sql, clazz, args);
    }

    /** Queries a single entity with raw SQL and positional arguments. */
    public Entity selectEntity(String sql, Object... args) {
        TransactionInfo transaction = getTransaction();
        var entityClass = getEntityClass();
        return repositoryHandler.queryOne(transaction, sql, entityClass, args);
    }

    /** Queries a single entity built by the given {@code SqlBuilder} function. */
    public Entity selectEntity(Function<DynamicRepository.SqlBuilder, DynamicRepository.SqlBuilder> sqlBuilderFunc) {
        TransactionInfo transaction = getTransaction();
        var entityClass = getEntityClass();
        DynamicRepository.SqlBuilder sqlBuilder = sqlBuilderFunc.apply(selectFromSql());
        return repositoryHandler.queryOne(transaction, sqlBuilder.toString(), entityClass, sqlBuilder.getSqlParamsArray());
    }

    protected DynamicRepository.SqlBuilder selectFromSql() {
        return repositoryHandler.select(repositoryHandler.getEntityInfo()).sqlBuilder();
    }
}
