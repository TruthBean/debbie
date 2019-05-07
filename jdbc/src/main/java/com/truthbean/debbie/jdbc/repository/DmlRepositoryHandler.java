package com.truthbean.debbie.jdbc.repository;

import com.truthbean.debbie.core.reflection.ReflectionHelper;
import com.truthbean.debbie.jdbc.column.ColumnInfo;
import com.truthbean.debbie.jdbc.domain.PageRequest;
import com.truthbean.debbie.jdbc.transaction.TransactionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/4/3 23:23.
 */
public class DmlRepositoryHandler<E, ID> extends RepositoryHandler {

    private Class<E> entityClass;
    private Class<ID> idClass;
    private EntityInfo<E> entityInfo;

    public DmlRepositoryHandler(Class<E> entityClass, Class<ID> idClass) {
        this.entityClass = entityClass;
        this.idClass = idClass;
    }

    private EntityInfo<E> getEntityInfo() {
        if (entityInfo == null) {
            entityInfo = EntityResolver.resolveEntityClass(getEntityClass());
        }
        return entityInfo;
    }

    @SuppressWarnings("unchecked")
    private Class<E> getEntityClass() {
        if (entityClass == null) {
            try {
                var types = ReflectionHelper.getActualTypes(getClass());
                if (types != null && types.length > 0) {
                    entityClass = (Class<E>) types[0];
                }
            } catch (Exception e) {
                LOGGER.error("getActualTypeArguments error. ", e);
            }
            if (entityClass == null) {
                throw new RuntimeException("get entity class error");
            }
        }
        return entityClass;
    }

    private static class ConditionAndValue {
        private String conditionSql;
        private List<Object> conditionValues;
    }

    public boolean deleteById(Connection connection, ID id) throws TransactionException {
        var entityInfo = getEntityInfo();
        var table = entityInfo.getTable();
        var primaryKey = entityInfo.getPrimaryKey();
        String sql = DynamicSqlBuilder.sql().delete().from(table).where().eq(primaryKey.getColumnName(), "?").builder();
        return super.update(connection, sql, id) > 0L;
    }

    private ConditionAndValue resolveCondition(EntityInfo<E> entityInfo) {
        List<ColumnInfo> columns = entityInfo.getColumnInfoList();

        var sqlBuilder = DynamicSqlBuilder.sql().eq("1", "1");
        List<Object> columnValues = new LinkedList<>();

        columns.stream()
                .filter(column -> column.getValue() != null)
                .forEach(column -> {
                    columnValues.add(column.getValue());
                    sqlBuilder.and(column.getColumnName() + " = ?");
                });

        var conditionAndValue = new ConditionAndValue();
        conditionAndValue.conditionSql = sqlBuilder.builder();
        conditionAndValue.conditionValues = columnValues;
        return conditionAndValue;
    }

    public int delete(Connection connection, E condition) throws TransactionException {
        var entityInfo = EntityResolver.resolveEntity(condition);
        var table = entityInfo.getTable();
        var conditionAndValue = resolveCondition(entityInfo);

        var sqlBuilder = DynamicSqlBuilder.sql().delete().from(table).where().extra(conditionAndValue.conditionSql);

        List<Object> columnValues = conditionAndValue.conditionValues;

        var sql = sqlBuilder.builder();
        LOGGER.debug(sql);
        return super.update(connection, sql, columnValues.toArray());
    }

    @SuppressWarnings("unchecked")
    public ID insert(Connection connection, E entity) throws TransactionException {
        var entityInfo = EntityResolver.resolveEntity(entity);
        var table = entityInfo.getTable();
        var columns = entityInfo.getColumnInfoList();

        List<String> columnNames = new LinkedList<>();
        List<Object> columnValues = new LinkedList<>();
        List<Object> signs = new LinkedList<>();
        columns.stream()
                .filter(column -> column.getValue() != null)
                .forEach(column -> {
                    columnValues.add(column.getValue());
                    columnNames.add(column.getColumnName());
                    signs.add("?");
                });

        var sql = DynamicSqlBuilder.sql().insert().extra(table).leftParenthesis()
                .columns(columnNames).rightParenthesis().values(signs).builder();
        LOGGER.debug(sql);
        var primaryKey = entityInfo.getPrimaryKey();
        var generatedKeys = primaryKey.getPrimaryKeyType() != null;
        return (ID) super.insert(connection, sql, generatedKeys, primaryKey.getJavaClass(), columnValues.toArray());
    }

    public boolean update(Connection connection, E entity) throws TransactionException {
        var entityInfo = EntityResolver.resolveEntity(entity);
        var table = entityInfo.getTable();
        var columns = entityInfo.getColumnInfoList();
        var primaryKey = entityInfo.getPrimaryKey();

        List<String> columnNames = new LinkedList<>();
        List<Object> columnValues = new LinkedList<>();
        for (ColumnInfo column : columns) {
            var value = column.getValue();
            if (!column.isPrimaryKey() && value != null) {
                columnValues.add(column.getValue());
                columnNames.add(column.getColumnName());
            }
        }

        columnValues.add(primaryKey.getValue());

        var sql = DynamicSqlBuilder.sql().update(table).set(columnNames)
                .where().eq(primaryKey.getColumnName(), "?").builder();
        LOGGER.debug(sql);
        return super.update(connection, sql, columnValues.toArray()) == 1;
    }

