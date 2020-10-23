/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.jdbc.repository;

import com.truthbean.debbie.jdbc.column.ColumnInfo;
import com.truthbean.debbie.jdbc.datasource.DataSourceConfiguration;
import com.truthbean.debbie.jdbc.datasource.DataSourceDriverName;
import com.truthbean.debbie.jdbc.domain.Page;
import com.truthbean.debbie.jdbc.domain.PageRequest;
import com.truthbean.debbie.jdbc.entity.EntityInfo;
import com.truthbean.debbie.jdbc.entity.EntityResolver;
import com.truthbean.debbie.jdbc.entity.SqlEntityNullException;
import com.truthbean.debbie.jdbc.transaction.TransactionInfo;
import com.truthbean.debbie.reflection.ReflectionHelper;
import com.truthbean.Logger;
import com.truthbean.logger.LoggerFactory;

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
    private Class<?> repository;

    private final EntityResolver entityResolver;

    public DmlRepositoryHandler() {
        entityResolver = new EntityResolver();
    }

    public DmlRepositoryHandler(DataSourceConfiguration configuration, Class<E> entityClass, Class<ID> idClass) {
        entityResolver = new EntityResolver();
        super.setDriverName(configuration.getDriverName());
        this.entityClass = entityClass;
        this.idClass = idClass;
    }

    public DmlRepositoryHandler(DataSourceConfiguration configuration, Class<?> repository) {
        entityResolver = new EntityResolver();
        super.setDriverName(configuration.getDriverName());
        this.repository = repository;
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
                Class<?> clazz = repository;
                if (clazz == null) {
                    clazz = getClass();
                }
                var types = ReflectionHelper.getActualTypes(clazz);
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

    public int deleteByIdIn(TransactionInfo transaction, List<ID> ids) {
        var primaryKey = entityInfo.getPrimaryKey();
        return deleteByColumnIn(transaction, primaryKey.getColumnName(), ids);
    }

    public <C> int deleteByColumnIn(TransactionInfo transaction, String columnName, List<C> values) {
        if (values == null || values.isEmpty()) return 0;

        DataSourceDriverName driverName = transaction.getDriverName();
        int length = values.size();
        if (length == 1) return deleteByColumn(transaction, columnName, values.get(0)) ? 1 : 0;

        var entityInfo = getEntityInfo();
        var table = entityInfo.getTable();
        String sql = DynamicRepository.sql(driverName).delete().from(table).where().in(columnName, length).builder();
        return super.update(transaction, sql, values.toArray());
    }

    public boolean deleteById(TransactionInfo transaction, ID id) {
        DataSourceDriverName driverName = transaction.getDriverName();

        var entityInfo = getEntityInfo();
        var table = entityInfo.getTable();
        var primaryKey = entityInfo.getPrimaryKey();
        String sql = DynamicRepository.sql(driverName).delete().from(table).where().eq(primaryKey.getColumnName(), "?").builder();
        return super.update(transaction, sql, id) > 0L;
    }

    public boolean deleteByColumn(TransactionInfo transaction, String columnName, Object value) {
        DataSourceDriverName driverName = transaction.getDriverName();

        var entityInfo = getEntityInfo();
        var table = entityInfo.getTable();
        String sql = DynamicRepository.sql(driverName).delete().from(table).where().eq(columnName, "?").builder();
        return super.update(transaction, sql, value) > 0L;
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

        var sqlBuilder = DynamicRepository.sql(entityInfo.getDriverName());
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

    public int delete(TransactionInfo transaction, E condition, boolean withNull) {
        DataSourceDriverName driverName = transaction.getDriverName();

        var entityInfo = entityResolver.resolveEntity(driverName, condition);
        var table = entityInfo.getTable();
        var conditionAndValue = resolveCondition(entityInfo, withNull);

        var sqlBuilder = DynamicRepository.sql(driverName).delete().from(table);
        if (!conditionAndValue.isEmpty()) {
            sqlBuilder.where().$(conditionAndValue.conditionSql);

            List<Object> columnValues = conditionAndValue.conditionValues;
            var sql = sqlBuilder.builder();
            return super.update(transaction, sql, columnValues.toArray());
        } else {
            var sql = sqlBuilder.builder();
            return super.update(transaction, sql);
        }
    }

    public int delete(TransactionInfo transaction) {
        DataSourceDriverName driverName = transaction.getDriverName();

        var entityInfo = getEntityInfo();
        var table = entityInfo.getTable();

        var sqlBuilder = DynamicRepository.sql(driverName).delete().from(table);

        var sql = sqlBuilder.builder();
        return super.update(transaction, sql);
    }

    @SuppressWarnings("unchecked")
    public ID insert(TransactionInfo transaction, E entity, boolean withNull) {
        DataSourceDriverName driverName = transaction.getDriverName();

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

        var sql = DynamicRepository.sql(driverName).insert().$(table).leftParenthesis()
            .joinWith(",", columnNames).rightParenthesis().values(signs).builder();
        var primaryKey = entityInfo.getPrimaryKey();
        if (primaryKey != null) {
            var generatedKeys = primaryKey.getPrimaryKeyType() != null;
            return (ID) super.insert(transaction, sql, generatedKeys, primaryKey.getJavaClass(), columnValues.toArray());
        }
        return (ID) super.insert(transaction, sql, false, null, columnValues.toArray());
    }

    public int insert(TransactionInfo transaction, Collection<E> entities, boolean withNull) {
        if (entities.isEmpty()) return 0;

        DataSourceDriverName driverName = transaction.getDriverName();

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

        columnSet.forEach(e -> e.forEach(column -> {
            var value = column.getValue();
            var bool = withNull || value != null;
            if (bool) {
                columnValues.add(value);
            }
        }));

        var sqlBuilder = DynamicRepository.sql(driverName).insert().$(table)
            .leftParenthesis().joinWith(",", columnNames).rightParenthesis()
            .$(" VALUES ");

        int size = entities.size();
        String[] values = new String[size];
        for (int i = 0; i < size; i++) {
            values[i] = DynamicRepository.sql(driverName).leftParenthesis().joinWith(",", signs).rightParenthesis().builder();
        }
        sqlBuilder.joinWith(",", values);
        return super.update(transaction, sqlBuilder.builder(), columnValues.toArray());
    }

    public boolean update(TransactionInfo transaction, E entity, boolean withNull) {
        DataSourceDriverName driverName = transaction.getDriverName();

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

        var sql = DynamicRepository.sql(driverName).update(table).set(columnNames)
            .where().eq(primaryKey.getColumnName(), "?").builder();
        return super.update(transaction, sql, columnValues.toArray()) == 1;
    }

    public int update(TransactionInfo transaction, E entity, boolean withNull, String whereSql, Object... args) {
        DataSourceDriverName driverName = transaction.getDriverName();

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

        var sql = DynamicRepository.sql(driverName).update(table).set(columnNames);
        if (whereSql != null && !whereSql.isBlank()) {
            var trimWhereSql = whereSql.trim();
            if (trimWhereSql.startsWith("where") || trimWhereSql.startsWith("WHERE")) {
                sql.$(whereSql);
            } else {
                sql.where().$(whereSql);
            }
        }

        return super.update(transaction, sql.builder(), args);
    }

    @SuppressWarnings("unchecked")
    public <S extends E> S save(TransactionInfo transaction, S entity) {
        DataSourceDriverName driverName = transaction.getDriverName();

        var entityInfo = entityResolver.resolveEntity(driverName, entity);
        ColumnInfo primaryKey = entityInfo.getPrimaryKey();
        if (primaryKey.getValue() != null) {
            var bool = update(transaction, entity, true);
            if (bool) {
                return (S) selectOne(transaction, entity, false);
            } else {
                return entity;
            }
        } else {
            ID insert = insert(transaction, entity, true);
            return (S) selectById(transaction, insert);
        }
    }

    public E selectOne(TransactionInfo transaction, E condition, boolean withNull) {
        DataSourceDriverName driverName = transaction.getDriverName();

        var entityInfo = getEntityInfo();
        var entityClass = entityInfo.getJavaType();

        var table = entityInfo.getTable();
        var columns = entityInfo.getColumnInfoList();

        List<String> columnNames = new LinkedList<>();
        for (ColumnInfo column : columns) {
            columnNames.add(column.getColumnName());
        }

        var sql = DynamicRepository.sql(driverName).select(columnNames).from(table);

        var conditionAndValues = resolveCondition(driverName, condition, withNull);
        if (!conditionAndValues.isEmpty()) {
            sql.where().$(conditionAndValues.conditionSql);
            return super.queryOne(transaction, sql.builder(), entityClass, conditionAndValues.conditionValues.toArray());
        } else {
            return super.queryOne(transaction, sql.builder(), entityClass);
        }
    }

    public E selectOne(TransactionInfo transaction, String whereSql, Object... args) {
        DataSourceDriverName driverName = transaction.getDriverName();

        var entityInfo = getEntityInfo();
        var entityClass = entityInfo.getJavaType();

        var table = entityInfo.getTable();
        var columns = entityInfo.getColumnInfoList();

        List<String> columnNames = new LinkedList<>();
        for (ColumnInfo column : columns) {
            columnNames.add(column.getColumnName());
        }

        var sql = DynamicRepository.sql(driverName).select(columnNames).from(table);
        if (whereSql != null && !whereSql.isBlank()) {
            var trimWhereSql = whereSql.trim();
            if (trimWhereSql.startsWith("where") || trimWhereSql.startsWith("WHERE")) {
                sql.$(whereSql);
            } else {
                sql.where().$(whereSql);
            }
        }

        return super.queryOne(transaction, sql.builder(), entityClass, args);
    }

    protected <T> DynamicRepository select(DataSourceDriverName driverName, T entity) {
        if (entity != null) {
            var entityInfo = entityResolver.resolveEntity(driverName, entity);

            var table = entityInfo.getTable();
            var columns = entityInfo.getColumnInfoList();

            List<String> columnNames = new LinkedList<>();
            for (ColumnInfo column : columns) {
                columnNames.add(column.getColumnName());
            }

            return DynamicRepository.sql(driverName).select(columnNames).from(table);
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

        var sqlBuilder = DynamicRepository.sql(driverName).select(columnNames).from(table);
        Object[] args = null;

        if (condition != null) {
            var conditionAndValues = resolveCondition(driverName, condition, withNull);
            if (!conditionAndValues.isEmpty()) {
                sqlBuilder.where().$(conditionAndValues.conditionSql);
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

        var sqlBuilder = DynamicRepository.sql(driverName).select(columnNames).from(table);
        if (whereSql != null && !whereSql.isBlank()) {
            var trimWhereSql = whereSql.trim();
            if (trimWhereSql.startsWith("where") || trimWhereSql.startsWith("WHERE")) {
                sqlBuilder.$(whereSql);
            } else {
                sqlBuilder.where().$(whereSql);
            }
        }

        var sqlAndArgs = new SqlAndArgs<E>();
        sqlAndArgs.sqlBuilder = sqlBuilder;
        sqlAndArgs.args = args;
        sqlAndArgs.entityClass = entityClass;

        return sqlAndArgs;
    }

    public List<E> selectList(TransactionInfo transaction, E condition, boolean withNull) {
        DataSourceDriverName driverName = transaction.getDriverName();

        SqlAndArgs<E> sqlAndArgs = preSelect(driverName, condition, withNull);

        var sql = sqlAndArgs.sqlBuilder.builder();
        return super.query(transaction, sql, sqlAndArgs.entityClass, sqlAndArgs.args);
    }

    public List<E> selectList(TransactionInfo transaction, String whereSql, Object... args) {
        DataSourceDriverName driverName = transaction.getDriverName();

        SqlAndArgs<E> sqlAndArgs = preSelect(driverName, whereSql, args);

        var sql = sqlAndArgs.sqlBuilder.builder();
        return super.query(transaction, sql, sqlAndArgs.entityClass, sqlAndArgs.args);
    }

    public Page<E> selectPaged(TransactionInfo transaction, E condition, boolean withNull, PageRequest pageable) {
        DataSourceDriverName driverName = transaction.getDriverName();
        SqlAndArgs<E> sqlAndArgs = preSelect(driverName, condition, withNull);

        var sql = sqlAndArgs.sqlBuilder.limit(pageable.getOffset(), pageable.getPageSize()).builder();

        var count = count(transaction, condition, withNull);
        List<E> content = super.query(transaction, sql, sqlAndArgs.entityClass, sqlAndArgs.args);
        return Page.createPage(pageable.getCurrentPage(), pageable.getPageSize(), count, content);
    }

    public Page<E> selectPaged(TransactionInfo transaction, PageRequest pageable, String whereSql, Object... args) {
        DataSourceDriverName driverName = transaction.getDriverName();

        SqlAndArgs<E> sqlAndArgs = preSelect(driverName, whereSql, args);

        var sql = sqlAndArgs.sqlBuilder.limit(pageable.getOffset(), pageable.getPageSize()).builder();

        var count = count(transaction);
        List<E> content = super.query(transaction, sql, sqlAndArgs.entityClass, sqlAndArgs.args);
        return Page.createPage(pageable.getCurrentPage(), pageable.getPageSize(), count, content);
    }

    public Page<E> selectPaged(TransactionInfo transaction, PageRequest pageable) {
        return selectPaged(transaction, pageable, null);
    }

    public List<E> selectAll(TransactionInfo transaction) {
        DataSourceDriverName driverName = transaction.getDriverName();
        SqlAndArgs<E> sqlAndArgs = preSelect(driverName, null, false);

        var sql = sqlAndArgs.sqlBuilder.builder();
        return super.query(transaction, sql, sqlAndArgs.entityClass, sqlAndArgs.args);
    }

    public Long count(TransactionInfo transaction, E condition, boolean withNull) {
        DataSourceDriverName driverName = transaction.getDriverName();

        var entityInfo = getEntityInfo();

        var table = entityInfo.getTable();

        var sqlBuilder = DynamicRepository.sql(driverName).select().count().from(table);
        Object[] args = null;

        if (condition != null) {
            var conditionAndValues = resolveCondition(driverName, condition, withNull);
            if (!conditionAndValues.isEmpty()) {
                sqlBuilder.where().$(conditionAndValues.conditionSql);
                args = conditionAndValues.conditionValues.toArray();
            }
        }

        var sql = sqlBuilder.builder();
        return super.queryOne(transaction, sql, Long.class, args);
    }

    public Long count(TransactionInfo transaction) {
        DataSourceDriverName driverName = transaction.getDriverName();

        var entityInfo = getEntityInfo();
        var table = entityInfo.getTable();
        var sql = DynamicRepository.sql(driverName).select().count().from(table).builder();
        return super.queryOne(transaction, sql, Long.class);
    }

    public E selectById(TransactionInfo transaction, ID id) {
        DataSourceDriverName driverName = transaction.getDriverName();

        var entityClass = getEntityClass();
        var entityInfo = entityResolver.resolveEntityClass(entityClass);

        var table = entityInfo.getTable();
        var columns = entityInfo.getColumnInfoList();
        var primaryKey = entityInfo.getPrimaryKey();

        List<String> columnNames = new LinkedList<>();
        for (ColumnInfo column : columns) {
            columnNames.add(column.getColumnName());
        }

        var sql = DynamicRepository.sql(driverName).select(columnNames).from(table)
            .where().eq(primaryKey.getColumnName(), "?").builder();
        return super.queryOne(transaction, sql, entityClass, id);
    }

    public Boolean existsById(TransactionInfo transaction, ID id) {
        DataSourceDriverName driverName = transaction.getDriverName();

        var entityInfo = getEntityInfo();
        var table = entityInfo.getTable();
        var primaryKey = entityInfo.getPrimaryKey();
        var subSql = DynamicRepository.sql(driverName).select(primaryKey.getColumnName()).from(table)
            .where().eq(primaryKey.getColumnName(), "?").builder();
        var sql = DynamicRepository.sql(driverName).select().exist(subSql).builder();
        return super.queryOne(transaction, sql, Long.class, id) > 0L;
    }

    public Optional<E> selectOptionalById(TransactionInfo transaction, ID id) {
        E result = selectById(transaction, id);
        if (result == null) {
            return Optional.empty();
        }
        return Optional.of(result);
    }

    private static class SqlAndArgs<T> {
        DynamicRepository sqlBuilder;
        Object[] args;
        Class<T> entityClass;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DmlRepositoryHandler.class);
}
