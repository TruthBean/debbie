package com.truthbean.debbie.jdbc.repository;

import com.truthbean.debbie.bean.BeanAware;
import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.jdbc.column.ColumnInfo;
import com.truthbean.debbie.jdbc.datasource.DataSourceConfiguration;
import com.truthbean.debbie.jdbc.datasource.DataSourceDriverName;
import com.truthbean.debbie.jdbc.datasource.DriverConnection;
import com.truthbean.debbie.jdbc.domain.Page;
import com.truthbean.debbie.jdbc.domain.PageRequest;
import com.truthbean.debbie.jdbc.entity.EntityInfo;
import com.truthbean.debbie.jdbc.entity.EntityResolver;
import com.truthbean.debbie.jdbc.entity.SqlEntityNullException;
import com.truthbean.debbie.jdbc.transaction.TransactionException;
import com.truthbean.debbie.reflection.ReflectionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.*;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/4/3 23:23.
 */
public class DmlRepositoryHandler<E, ID> extends RepositoryHandler implements BeanAware<DataSourceConfiguration> {

    private Class<E> entityClass;
    private Class<ID> idClass;
    private EntityInfo<E> entityInfo;

    private final EntityResolver entityResolver;

    // @BeanInject
    private DataSourceConfiguration configuration;

    @Override
    public Class<DataSourceConfiguration> getBeanClass() {
        return DataSourceConfiguration.class;
    }

    @Override
    public void setBean(DataSourceConfiguration configuration) {
        this.configuration = configuration;
        super.setDriverName(configuration.getDriverName());
    }

    public DmlRepositoryHandler() {
        entityResolver = new EntityResolver();
    }