    public E findOne(Connection connection, E condition) {
        var entityInfo = getEntityInfo();
        var entityClass = entityInfo.getJavaType();

        var table = entityInfo.getTable();
        var columns = entityInfo.getColumnInfoList();

        List<String> columnNames = new LinkedList<>();
        for (ColumnInfo column : columns) {
            columnNames.add(column.getColumnName());
        }

        var conditionInfo = EntityResolver.resolveEntity(condition);
        var conditionAndValues = resolveCondition(conditionInfo);
        var sql = DynamicSqlBuilder.sql().select(columnNames).from(table)
                .where().extra(conditionAndValues.conditionSql).builder();
        LOGGER.debug(sql);
        return super.selectOne(connection, sql, entityClass, conditionAndValues.conditionValues.toArray());
    }

    private SqlAndArgs<E> prequery(E condition) {
        var entityInfo = getEntityInfo();
        var entityClass = entityInfo.getJavaType();

        var table = entityInfo.getTable();
        var columns = entityInfo.getColumnInfoList();

        List<String> columnNames = new LinkedList<>();
        for (ColumnInfo column : columns) {
            columnNames.add(column.getColumnName());
        }

        var sqlBuilder = DynamicSqlBuilder.sql().select(columnNames).from(table);
        Object[] args = null;

        if (condition != null) {
            var conditionInfo = EntityResolver.resolveEntity(condition);
            var conditionAndValues = resolveCondition(conditionInfo);
            sqlBuilder.where().extra(conditionAndValues.conditionSql);
            args = conditionAndValues.conditionValues.toArray();
        }

        var sqlAndArgs = new SqlAndArgs<E>();
        sqlAndArgs.sqlBuilder = sqlBuilder;
        sqlAndArgs.args = args;
        sqlAndArgs.entityClass = entityClass;

        return sqlAndArgs;
    }

    public List<E> findList(Connection connection, E condition) {
        SqlAndArgs<E> sqlAndArgs = prequery(condition);

        var sql = sqlAndArgs.sqlBuilder.builder();
        LOGGER.debug(sql);
        return super.select(connection, sql, sqlAndArgs.entityClass, sqlAndArgs.args);
    }

    public Page<E> findPaged(Connection connection, E condition, PageRequest pageable) {
        SqlAndArgs<E> sqlAndArgs = prequery(condition);

        var sql = sqlAndArgs.sqlBuilder.limit(pageable.getOffset(), pageable.getPageSize()).builder();
        LOGGER.debug(sql);

        var count = count(connection, condition);
        List<E> content = super.select(connection, sql, sqlAndArgs.entityClass, sqlAndArgs.args);
        return Page.createPage(pageable.getCurrentPage(), pageable.getPageSize(), count, content);
    }

    public Page<E> findPaged(Connection connection, PageRequest pageable) {
        SqlAndArgs<E> sqlAndArgs = prequery(null);

        var sql = sqlAndArgs.sqlBuilder.limit(pageable.getOffset(), pageable.getPageSize()).builder();
        LOGGER.debug(sql);

        var count = count(connection);
        List<E> content = super.select(connection, sql, sqlAndArgs.entityClass, sqlAndArgs.args);
        return Page.createPage(pageable.getCurrentPage(), pageable.getPageSize(), count, content);
    }

    public List<E> findAll(Connection connection) {
        SqlAndArgs<E> sqlAndArgs = prequery(null);

        var sql = sqlAndArgs.sqlBuilder.builder();
        LOGGER.debug(sql);
        return super.select(connection, sql, sqlAndArgs.entityClass, sqlAndArgs.args);
    }

    public Long count(Connection connection, E condition) {
        var entityInfo = getEntityInfo();

        var table = entityInfo.getTable();

        var sqlBuilder = DynamicSqlBuilder.sql().select().count().from(table);
        Object[] args = null;

        if (condition != null) {
            var conditionInfo = EntityResolver.resolveEntity(condition);
            var conditionAndValues = resolveCondition(conditionInfo);
            sqlBuilder.where().extra(conditionAndValues.conditionSql);
            args = conditionAndValues.conditionValues.toArray();
        }

        var sql = sqlBuilder.builder();
        LOGGER.debug(sql);
        return super.selectOne(connection, sql, Long.class, args);
    }

    public Long count(Connection connection) {
        var entityInfo = getEntityInfo();
        var table = entityInfo.getTable();
        var sql = DynamicSqlBuilder.sql().select().count().from(table).builder();
        LOGGER.debug(sql);
        return super.selectOne(connection, sql, Long.class);
    }

    public E findById(Connection connection, ID id) {
        var entityClass = getEntityClass();
        var entityInfo = EntityResolver.resolveEntityClass(entityClass);

        var table = entityInfo.getTable();
        var columns = entityInfo.getColumnInfoList();
        var primaryKey = entityInfo.getPrimaryKey();

        List<String> columnNames = new LinkedList<>();
        for (ColumnInfo column : columns) {
            columnNames.add(column.getColumnName());
        }

        var sql = DynamicSqlBuilder.sql().select(columnNames).from(table)
                .where().eq(primaryKey.getColumnName(), "?").builder();
        LOGGER.debug(">>>>>>>>>>>> " + sql);
        LOGGER.debug(">>>>>>>>>>>>> " + id);
        return super.selectOne(connection, sql, entityClass, id);
    }

    public Optional<E> queryById(Connection connection, ID id) {
        E result = findById(connection, id);
        if (result == null) {
            return Optional.empty();
        }
        return Optional.of(result);
    }

    private class SqlAndArgs<T> {
        DynamicSqlBuilder sqlBuilder;
        Object[] args;
        Class<T> entityClass;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DmlRepositoryHandler.class);
}
