package com.truthbean.debbie.jdbc.repository;

import com.truthbean.debbie.reflection.ReflectionHelper;
import com.truthbean.debbie.jdbc.column.ColumnInfo;
import com.truthbean.debbie.jdbc.domain.Page;
import com.truthbean.debbie.jdbc.domain.PageRequest;
import com.truthbean.debbie.jdbc.entity.EntityInfo;
import com.truthbean.debbie.jdbc.entity.EntityResolver;
import com.truthbean.debbie.jdbc.transaction.TransactionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.*;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/4/3 23:23.
 */
public class DmlRepositoryHandler<E, ID> extends RepositoryHandler {

    private Class<E> entityClass;
    private Class<ID> idClass;
    private EntityInfo<E> entityInfo;

    private EntityResolver entityResolver = new EntityResolver();

    public DmlRepositoryHandler() {
    }

    public static <E, ID> DmlRepositoryHandler<E, ID> of(Class<E> entityClass, Class<ID> idClass) {
        var handler = new DmlRepositoryHandler<E, ID>();
        handler.entityClass = entityClass;
        handler.idClass = idClass;

        return handler;
    }

    private EntityInfo<E> getEntityInfo() {
        if (entityInfo == null) {
            entityInfo = entityResolver.resolveEntityClass(getEntityClass());
        }
        return entityInfo;
    }

