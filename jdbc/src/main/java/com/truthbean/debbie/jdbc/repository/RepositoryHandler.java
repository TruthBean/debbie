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

import com.truthbean.debbie.data.transformer.TransformerFactory;
import com.truthbean.debbie.jdbc.annotation.JdbcTransient;
import com.truthbean.debbie.jdbc.annotation.SqlColumn;
import com.truthbean.debbie.jdbc.column.ColumnInfo;
import com.truthbean.debbie.jdbc.column.FStartColumnNameTransformer;
import com.truthbean.debbie.jdbc.column.JdbcColumnResolver;
import com.truthbean.debbie.jdbc.column.type.ColumnTypeHandler;
import com.truthbean.debbie.jdbc.datasource.DataSourceDriverName;
import com.truthbean.debbie.jdbc.entity.EntityResolver;
import com.truthbean.debbie.jdbc.transaction.TransactionException;
import com.truthbean.debbie.jdbc.transaction.TransactionInfo;
import com.truthbean.debbie.jdbc.util.JdbcUtils;
import com.truthbean.debbie.lang.Callback;
import com.truthbean.debbie.reflection.ReflectionHelper;
import com.truthbean.debbie.reflection.TypeHelper;
import com.truthbean.debbie.util.StringUtils;
import com.truthbean.Logger;
import com.truthbean.logger.LoggerFactory;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;

/**
 * handle repository of curd
 *
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-03-14 11:53
 */
