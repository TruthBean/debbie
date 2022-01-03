/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.jdbc.repository;

import com.truthbean.debbie.jdbc.column.ColumnInfo;
import com.truthbean.debbie.jdbc.datasource.DataSourceDriverName;
import com.truthbean.debbie.jdbc.entity.EntityInfo;
import com.truthbean.debbie.jdbc.transaction.TransactionInfo;
import com.truthbean.Logger;
import com.truthbean.LoggerFactory;

import java.util.*;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/4/3 23:23.
 */
public class DmlRepositoryHandler extends RepositoryHandler {

    private static volatile DmlRepositoryHandler repositoryHandler;

    protected DmlRepositoryHandler() {
    }

    public static DmlRepositoryHandler getInstance() {
        if (repositoryHandler != null) {
            synchronized (DmlRepositoryHandler.class) {
                if (repositoryHandler != null) {
                    repositoryHandler = new DmlRepositoryHandler();
                }
            }
        }
        return repositoryHandler;
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

    public <Id> int deleteByIdIn(Logger logger, TransactionInfo transaction, String table, String idColumn, List<Id> ids) {
        return deleteByColumnIn(logger, transaction, table, idColumn, ids);
    }

    public <Entity, Id> int deleteByIdIn(Logger logger, TransactionInfo transaction, EntityInfo<Entity> entityInfo, List<Id> ids) {
        var primaryKey = entityInfo.getPrimaryKey();
        return deleteByColumnIn(logger, transaction, entityInfo, primaryKey.getColumn(), ids);
    }

    public <Entity, C> int deleteByColumnIn(Logger logger, TransactionInfo transaction, EntityInfo<Entity> entityInfo,
                                            String columnName, List<C> values) {
        if (values == null || values.isEmpty()) return 0;

        DataSourceDriverName driverName = transaction.getDriverName();
        int length = values.size();
        if (length == 1) return deleteByColumn(logger, transaction, entityInfo, columnName, values.get(0)) ? 1 : 0;

        var table = entityInfo.getTable();
        String sql = DynamicRepository.sql(driverName).delete().from(table).where().in(columnName, length).builder();
        return super.update(logger, transaction, sql, values.toArray());
    }

    public <C> int deleteByColumnIn(Logger logger, TransactionInfo transaction, String table, String columnName, List<C> values) {
        if (values == null || values.isEmpty()) return 0;

        DataSourceDriverName driverName = transaction.getDriverName();
        int length = values.size();
        if (length == 1) return deleteByColumn(logger, transaction, table, columnName, values.get(0)) ? 1 : 0;

        String sql = DynamicRepository.sql(driverName).delete().from(table).where().in(columnName, length).builder();
        return super.update(logger, transaction, sql, values.toArray());
    }

    public <Entity, Id> boolean deleteById(Logger logger, TransactionInfo transaction, EntityInfo<Entity> entityInfo, Id id) {
        DataSourceDriverName driverName = transaction.getDriverName();

        var table = entityInfo.getTable();
        var primaryKey = entityInfo.getPrimaryKey();
        String sql = DynamicRepository.sql(driverName).delete().from(table).where().eq(primaryKey.getColumn(), "?").builder();
        return super.update(logger, transaction, sql, id) > 0L;
    }

    public <Entity> boolean deleteByColumn(Logger logger, TransactionInfo transaction, EntityInfo<Entity> entityInfo
            , String columnName, Object value) {
        DataSourceDriverName driverName = transaction.getDriverName();

        var table = entityInfo.getTable();
        String sql = DynamicRepository.sql(driverName).delete().from(table).where().eq(columnName, "?").builder();
        return super.update(logger, transaction, sql, value) > 0L;
    }

    public <T> boolean deleteByColumn(Logger logger, TransactionInfo transaction, String table, String columnName, T value) {
        DataSourceDriverName driverName = transaction.getDriverName();

        String sql = DynamicRepository.sql(driverName).delete().from(table).where().eq(columnName, "?").builder();
        return super.update(logger, transaction, sql, value) > 0L;
    }

    public <T> ConditionAndValue resolveCondition(EntityInfo<T> entityInfo, boolean withNull) {
        List<ColumnInfo> columns = entityInfo.getColumnInfoList();

        var sqlBuilder = DynamicRepository.sql(entityInfo.getDriverName());
        List<Object> columnValues = new LinkedList<>();

        columns.forEach(column -> {
            var value = column.getValue();
            var bool = withNull || value != null;
            if (bool) {
                columnValues.add(value);
                sqlBuilder.and(column.getColumn() + " = ?");
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

    public <E> int delete(Logger logger, TransactionInfo transaction, EntityInfo<E> entityInfo, boolean withNull) {
        DataSourceDriverName driverName = transaction.getDriverName();

        var table = entityInfo.getTable();
        var conditionAndValue = resolveCondition(entityInfo, withNull);

        var sqlBuilder = DynamicRepository.sql(driverName).delete().from(table);
        if (!conditionAndValue.isEmpty()) {
            sqlBuilder.where().$(conditionAndValue.conditionSql);

            List<Object> columnValues = conditionAndValue.conditionValues;
            var sql = sqlBuilder.builder();
            return super.update(logger, transaction, sql, columnValues.toArray());
        } else {
            var sql = sqlBuilder.builder();
            return super.update(logger, transaction, sql);
        }
    }

    public int deleteAll(Logger logger, TransactionInfo transaction, String table) {
        DataSourceDriverName driverName = transaction.getDriverName();

        var sqlBuilder = DynamicRepository.sql(driverName).delete().from(table);

        var sql = sqlBuilder.builder();
        return super.update(logger, transaction, sql);
    }

    @SuppressWarnings("unchecked")
    public <E, ID> ID insert(Logger logger, TransactionInfo transaction, EntityInfo<E> entityInfo, boolean withNull) {
        DataSourceDriverName driverName = transaction.getDriverName();

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
                columnNames.add(column.getColumn());
                signs.add("?");
            }
        });

        var sql = DynamicRepository.sql(driverName).insert().$(table).leftParenthesis()
            .joinWith(",", columnNames).rightParenthesis().values(signs).builder();
        var primaryKey = entityInfo.getPrimaryKey();
        if (primaryKey != null) {
            var generatedKeys = primaryKey.getPrimaryKeyType() != null;
            return (ID) super.insert(logger, transaction, sql, generatedKeys, primaryKey.getJavaClass(), columnValues.toArray());
        }
        return super.insert(logger, transaction, sql, false, null, columnValues.toArray());
    }

    public <E> int insertMany(Logger logger, TransactionInfo transaction, Collection<EntityInfo<E>> entityInfos, boolean withNull) {
        if (entityInfos.isEmpty()) {
            return 0;
        }

        DataSourceDriverName driverName = transaction.getDriverName();

        Iterator<EntityInfo<E>> iterator = entityInfos.iterator();
        EntityInfo<E> entityInfo = iterator.next();

        var table = entityInfo.getTable();
        var columns = entityInfo.getColumnInfoList();

        Set<List<ColumnInfo>> columnSet = new LinkedHashSet<>();
        columnSet.add(entityInfo.getColumnInfoList());
        columnSet.add(columns);
        while (iterator.hasNext()) {
            entityInfo = iterator.next();
            columnSet.add(entityInfo.getColumnInfoList());
        }

        List<String> columnNames = new LinkedList<>();
        List<String> signs = new LinkedList<>();

        columns.forEach(column -> {
            var value = column.getValue();
            var bool = withNull || value != null;
            if (bool) {
                columnNames.add(column.getColumn());
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

        int size = entityInfos.size();
        String[] values = new String[size];
        for (int i = 0; i < size; i++) {
            values[i] = DynamicRepository.sql(driverName).leftParenthesis().joinWith(",", signs).rightParenthesis().builder();
        }
        sqlBuilder.joinWith(",", values);
        return super.update(logger, transaction, sqlBuilder.builder(), columnValues.toArray());
    }

    public <E> boolean update(Logger logger, TransactionInfo transaction, EntityInfo<E> entityInfo, boolean withNull) {
        DataSourceDriverName driverName = transaction.getDriverName();

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
                columnNames.add(column.getColumn());
            }
        }

        columnValues.add(primaryKey.getValue());

        var sql = DynamicRepository.sql(driverName).update(table).set(columnNames)
            .where().eq(primaryKey.getColumn(), "?").builder();
        return super.update(logger, transaction, sql, columnValues.toArray()) == 1;
    }

    public <E> int update(Logger logger, TransactionInfo transaction, EntityInfo<E> entityInfo, boolean withNull, String whereSql, Object... args) {
        DataSourceDriverName driverName = transaction.getDriverName();

        var table = entityInfo.getTable();
        var columns = entityInfo.getColumnInfoList();

        List<String> columnNames = new LinkedList<>();
        for (ColumnInfo column : columns) {
            var value = column.getValue();
            var bool = withNull || value != null;
            if (!column.isPrimaryKey() && bool) {
                columnNames.add(column.getColumn());
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

        return super.update(logger, transaction, sql.builder(), args);
    }

    /*@SuppressWarnings("unchecked")
    public <S extends E, E, ID> S save(TransactionInfo transaction, EntityInfo<E> entityInfo) {
        ColumnInfo primaryKey = entityInfo.getPrimaryKey();
        if (primaryKey.getValue() != null) {
            var bool = update(transaction, entityInfo, true);
            if (bool) {
                return (S) selectOne(transaction, entityInfo, false);
            } else {
                return null;
            }
        } else {
            ID insert = insert(transaction, entityInfo, true);
            entityInfo.getPrimaryKey().setValue(insert);
            return (S) selectById(transaction, entityInfo, insert);
        }
    }*/

    public <E> List<ColumnInfo> selectOne(Logger logger, TransactionInfo transaction, EntityInfo<E> entityInfo, boolean withNull) {
        DataSourceDriverName driverName = transaction.getDriverName();

        var table = entityInfo.getTable();
        var columns = entityInfo.getColumnInfoList();

        List<String> columnNames = new LinkedList<>();
        for (ColumnInfo column : columns) {
            columnNames.add(column.getColumn());
        }

        var sql = DynamicRepository.sql(driverName).select(columnNames).from(table);

        var conditionAndValues = resolveCondition(entityInfo, withNull);
        if (!conditionAndValues.isEmpty()) {
            sql.where().$(conditionAndValues.conditionSql);
            return super.queryOne(logger, transaction, sql.builder(), conditionAndValues.conditionValues.toArray());
        } else {
            return super.queryOne(logger, transaction, sql.builder());
        }
    }

    public <E> List<ColumnInfo> selectOne(Logger logger, TransactionInfo transaction, EntityInfo<E> entityInfo, String whereSql, Object... args) {
        DataSourceDriverName driverName = transaction.getDriverName();

        var table = entityInfo.getTable();
        var columns = entityInfo.getColumnInfoList();

        List<String> columnNames = new LinkedList<>();
        for (ColumnInfo column : columns) {
            columnNames.add(column.getColumn());
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

        return super.queryOne(logger, transaction, sql.builder(), args);
    }

    protected <E> DynamicRepository select(EntityInfo<E> entityInfo) {
        if (entityInfo != null) {

            var table = entityInfo.getTable();
            var columns = entityInfo.getColumnInfoList();

            List<String> columnNames = new LinkedList<>();
            for (ColumnInfo column : columns) {
                columnNames.add(column.getColumn());
            }

            return DynamicRepository.sql(entityInfo.getDriverName()).select(columnNames).from(table);
        }
        return null;
    }

    protected <E> SqlAndArgs<E> preSelect(DataSourceDriverName driverName, EntityInfo<E> entityInfo,
                                        E condition, boolean withNull) {
        var entityClass = entityInfo.getJavaType();

        var table = entityInfo.getTable();
        var columns = entityInfo.getColumnInfoList();

        List<String> columnNames = new LinkedList<>();
        for (ColumnInfo column : columns) {
            columnNames.add(column.getColumn());
        }

        var sqlBuilder = DynamicRepository.sql(driverName).select(columnNames).from(table);
        Object[] args = null;

        if (condition != null) {
            var conditionAndValues = resolveCondition(entityInfo, withNull);
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

    protected <E> SqlAndArgs<E> preSelect(DataSourceDriverName driverName, EntityInfo<E> entityInfo,
                                        String whereSql, Object... args) {
        var entityClass = entityInfo.getJavaType();

        var table = entityInfo.getTable();
        var columns = entityInfo.getColumnInfoList();

        List<String> columnNames = new LinkedList<>();
        for (ColumnInfo column : columns) {
            columnNames.add(column.getColumn());
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

    public <E> List<List<ColumnInfo>> selectList(Logger logger, TransactionInfo transaction, EntityInfo<E> entityInfo, E condition, boolean withNull) {
        DataSourceDriverName driverName = transaction.getDriverName();

        SqlAndArgs<E> sqlAndArgs = preSelect(driverName, entityInfo, condition, withNull);

        var sql = sqlAndArgs.sqlBuilder.builder();
        return super.query(logger, transaction, sql, sqlAndArgs.args);
    }

    public <E> List<List<ColumnInfo>> selectList(Logger logger, TransactionInfo transaction, EntityInfo<E> entityInfo, String whereSql, Object... args) {
        DataSourceDriverName driverName = transaction.getDriverName();

        SqlAndArgs<E> sqlAndArgs = preSelect(driverName, entityInfo, whereSql, args);

        var sql = sqlAndArgs.sqlBuilder.builder();
        return super.query(logger, transaction, sql, sqlAndArgs.args);
    }

    public <E> List<List<ColumnInfo>> selectAll(Logger logger, TransactionInfo transaction, EntityInfo<E> entityInfo) {
        DataSourceDriverName driverName = transaction.getDriverName();
        SqlAndArgs<E> sqlAndArgs = preSelect(driverName, entityInfo, null, false);

        var sql = sqlAndArgs.sqlBuilder.builder();
        return super.query(logger, transaction, sql, sqlAndArgs.args);
    }

    public <E> Long count(Logger logger, TransactionInfo transaction, EntityInfo<E> entityInfo, E condition, boolean withNull) {
        DataSourceDriverName driverName = transaction.getDriverName();

        var table = entityInfo.getTable();

        var sqlBuilder = DynamicRepository.sql(driverName).select().count().from(table);
        Object[] args = null;

        if (condition != null) {
            var conditionAndValues = resolveCondition(entityInfo, withNull);
            if (!conditionAndValues.isEmpty()) {
                sqlBuilder.where().$(conditionAndValues.conditionSql);
                args = conditionAndValues.conditionValues.toArray();
            }
        }

        var sql = sqlBuilder.builder();
        ColumnInfo list = super.querySingleOne(logger, transaction, sql, args);
        Object value = list.getValue();
        if (value instanceof Long) {
            return (Long) value;
        } else if (value instanceof String) {
            return Long.valueOf((String) value);
        }
        return 0L;
    }

    public <E> Long count(Logger logger, TransactionInfo transaction, EntityInfo<E> entityInfo) {
        DataSourceDriverName driverName = transaction.getDriverName();

        var table = entityInfo.getTable();
        var sql = DynamicRepository.sql(driverName).select().count().from(table).builder();
        ColumnInfo list = super.querySingleOne(logger, transaction, sql);
        Object value = list.getValue();
        if (value instanceof Long) {
            return (Long) value;
        } else if (value instanceof String) {
            return Long.valueOf((String) value);
        }
        return 0L;
    }

    public Long countAll(Logger logger, TransactionInfo transaction, String table) {
        DataSourceDriverName driverName = transaction.getDriverName();

        var sql = DynamicRepository.sql(driverName).select().count().from(table).builder();
        ColumnInfo list = super.querySingleOne(logger, transaction, sql);
        Object value = list.getValue();
        if (value instanceof Long) {
            return (Long) value;
        } else if (value instanceof String) {
            return Long.valueOf((String) value);
        } else if (value instanceof Integer) {
            return ((Integer) value).longValue();
        }
        return 0L;
    }

    public <E, ID> List<ColumnInfo> selectById(Logger logger, TransactionInfo transaction, EntityInfo<E> entityInfo, ID id) {
        DataSourceDriverName driverName = transaction.getDriverName();

        var table = entityInfo.getTable();
        var columns = entityInfo.getColumnInfoList();
        var primaryKey = entityInfo.getPrimaryKey();

        List<String> columnNames = new LinkedList<>();
        for (ColumnInfo column : columns) {
            columnNames.add(column.getColumn());
        }

        var sql = DynamicRepository.sql(driverName).select(columnNames).from(table)
            .where().eq(primaryKey.getColumn(), "?").builder();
        return super.queryOne(logger, transaction, sql, id);
    }

    public <E, ID> List<List<ColumnInfo>> selectByIdIn(Logger logger, TransactionInfo transaction, EntityInfo<E> entityInfo, Collection<ID> id) {
        DataSourceDriverName driverName = transaction.getDriverName();

        var table = entityInfo.getTable();
        var columns = entityInfo.getColumnInfoList();
        var primaryKey = entityInfo.getPrimaryKey();

        List<String> columnNames = new LinkedList<>();
        for (ColumnInfo column : columns) {
            columnNames.add(column.getColumn());
        }

        var sql = DynamicRepository.sql(driverName).select(columnNames).from(table)
                .where().in(primaryKey.getColumn(), id.size()).builder();
        return super.query(logger, transaction, sql, id.toArray());
    }

    public <Entity, Id> Boolean existsById(Logger logger, TransactionInfo transaction, EntityInfo<Entity> entityInfo, Id id) {
        var table = entityInfo.getTable();
        var primaryKey = entityInfo.getPrimaryKey();

        return existsById(logger, transaction, table, primaryKey.getColumn(), id);
    }

    public <Id> Boolean existsById(Logger logger, TransactionInfo transaction, String table, String idColumn, Id id) {
        DataSourceDriverName driverName = transaction.getDriverName();

        var subSql = DynamicRepository.sql(driverName).select(idColumn).from(table)
                .where().eq(idColumn, "?").builder();
        var sql = DynamicRepository.sql(driverName).select().exist(subSql).builder();
        ColumnInfo info = super.querySingleOne(logger, transaction, sql, id);
        Object value = info.getValue();
        if (value instanceof Boolean) {
            return (Boolean) value;
        } else if (value instanceof Long) {
            return (Long) value > 0L;
        } else if (value instanceof Integer) {
            return (Integer) value > 0;
        }
        return null;
    }

    protected static class SqlAndArgs<T> {
        DynamicRepository sqlBuilder;
        Object[] args;
        Class<T> entityClass;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DmlRepositoryHandler.class);
}