    @SuppressWarnings("unchecked")
    protected Class<E> getEntityClass() {
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

    protected static class ConditionAndValue {
        private String conditionSql;
        private List<Object> conditionValues;

        public String getConditionSql() {
            return conditionSql;
        }

        public List<Object> getConditionValues() {
            return conditionValues;
        }

        public boolean isEmpty() {
            return conditionSql.isBlank() || conditionValues.isEmpty();
        }
    }

    public int deleteByIdIn(Connection connection, List<ID> ids) throws TransactionException {
        var primaryKey = entityInfo.getPrimaryKey();
        return deleteByColumnIn(connection, primaryKey.getColumnName(), ids);
    }

    public <C> int deleteByColumnIn(Connection connection, String columnName, List<C> values) throws TransactionException {
        if (values == null || values.size() == 0) return 0;
        int length = values.size();
        if (length == 1) return deleteByColumn(connection, columnName, values.get(0)) ? 1 : 0;

        var entityInfo = getEntityInfo();
        var table = entityInfo.getTable();
        String sql = DynamicSqlBuilder.sql().delete().from(table).where().in(columnName, length).builder();
        return super.update(connection, sql, values.toArray());
    }

    public boolean deleteById(Connection connection, ID id) throws TransactionException {
        var entityInfo = getEntityInfo();
        var table = entityInfo.getTable();
        var primaryKey = entityInfo.getPrimaryKey();
        String sql = DynamicSqlBuilder.sql().delete().from(table).where().eq(primaryKey.getColumnName(), "?").builder();
        return super.update(connection, sql, id) > 0L;
    }

    public boolean deleteByColumn(Connection connection, String columnName, Object value) throws TransactionException {
        var entityInfo = getEntityInfo();
        var table = entityInfo.getTable();
        String sql = DynamicSqlBuilder.sql().delete().from(table).where().eq(columnName, "?").builder();
        return super.update(connection, sql, value) > 0L;
    }

    protected <T> ConditionAndValue resolveCondition(T condition, boolean withNull) {
        if (condition != null) {
            var conditionInfo = entityResolver.resolveEntity(condition);
            return resolveCondition(conditionInfo, withNull);
        }
        return new ConditionAndValue();
    }

    private <T> ConditionAndValue resolveCondition(EntityInfo<T> entityInfo, boolean withNull) {
        List<ColumnInfo> columns = entityInfo.getColumnInfoList();

        var sqlBuilder = DynamicSqlBuilder.sql();
        List<Object> columnValues = new LinkedList<>();

        columns.forEach(column -> {
            var value = column.getValue();
            var bool = withNull || value != null;
            if (bool) {
                columnValues.add(value);
                sqlBuilder.and(column.getColumnName() + " = ?");
            }
        });

        var conditionAndValue = new ConditionAndValue();
        conditionAndValue.conditionSql = sqlBuilder.builder();
        var trim = conditionAndValue.conditionSql.trim();
        if (trim.startsWith("and") || trim.startsWith("AND")) {
            var indexOf = conditionAndValue.conditionSql.indexOf("and");
            if (indexOf == -1) {
                indexOf = conditionAndValue.conditionSql.indexOf("AND");
            }
            conditionAndValue.conditionSql = " " + conditionAndValue.conditionSql.substring(indexOf + 3);
        }
        conditionAndValue.conditionValues = columnValues;
        return conditionAndValue;
    }

    public int delete(Connection connection, E condition, boolean withNull) throws TransactionException {
        var entityInfo = entityResolver.resolveEntity(condition);
        var table = entityInfo.getTable();
        var conditionAndValue = resolveCondition(entityInfo, withNull);

        var sqlBuilder = DynamicSqlBuilder.sql().delete().from(table);
        if (!conditionAndValue.isEmpty()) {
            sqlBuilder.where().extra(conditionAndValue.conditionSql);

            List<Object> columnValues = conditionAndValue.conditionValues;
            var sql = sqlBuilder.builder();
            return super.update(connection, sql, columnValues.toArray());
        } else {
            var sql = sqlBuilder.builder();
            return super.update(connection, sql);
        }
    }

    public int delete(Connection connection) throws TransactionException {
        var entityInfo = getEntityInfo();
        var table = entityInfo.getTable();

        var sqlBuilder = DynamicSqlBuilder.sql().delete().from(table);

        var sql = sqlBuilder.builder();
        return super.update(connection, sql);
    }

    @SuppressWarnings("unchecked")
    public ID insert(Connection connection, E entity, boolean withNull) throws TransactionException {
        var entityInfo = entityResolver.resolveEntity(entity);
        var table = entityInfo.getTable();
        var columns = entityInfo.getColumnInfoList();

        List<String> columnNames = new LinkedList<>();
        List<Object> columnValues = new LinkedList<>();
        List<Object> signs = new LinkedList<>();
        columns.forEach(column -> {
            var value = column.getValue();
            var bool = withNull || value != null;
            if (bool) {
                columnValues.add(value);
                columnNames.add(column.getColumnName());
                signs.add("?");
            }
        });

        var sql = DynamicSqlBuilder.sql().insert().extra(table).leftParenthesis()
            .joinWith(",", columnNames).rightParenthesis().values(signs).builder();
        var primaryKey = entityInfo.getPrimaryKey();
        if (primaryKey != null) {
            var generatedKeys = primaryKey.getPrimaryKeyType() != null;
            return (ID) super.insert(connection, sql, generatedKeys, primaryKey.getJavaClass(), columnValues.toArray());
        }
        return (ID) super.insert(connection, sql, false, null, columnValues.toArray());
    }

    public int insert(Connection connection, Collection<E> entities, boolean withNull) throws TransactionException {
        Iterator<E> iterator = entities.iterator();
        E entity = iterator.next();
        var entityInfo = entityResolver.resolveEntity(entity);

        var table = entityInfo.getTable();
        var columns = entityInfo.getColumnInfoList();

        Set<List<ColumnInfo>> columnSet = new LinkedHashSet<>();
        columnSet.add(columns);
        while (iterator.hasNext()) {
            E e = iterator.next();
            EntityInfo<E> eEntityInfo = entityResolver.resolveEntity(e);
            columnSet.add(eEntityInfo.getColumnInfoList());
        }

        List<String> columnNames = new LinkedList<>();
        List<String> signs = new LinkedList<>();

        columns.forEach(column -> {
            var value = column.getValue();
            var bool = withNull || value != null;
            if (bool) {
                columnNames.add(column.getColumnName());
                signs.add("?");
            }
        });

        List<Object> columnValues = new LinkedList<>();

        columnSet.forEach((e) -> {
            e.forEach(column -> {
                var value = column.getValue();
                var bool = withNull || value != null;
                if (bool) {
                    columnValues.add(value);
                }
            });
        });

        var sqlBuilder = DynamicSqlBuilder.sql().insert().extra(table)
            .leftParenthesis().joinWith(",", columnNames).rightParenthesis()
            .extra(" VALUES ");

        int size = entities.size();
        String[] values = new String[size];
        for (int i = 0; i < size; i++) {
            values[i] = DynamicSqlBuilder.sql().leftParenthesis().joinWith(",", signs).rightParenthesis().builder();
        }
        sqlBuilder.joinWith(",", values);
        return super.update(connection, sqlBuilder.builder(), columnValues.toArray());
    }

    public boolean update(Connection connection, E entity, boolean withNull) throws TransactionException {
        var entityInfo = entityResolver.resolveEntity(entity);
        var table = entityInfo.getTable();
        var columns = entityInfo.getColumnInfoList();
        var primaryKey = entityInfo.getPrimaryKey();

        List<String> columnNames = new LinkedList<>();
        List<Object> columnValues = new LinkedList<>();
        for (ColumnInfo column : columns) {
            var value = column.getValue();
            var bool = withNull || value != null;
            if (!column.isPrimaryKey() && bool) {
                columnValues.add(value);
                columnNames.add(column.getColumnName());
            }
        }

        columnValues.add(primaryKey.getValue());

        var sql = DynamicSqlBuilder.sql().update(table).set(columnNames)
            .where().eq(primaryKey.getColumnName(), "?").builder();
        return super.update(connection, sql, columnValues.toArray()) == 1;
    }

    public int update(Connection connection, E entity, boolean withNull, String whereSql, Object... args) {
        var entityInfo = entityResolver.resolveEntity(entity);
        var table = entityInfo.getTable();
        var columns = entityInfo.getColumnInfoList();

        List<String> columnNames = new LinkedList<>();
        for (ColumnInfo column : columns) {
            var value = column.getValue();
            var bool = withNull || value != null;
            if (!column.isPrimaryKey() && bool) {
                columnNames.add(column.getColumnName());
            }
        }

        var sql = DynamicSqlBuilder.sql().update(table).set(columnNames);
        if (whereSql != null && !whereSql.isBlank()) {
            var trimWhereSql = whereSql.trim();
            if (trimWhereSql.startsWith("where") || trimWhereSql.startsWith("WHERE")) {
                sql.extra(whereSql);
            } else {
                sql.where().extra(whereSql);
            }
        }

        return super.update(connection, sql.builder(), args);
    }

    @SuppressWarnings("unchecked")
    public <S extends E> S save(Connection connection, S entity) {
        var entityInfo = entityResolver.resolveEntity(entity);
        ColumnInfo primaryKey = entityInfo.getPrimaryKey();
        if (primaryKey.getValue() != null) {
            var bool = update(connection, entity, true);
            if (bool) {
                return (S) selectOne(connection, entity, false);
            } else {
                return entity;
            }
        } else {
            ID insert = insert(connection, entity, true);
            return (S) selectById(connection, insert);
        }
    }

    public E selectOne(Connection connection, E condition, boolean withNull) {
        var entityInfo = getEntityInfo();
        var entityClass = entityInfo.getJavaType();

        var table = entityInfo.getTable();
        var columns = entityInfo.getColumnInfoList();

        List<String> columnNames = new LinkedList<>();
        for (ColumnInfo column : columns) {
            columnNames.add(column.getColumnName());
        }

        var sql = DynamicSqlBuilder.sql().select(columnNames).from(table);

        var conditionAndValues = resolveCondition(condition, withNull);
        if (!conditionAndValues.isEmpty()) {
            sql.where().extra(conditionAndValues.conditionSql);
            return super.queryOne(connection, sql.builder(), entityClass, conditionAndValues.conditionValues.toArray());
        } else {
            return super.queryOne(connection, sql.builder(), entityClass);
        }
    }

    public E selectOne(Connection connection, String whereSql, Object... args) {
        var entityInfo = getEntityInfo();
        var entityClass = entityInfo.getJavaType();

        var table = entityInfo.getTable();
        var columns = entityInfo.getColumnInfoList();

        List<String> columnNames = new LinkedList<>();
        for (ColumnInfo column : columns) {
            columnNames.add(column.getColumnName());
        }

        var sql = DynamicSqlBuilder.sql().select(columnNames).from(table);
        if (whereSql != null && !whereSql.isBlank()) {
            var trimWhereSql = whereSql.trim();
            if (trimWhereSql.startsWith("where") || trimWhereSql.startsWith("WHERE")) {
                sql.extra(whereSql);
            } else {
                sql.where().extra(whereSql);
            }
        }

        return super.queryOne(connection, sql.builder(), entityClass, args);
    }

    protected <T> DynamicSqlBuilder select(T entity) {
        if (entity != null) {
            var entityInfo = entityResolver.resolveEntity(entity);

            var table = entityInfo.getTable();
            var columns = entityInfo.getColumnInfoList();

            List<String> columnNames = new LinkedList<>();
            for (ColumnInfo column : columns) {
                columnNames.add(column.getColumnName());
            }

            return DynamicSqlBuilder.sql().select(columnNames).from(table);
        }
        return null;
    }

    private SqlAndArgs<E> preSelect(E condition, boolean withNull) {
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
            var conditionAndValues = resolveCondition(condition, withNull);
            if (!conditionAndValues.isEmpty()) {
                sqlBuilder.where().extra(conditionAndValues.conditionSql);
                args = conditionAndValues.conditionValues.toArray();
            }
        }

        var sqlAndArgs = new SqlAndArgs<E>();
        sqlAndArgs.sqlBuilder = sqlBuilder;
        sqlAndArgs.args = args;
        sqlAndArgs.entityClass = entityClass;

        return sqlAndArgs;
    }