    public static <E, ID> DmlRepositoryHandler<E, ID> of(DataSourceDriverName driverName,
                                                         Class<E> entityClass, Class<ID> idClass) {
        var handler = new DmlRepositoryHandler<E, ID>();
        handler.entityClass = entityClass;
        handler.idClass = idClass;
        handler.setDriverName(driverName);

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
                throw new SqlEntityNullException("get entity class error");
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

    public int deleteByIdIn(DriverConnection connection, List<ID> ids) throws TransactionException {
        var primaryKey = entityInfo.getPrimaryKey();
        return deleteByColumnIn(connection, primaryKey.getColumnName(), ids);
    }

    public <C> int deleteByColumnIn(DriverConnection driverConnection, String columnName, List<C> values) throws TransactionException {
        if (values == null || values.size() == 0) return 0;

        Connection connection = driverConnection.getConnection();
        DataSourceDriverName driverName = driverConnection.getDriverName();
        int length = values.size();
        if (length == 1) return deleteByColumn(driverConnection, columnName, values.get(0)) ? 1 : 0;

        var entityInfo = getEntityInfo();
        var table = entityInfo.getTable();
        String sql = DynamicSqlBuilder.sql(driverName).delete().from(table).where().in(columnName, length).builder();
        return super.update(connection, sql, values.toArray());
    }

    public boolean deleteById(DriverConnection driverConnection, ID id) throws TransactionException {
        Connection connection = driverConnection.getConnection();
        DataSourceDriverName driverName = driverConnection.getDriverName();

        var entityInfo = getEntityInfo();
        var table = entityInfo.getTable();
        var primaryKey = entityInfo.getPrimaryKey();
        String sql = DynamicSqlBuilder.sql(driverName).delete().from(table).where().eq(primaryKey.getColumnName(), "?").builder();
        return super.update(connection, sql, id) > 0L;
    }

    public boolean deleteByColumn(DriverConnection driverConnection, String columnName, Object value) throws TransactionException {
        Connection connection = driverConnection.getConnection();
        DataSourceDriverName driverName = driverConnection.getDriverName();

        var entityInfo = getEntityInfo();
        var table = entityInfo.getTable();
        String sql = DynamicSqlBuilder.sql(driverName).delete().from(table).where().eq(columnName, "?").builder();
        return super.update(connection, sql, value) > 0L;
    }

    protected <T> ConditionAndValue resolveCondition(DataSourceDriverName driverName, T condition, boolean withNull) {
        if (condition != null) {
            var conditionInfo = entityResolver.resolveEntity(driverName, condition);
            return resolveCondition(conditionInfo, withNull);
        }
        return new ConditionAndValue();
    }

    private <T> ConditionAndValue resolveCondition(EntityInfo<T> entityInfo, boolean withNull) {
        List<ColumnInfo> columns = entityInfo.getColumnInfoList();

        var sqlBuilder = DynamicSqlBuilder.sql(entityInfo.getDriverName());
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

    public int delete(DriverConnection driverConnection, E condition, boolean withNull) throws TransactionException {
        Connection connection = driverConnection.getConnection();
        DataSourceDriverName driverName = driverConnection.getDriverName();

        var entityInfo = entityResolver.resolveEntity(driverName, condition);
        var table = entityInfo.getTable();
        var conditionAndValue = resolveCondition(entityInfo, withNull);

        var sqlBuilder = DynamicSqlBuilder.sql(driverName).delete().from(table);
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

    public int delete(DriverConnection driverConnection) throws TransactionException {
        Connection connection = driverConnection.getConnection();
        DataSourceDriverName driverName = driverConnection.getDriverName();

        var entityInfo = getEntityInfo();
        var table = entityInfo.getTable();

        var sqlBuilder = DynamicSqlBuilder.sql(driverName).delete().from(table);

        var sql = sqlBuilder.builder();
        return super.update(connection, sql);
    }

    @SuppressWarnings("unchecked")
    public ID insert(DriverConnection driverConnection, E entity, boolean withNull) throws TransactionException {
        Connection connection = driverConnection.getConnection();
        DataSourceDriverName driverName = driverConnection.getDriverName();

        var entityInfo = entityResolver.resolveEntity(driverName, entity);
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

        var sql = DynamicSqlBuilder.sql(driverName).insert().extra(table).leftParenthesis()
            .joinWith(",", columnNames).rightParenthesis().values(signs).builder();
        var primaryKey = entityInfo.getPrimaryKey();
        if (primaryKey != null) {
            var generatedKeys = primaryKey.getPrimaryKeyType() != null;
            return (ID) super.insert(connection, sql, generatedKeys, primaryKey.getJavaClass(), columnValues.toArray());
        }
        return (ID) super.insert(connection, sql, false, null, columnValues.toArray());
    }

    public int insert(DriverConnection driverConnection, Collection<E> entities, boolean withNull) throws TransactionException {
        if (entities.isEmpty()) return 0;

        Connection connection = driverConnection.getConnection();
        DataSourceDriverName driverName = driverConnection.getDriverName();

        Iterator<E> iterator = entities.iterator();
        E entity = iterator.next();
        var entityInfo = entityResolver.resolveEntity(driverName, entity);

        var table = entityInfo.getTable();
        var columns = entityInfo.getColumnInfoList();

        Set<List<ColumnInfo>> columnSet = new LinkedHashSet<>();
        columnSet.add(columns);
        while (iterator.hasNext()) {
            E e = iterator.next();
            EntityInfo<E> eEntityInfo = entityResolver.resolveEntity(driverName, e);
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

        var sqlBuilder = DynamicSqlBuilder.sql(driverName).insert().extra(table)
            .leftParenthesis().joinWith(",", columnNames).rightParenthesis()
            .extra(" VALUES ");

        int size = entities.size();
        String[] values = new String[size];
        for (int i = 0; i < size; i++) {
            values[i] = DynamicSqlBuilder.sql(driverName).leftParenthesis().joinWith(",", signs).rightParenthesis().builder();
        }
        sqlBuilder.joinWith(",", values);
        return super.update(connection, sqlBuilder.builder(), columnValues.toArray());
    }

    public boolean update(DriverConnection driverConnection, E entity, boolean withNull) throws TransactionException {
        Connection connection = driverConnection.getConnection();
        DataSourceDriverName driverName = driverConnection.getDriverName();

        var entityInfo = entityResolver.resolveEntity(driverName, entity);
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

        var sql = DynamicSqlBuilder.sql(driverName).update(table).set(columnNames)
            .where().eq(primaryKey.getColumnName(), "?").builder();
        return super.update(connection, sql, columnValues.toArray()) == 1;
    }

    public int update(DriverConnection driverConnection, E entity, boolean withNull, String whereSql, Object... args) {
        Connection connection = driverConnection.getConnection();
        DataSourceDriverName driverName = driverConnection.getDriverName();

        var entityInfo = entityResolver.resolveEntity(driverName, entity);
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

        var sql = DynamicSqlBuilder.sql(driverName).update(table).set(columnNames);
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
    public <S extends E> S save(DriverConnection driverConnection, S entity) {
        Connection connection = driverConnection.getConnection();
        DataSourceDriverName driverName = driverConnection.getDriverName();

        var entityInfo = entityResolver.resolveEntity(driverName, entity);
        ColumnInfo primaryKey = entityInfo.getPrimaryKey();
        if (primaryKey.getValue() != null) {
            var bool = update(driverConnection, entity, true);
            if (bool) {
                return (S) selectOne(driverConnection, entity, false);
            } else {
                return entity;
            }
        } else {
            ID insert = insert(driverConnection, entity, true);
            return (S) selectById(driverConnection, insert);
        }
    }

    public E selectOne(DriverConnection driverConnection, E condition, boolean withNull) {
        Connection connection = driverConnection.getConnection();
        DataSourceDriverName driverName = driverConnection.getDriverName();

        var entityInfo = getEntityInfo();
        var entityClass = entityInfo.getJavaType();

        var table = entityInfo.getTable();
        var columns = entityInfo.getColumnInfoList();

        List<String> columnNames = new LinkedList<>();
        for (ColumnInfo column : columns) {
            columnNames.add(column.getColumnName());
        }

        var sql = DynamicSqlBuilder.sql(driverName).select(columnNames).from(table);

        var conditionAndValues = resolveCondition(driverName, condition, withNull);
        if (!conditionAndValues.isEmpty()) {
            sql.where().extra(conditionAndValues.conditionSql);
            return super.queryOne(connection, sql.builder(), entityClass, conditionAndValues.conditionValues.toArray());
        } else {
            return super.queryOne(connection, sql.builder(), entityClass);
        }
    }

    public E selectOne(DriverConnection driverConnection, String whereSql, Object... args) {
        Connection connection = driverConnection.getConnection();
        DataSourceDriverName driverName = driverConnection.getDriverName();

        var entityInfo = getEntityInfo();
        var entityClass = entityInfo.getJavaType();

        var table = entityInfo.getTable();
        var columns = entityInfo.getColumnInfoList();

        List<String> columnNames = new LinkedList<>();
        for (ColumnInfo column : columns) {
            columnNames.add(column.getColumnName());
        }

        var sql = DynamicSqlBuilder.sql(driverName).select(columnNames).from(table);
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

    protected <T> DynamicSqlBuilder select(DataSourceDriverName driverName, T entity) {
        if (entity != null) {
            var entityInfo = entityResolver.resolveEntity(driverName, entity);

            var table = entityInfo.getTable();
            var columns = entityInfo.getColumnInfoList();

            List<String> columnNames = new LinkedList<>();
            for (ColumnInfo column : columns) {
                columnNames.add(column.getColumnName());
            }

            return DynamicSqlBuilder.sql(driverName).select(columnNames).from(table);
        }
        return null;
    }

    private SqlAndArgs<E> preSelect(DataSourceDriverName driverName, E condition, boolean withNull) {
        var entityInfo = getEntityInfo();
        var entityClass = entityInfo.getJavaType();

        var table = entityInfo.getTable();
        var columns = entityInfo.getColumnInfoList();

        List<String> columnNames = new LinkedList<>();
        for (ColumnInfo column : columns) {
            columnNames.add(column.getColumnName());
        }

        var sqlBuilder = DynamicSqlBuilder.sql(driverName).select(columnNames).from(table);
        Object[] args = null;

        if (condition != null) {
            var conditionAndValues = resolveCondition(driverName, condition, withNull);
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

    private SqlAndArgs<E> preSelect(DataSourceDriverName driverName, String whereSql, Object... args) {
        var entityInfo = getEntityInfo();
        var entityClass = entityInfo.getJavaType();

        var table = entityInfo.getTable();
        var columns = entityInfo.getColumnInfoList();

        List<String> columnNames = new LinkedList<>();
        for (ColumnInfo column : columns) {
            columnNames.add(column.getColumnName());
        }

        var sqlBuilder = DynamicSqlBuilder.sql(driverName).select(columnNames).from(table);
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

    public List<E> selectList(DriverConnection driverConnection, E condition, boolean withNull) {
        Connection connection = driverConnection.getConnection();
        DataSourceDriverName driverName = driverConnection.getDriverName();

        SqlAndArgs<E> sqlAndArgs = preSelect(driverName, condition, withNull);

        var sql = sqlAndArgs.sqlBuilder.builder();
        return super.query(connection, sql, sqlAndArgs.entityClass, sqlAndArgs.args);
    }

    public List<E> selectList(DriverConnection driverConnection, String whereSql, Object... args) {
        Connection connection = driverConnection.getConnection();
        DataSourceDriverName driverName = driverConnection.getDriverName();

        SqlAndArgs<E> sqlAndArgs = preSelect(driverName, whereSql, args);

        var sql = sqlAndArgs.sqlBuilder.builder();
        return super.query(connection, sql, sqlAndArgs.entityClass, sqlAndArgs.args);
    }

    public Page<E> selectPaged(DriverConnection driverConnection, E condition, boolean withNull, PageRequest pageable) {
        Connection connection = driverConnection.getConnection();
        DataSourceDriverName driverName = driverConnection.getDriverName();
        SqlAndArgs<E> sqlAndArgs = preSelect(driverName, condition, withNull);

        var sql = sqlAndArgs.sqlBuilder.limit(pageable.getOffset(), pageable.getPageSize()).builder();

        var count = count(driverConnection, condition, withNull);
        List<E> content = super.query(connection, sql, sqlAndArgs.entityClass, sqlAndArgs.args);
        return Page.createPage(pageable.getCurrentPage(), pageable.getPageSize(), count, content);
    }

    public Page<E> selectPaged(DriverConnection driverConnection, PageRequest pageable, String whereSql, Object... args) {
        Connection connection = driverConnection.getConnection();
        DataSourceDriverName driverName = driverConnection.getDriverName();

        SqlAndArgs<E> sqlAndArgs = preSelect(driverName, whereSql, args);

        var sql = sqlAndArgs.sqlBuilder.limit(pageable.getOffset(), pageable.getPageSize()).builder();

        var count = count(driverConnection);
        List<E> content = super.query(connection, sql, sqlAndArgs.entityClass, sqlAndArgs.args);
        return Page.createPage(pageable.getCurrentPage(), pageable.getPageSize(), count, content);
    }

    public Page<E> selectPaged(DriverConnection connection, PageRequest pageable) {
        return selectPaged(connection, pageable, null);
    }

    public List<E> selectAll(DriverConnection driverConnection) {
        Connection connection = driverConnection.getConnection();
        DataSourceDriverName driverName = driverConnection.getDriverName();
        SqlAndArgs<E> sqlAndArgs = preSelect(driverName, null, false);

        var sql = sqlAndArgs.sqlBuilder.builder();
        return super.query(connection, sql, sqlAndArgs.entityClass, sqlAndArgs.args);
    }

    public Long count(DriverConnection driverConnection, E condition, boolean withNull) {
        Connection connection = driverConnection.getConnection();
        DataSourceDriverName driverName = driverConnection.getDriverName();

        var entityInfo = getEntityInfo();

        var table = entityInfo.getTable();

        var sqlBuilder = DynamicSqlBuilder.sql(driverName).select().count().from(table);
        Object[] args = null;

        if (condition != null) {
            var conditionAndValues = resolveCondition(driverName, condition, withNull);
            if (!conditionAndValues.isEmpty()) {
                sqlBuilder.where().extra(conditionAndValues.conditionSql);
                args = conditionAndValues.conditionValues.toArray();
            }
        }

        var sql = sqlBuilder.builder();
        return super.queryOne(connection, sql, Long.class, args);
    }

    public Long count(DriverConnection driverConnection) {
        Connection connection = driverConnection.getConnection();
        DataSourceDriverName driverName = driverConnection.getDriverName();

        var entityInfo = getEntityInfo();
        var table = entityInfo.getTable();
        var sql = DynamicSqlBuilder.sql(driverName).select().count().from(table).builder();
        return super.queryOne(connection, sql, Long.class);
    }

    public E selectById(DriverConnection driverConnection, ID id) {
        Connection connection = driverConnection.getConnection();
        DataSourceDriverName driverName = driverConnection.getDriverName();

        var entityClass = getEntityClass();
        var entityInfo = entityResolver.resolveEntityClass(entityClass);

        var table = entityInfo.getTable();
        var columns = entityInfo.getColumnInfoList();
        var primaryKey = entityInfo.getPrimaryKey();

        List<String> columnNames = new LinkedList<>();
        for (ColumnInfo column : columns) {
            columnNames.add(column.getColumnName());
        }

        var sql = DynamicSqlBuilder.sql(driverName).select(columnNames).from(table)
            .where().eq(primaryKey.getColumnName(), "?").builder();
        return super.queryOne(connection, sql, entityClass, id);
    }

    public Boolean existsById(DriverConnection driverConnection, ID id) {
        Connection connection = driverConnection.getConnection();
        DataSourceDriverName driverName = driverConnection.getDriverName();

        var entityInfo = getEntityInfo();
        var table = entityInfo.getTable();
        var primaryKey = entityInfo.getPrimaryKey();
        var subSql = DynamicSqlBuilder.sql(driverName).select(primaryKey.getColumnName()).from(table)
            .where().eq(primaryKey.getColumnName(), "?").builder();
        var sql = DynamicSqlBuilder.sql(driverName).select().exist(subSql).builder();
        return super.queryOne(connection, sql, Long.class, id) > 0L;
    }

    public Optional<E> selectOptionalById(DriverConnection driverConnection, ID id) {
        E result = selectById(driverConnection, id);
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