public class RepositoryHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryHandler.class);

    private DataSourceDriverName driverName;

    public void setDriverName(DataSourceDriverName driverName) {
        this.driverName = driverName;
    }

    public DataSourceDriverName getDriverName() {
        return driverName;
    }

    public final int[] batch(final TransactionInfo transaction, final String sql, final Object[][] args) {
        loggerSqlAndParameters(sql, args);

        PreparedStatement preparedStatement = null;
        int[] rows;
        try {
            transaction.setUsing(true);
            var connection = transaction.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            int count = args == null ? 0 : args.length;
            for (int j = 0; j < count; j++) {
                for (int i = 0; i < args[j].length; i++) {
                    ColumnTypeHandler.setSqlArgValue(driverName, preparedStatement, i + 1, args[j][i]);
                }
                //jdbc自带的批量操作方法，存储sql操作集
                preparedStatement.addBatch();
            }
            //执行sql操作集
            rows = preparedStatement.executeBatch();
        } catch (SQLException ex) {
            LOGGER.error("", ex);
            throw new TransactionException(ex);
        } finally {
            transaction.setUsing(false);
            JdbcUtils.close(null, preparedStatement);
        }
        return rows;
    }

    public Object insert(Connection connection, String sql, boolean generatedKeys, Object... args) {
        return insert(connection, sql, generatedKeys, Object.class, args);
    }

    public final <K> K insert(final TransactionInfo transaction, final String sql, final boolean generatedKeys,
                              final Class<K> keyClass, final Object... args) {

        loggerSqlAndParameters(sql, args);

        K id = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            int autoGeneratedKeys;
            if (generatedKeys) {
                // 获取自动生成的键
                autoGeneratedKeys = Statement.RETURN_GENERATED_KEYS;
            } else {
                autoGeneratedKeys = Statement.NO_GENERATED_KEYS;
            }
            transaction.setUsing(true);
            var connection = transaction.getConnection();
            preparedStatement = connection.prepareStatement(sql, autoGeneratedKeys);
            if (args != null && args.length > 0) {
                for (int i = 0; i < args.length; i++) {
                    ColumnTypeHandler.setSqlArgValue(driverName, preparedStatement, i + 1, args[i]);
                }
            }
            int row = preparedStatement.executeUpdate();
            LOGGER.debug(row + " row data has been inserted");
            if (generatedKeys) {
                resultSet = preparedStatement.getGeneratedKeys();
                if (resultSet.next()) {
                    id = ColumnTypeHandler.getColumnValue(resultSet, 1, keyClass.getName());
                }
            }
        } catch (SQLException | ClassNotFoundException ex) {
            throw new TransactionException(ex);
        } finally {
            transaction.setUsing(false);
            JdbcUtils.close(resultSet, preparedStatement);
        }
        return id;
    }

    public final int update(final TransactionInfo transaction, final String sql, final Object... args) {
        loggerSqlAndParameters(sql, args);

        PreparedStatement preparedStatement = null;
        int rows = 0;
        try {
            transaction.setUsing(true);
            var connection = transaction.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            int count = args == null ? 0 : args.length;
            for (int i = 0; i < count; i++) {
                ColumnTypeHandler.setSqlArgValue(driverName, preparedStatement, i + 1, args[i]);
            }
            rows = preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.error("", ex);
            throw new TransactionException(ex);
        } finally {
            transaction.setUsing(false);
            JdbcUtils.close(null, preparedStatement);
        }
        return rows;
    }

    public final ResultSet executeQuery(final TransactionInfo transaction, final String sql, final Object... args) {
        loggerSqlAndParameters(sql, args);

        transaction.setUsing(true);
        var connection = transaction.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            if (args != null) {
                for (int e = 0; e < args.length; ++e) {
                    ColumnTypeHandler.setSqlArgValue(driverName, preparedStatement, e + 1, args[e]);
                }
            }
            return preparedStatement.executeQuery();
        } catch (SQLException e) {
            LOGGER.error("", e);
            throw new TransactionException(e);
        } finally {
            transaction.setUsing(false);
        }
    }

    public List<List<ColumnInfo>> query(final TransactionInfo transaction, String sql, Object... args) {
        ResultSet resultSet = executeQuery(transaction, sql, args);
        return JdbcColumnResolver.resolveResultSetValue(resultSet, new FStartColumnNameTransformer());
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> query(final TransactionInfo transaction, String selectSql, Class<T> clazz, Object... args) {
        List<List<ColumnInfo>> selectResult = query(transaction, selectSql, args);
        List<T> result = new ArrayList<>();
        if (!TypeHelper.isBaseType(clazz)) {
            if (clazz == Map.class) {
                for (List<ColumnInfo> columns : selectResult) {
                    Map<String, Object> columnMap = new HashMap<>();
                    for (ColumnInfo column : columns) {
                        columnMap.put(column.getColumnName(), column.getValue());
                    }
                    result.add((T) columnMap);
                }
                return result;
            }
            List<Field> declaredFields = ReflectionHelper.getDeclaredFields(clazz);
            selectResult.forEach(map -> result.add(transformer(map, declaredFields, clazz)));
        } else {
            for (List<ColumnInfo> map : selectResult) {
                if (map.size() == 1) {
                    ColumnInfo data = map.get(0);
                    if (data.getJavaClass() == clazz) {
                        result.add(clazz.cast(data.getValue()));
                    } else {
                        Class<?> type = clazz;
                        if (TypeHelper.isRawBaseType(type)) {
                            type = TypeHelper.getWrapperClass(type);
                        }
                        @SuppressWarnings("unchecked")
                        T t = (T) TransformerFactory.transform(data.getValue(), type);
                        result.add(t);
                    }
                }
            }
        }

        return result;
    }

    public List<Map<String, Object>> queryMap(TransactionInfo transaction, String selectSql, Object... args) {
        List<List<ColumnInfo>> selectResult = query(transaction, selectSql, args);
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> map;
        for (List<ColumnInfo> columnInfos : selectResult) {
            map = new HashMap<>();
            for (ColumnInfo columnInfo : columnInfos) {
                map.put(columnInfo.getColumnName(), columnInfo.getValue());
            }
            result.add(map);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public <T> T queryOne(TransactionInfo transaction, String selectSql, Class<T> clazz, Object... args) {
        List<List<ColumnInfo>> selectResult = query(transaction, selectSql, args);
        if (selectResult.isEmpty()) {
            return null;
        }
        if (selectResult.size() <= 1) {
            List<ColumnInfo> row = selectResult.get(0);
            if (clazz == Map.class) {
                Map<String, Object> map = new HashMap<>();
                for (ColumnInfo columnInfo : row) {
                    map.put(columnInfo.getColumnName(), columnInfo.getValue());
                }
                return (T) map;
            }
            if (row.size() == 1) {
                ColumnInfo data = row.get(0);
                return transform(data, clazz, arg -> {
                    List<Field> declaredFields = ReflectionHelper.getDeclaredFields(clazz);
                    return transformer(row, declaredFields, clazz);
                });
            } else {
                List<Field> declaredFields = ReflectionHelper.getDeclaredFields(clazz);
                return transformer(row, declaredFields, clazz);
            }
        } else {
            throw new MoreRowException("Expect one row, but it has" + selectResult.size() + "rows.");
        }
    }

    private void loggerSqlAndParameters(String sql, Object[][] args) {
        LOGGER.debug("Preparing >>> " + sql);
        LOGGER.debug("Parameters >>> " + Arrays.deepToString(args));
    }

    private void loggerSqlAndParameters(String sql, Object[] args) {
        LOGGER.debug("Preparing >>> " + sql);
        LOGGER.debug("Parameters >>> " + StringUtils.getParameterValueString(args));
    }

    @SuppressWarnings("unchecked")
    private <T> T transform(ColumnInfo data, Class<T> clazz, Callback<T> others) {
        if (TypeHelper.isBaseType(clazz)) {
            if (data.getJavaClass() == clazz) {
                return clazz.cast(data.getValue());
            } else {
                Class<?> type = clazz;
                if (TypeHelper.isRawBaseType(type)) {
                    type = TypeHelper.getWrapperClass(type);
                }
                return (T) TransformerFactory.transform(data.getValue(), type);
            }
        } else if (TypeHelper.isTimeType(clazz)) {
            Object value = data.getValue();
            if (value instanceof Timestamp) {
                Timestamp timestamp = (Timestamp) value;
                return TransformerFactory.transform(timestamp, clazz);
            }
        } else {
            return others.call();
        }
        return null;
    }

    private <T> T transformer(List<ColumnInfo> map, List<Field> declaredFields, Class<T> clazz) {
        T instance = ReflectionHelper.newInstance(clazz);
        if (instance == null) {
            throw new NullPointerException("Class(" + clazz + ") reflect to new instance null");
        }
        // todo 自定义map to T
        SqlColumn column;
        for (var field : declaredFields) {
            if (field.getAnnotation(JdbcTransient.class) != null)
                continue;

            column = field.getAnnotation(SqlColumn.class);
            var columnName = EntityResolver.getColumnName(column, field.getName());
            for (var entry : map) {
                if (columnName.equals(entry.getColumnName())) {
                    Class<?> type = field.getType();
                    Class<?> javaClass = entry.getJavaClass();
                    Object value = entry.getValue();
                    // todo check
                    if (javaClass != type) {
                        value = TransformerFactory.transform(entry.getValue(), type);
                    }
                    ReflectionHelper.invokeSetMethod(instance, field, value);
                    break;
                }
            }
        }
        return instance;
    }
}