    private SqlAndArgs<E> preSelect(String whereSql, Object... args) {
        var entityInfo = getEntityInfo();
        var entityClass = entityInfo.getJavaType();

        var table = entityInfo.getTable();
        var columns = entityInfo.getColumnInfoList();

        List<String> columnNames = new LinkedList<>();
        for (ColumnInfo column : columns) {
            columnNames.add(column.getColumnName());
        }

        var sqlBuilder = DynamicSqlBuilder.sql().select(columnNames).from(table);
        if (whereSql != null && !whereSql.isBlank()) {
            var trimWhereSql = whereSql.trim();
            if (trimWhereSql.startsWith("where") || trimWhereSql.startsWith("WHERE")) {
                sqlBuilder.extra(whereSql);
            } else {
                sqlBuilder.where().extra(whereSql);
            }
        }

        var sqlAndArgs = new SqlAndArgs<E>();
        sqlAndArgs.sqlBuilder = sqlBuilder;
        sqlAndArgs.args = args;
        sqlAndArgs.entityClass = entityClass;

        return sqlAndArgs;
    }

    public List<E> selectList(Connection connection, E condition, boolean withNull) {
        SqlAndArgs<E> sqlAndArgs = preSelect(condition, withNull);

        var sql = sqlAndArgs.sqlBuilder.builder();
        return super.query(connection, sql, sqlAndArgs.entityClass, sqlAndArgs.args);
    }

    public List<E> selectList(Connection connection, String whereSql, Object... args) {
        SqlAndArgs<E> sqlAndArgs = preSelect(whereSql, args);

        var sql = sqlAndArgs.sqlBuilder.builder();
        return super.query(connection, sql, sqlAndArgs.entityClass, sqlAndArgs.args);
    }

    public Page<E> selectPaged(Connection connection, E condition, boolean withNull, PageRequest pageable) {
        SqlAndArgs<E> sqlAndArgs = preSelect(condition, withNull);

        var sql = sqlAndArgs.sqlBuilder.limit(pageable.getOffset(), pageable.getPageSize()).builder();

        var count = count(connection, condition, withNull);
        List<E> content = super.query(connection, sql, sqlAndArgs.entityClass, sqlAndArgs.args);
        return Page.createPage(pageable.getCurrentPage(), pageable.getPageSize(), count, content);
    }

    public Page<E> selectPaged(Connection connection, PageRequest pageable, String whereSql, Object... args) {
        SqlAndArgs<E> sqlAndArgs = preSelect(whereSql, args);

        var sql = sqlAndArgs.sqlBuilder.limit(pageable.getOffset(), pageable.getPageSize()).builder();

        var count = count(connection);
        List<E> content = super.query(connection, sql, sqlAndArgs.entityClass, sqlAndArgs.args);
        return Page.createPage(pageable.getCurrentPage(), pageable.getPageSize(), count, content);
    }

    public Page<E> selectPaged(Connection connection, PageRequest pageable) {
        return selectPaged(connection, pageable, null);
    }

    public List<E> selectAll(Connection connection) {
        SqlAndArgs<E> sqlAndArgs = preSelect(null, false);

        var sql = sqlAndArgs.sqlBuilder.builder();
        return super.query(connection, sql, sqlAndArgs.entityClass, sqlAndArgs.args);
    }

    public Long count(Connection connection, E condition, boolean withNull) {
        var entityInfo = getEntityInfo();

        var table = entityInfo.getTable();

        var sqlBuilder = DynamicSqlBuilder.sql().select().count().from(table);
        Object[] args = null;

        if (condition != null) {
            var conditionAndValues = resolveCondition(condition, withNull);
            if (!conditionAndValues.isEmpty()) {
                sqlBuilder.where().extra(conditionAndValues.conditionSql);
                args = conditionAndValues.conditionValues.toArray();
            }
        }

        var sql = sqlBuilder.builder();
        return super.queryOne(connection, sql, Long.class, args);
    }

    public Long count(Connection connection) {
        var entityInfo = getEntityInfo();
        var table = entityInfo.getTable();
        var sql = DynamicSqlBuilder.sql().select().count().from(table).builder();
        return super.queryOne(connection, sql, Long.class);
    }

    public E selectById(Connection connection, ID id) {
        var entityClass = getEntityClass();
        var entityInfo = entityResolver.resolveEntityClass(entityClass);

        var table = entityInfo.getTable();
        var columns = entityInfo.getColumnInfoList();
        var primaryKey = entityInfo.getPrimaryKey();

        List<String> columnNames = new LinkedList<>();
        for (ColumnInfo column : columns) {
            columnNames.add(column.getColumnName());
        }

        var sql = DynamicSqlBuilder.sql().select(columnNames).from(table)
            .where().eq(primaryKey.getColumnName(), "?").builder();
        return super.queryOne(connection, sql, entityClass, id);
    }

    public Boolean existsById(Connection connection, ID id) {
        var entityInfo = getEntityInfo();
        var table = entityInfo.getTable();
        var primaryKey = entityInfo.getPrimaryKey();
        var subSql = DynamicSqlBuilder.sql().select(primaryKey.getColumnName()).from(table)
            .where().eq(primaryKey.getColumnName(), "?").builder();
        var sql = DynamicSqlBuilder.sql().select().exist(subSql).builder();
        return super.queryOne(connection, sql, Long.class, id) > 0L;
    }

    public Optional<E> selectOptionalById(Connection connection, ID id) {
        E result = selectById(connection, id);
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
