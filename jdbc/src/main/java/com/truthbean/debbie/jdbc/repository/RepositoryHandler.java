package com.truthbean.debbie.jdbc.repository;

import com.truthbean.debbie.core.data.transformer.DataTransformerFactory;
import com.truthbean.debbie.core.reflection.ClassNotMatchedException;
import com.truthbean.debbie.core.reflection.ReflectionHelper;
import com.truthbean.debbie.core.reflection.TypeHelper;
import com.truthbean.debbie.jdbc.annotation.SqlColumn;
import com.truthbean.debbie.jdbc.column.ColumnInfo;
import com.truthbean.debbie.jdbc.column.FStartColumnNameTransformer;
import com.truthbean.debbie.jdbc.column.JdbcColumnResolver;
import com.truthbean.debbie.jdbc.column.type.ColumnTypeHandler;
import com.truthbean.debbie.jdbc.datasource.DataSourceFactory;
import com.truthbean.debbie.jdbc.transaction.TransactionException;
import com.truthbean.debbie.jdbc.util.JdbcUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * handle repository of curd
 *
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-03-14 11:53
 */
public class RepositoryHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryHandler.class);

    public int[] batch(Connection connection, String sql, Object[][] args) throws TransactionException {
        PreparedStatement preparedStatement = null;
        int[] rows;
        try {
            preparedStatement = connection.prepareStatement(sql);
            int count = args == null ? 0 : args.length;
            for (int j = 0; j < count; j++) {
                for (int i = 0; i < args[j].length; i++) {
                    ColumnTypeHandler.setSqlArgValue(preparedStatement, i + 1, args[j][i]);
                }
                //jdbc自带的批量操作方法，存储sql操作集
                preparedStatement.addBatch();
            }
            //执行sql操作集
            rows = preparedStatement.executeBatch();
        } catch (SQLException ex) {
            LOGGER.error(null, ex);
            throw new TransactionException(ex);
        } finally {
            JdbcUtils.close(null, preparedStatement);
        }
        return rows;
    }

    public Object insert(Connection connection, String sql, boolean generatedKeys, Object... args)
            throws TransactionException {
        return insert(connection, sql, generatedKeys, Object.class, args);
    }

    public <K> K insert(Connection connection, String sql, boolean generatedKeys, Class<K> keyClass, Object... args)
            throws TransactionException {
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
            preparedStatement = connection.prepareStatement(sql, autoGeneratedKeys);
            if (args != null && args.length > 0) {
                for (int i = 0; i < args.length; i++) {
                    ColumnTypeHandler.setSqlArgValue(preparedStatement, i + 1, args[i]);
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
        } catch (SQLException ex) {
            throw new TransactionException(ex);
        } finally {
            JdbcUtils.close(resultSet, preparedStatement);
        }
        return id;
    }

    public int update(Connection connection, String sql, Object... args) throws TransactionException {
        PreparedStatement preparedStatement = null;
        int rows = 0;
        try {
            preparedStatement = connection.prepareStatement(sql);
            int count = args == null ? 0 : args.length;
            for (int i = 0; i < count; i++) {
                ColumnTypeHandler.setSqlArgValue(preparedStatement, i + 1, args[i]);
            }
            rows = preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.error("", ex);
            throw new TransactionException(ex);
        } finally {
            JdbcUtils.close(null, preparedStatement);
        }
        return rows;
    }

    public ResultSet executeQuery(Connection connection, String sql, Object... args) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            if (args != null) {
                for (int e = 0; e < args.length; ++e) {
                    ColumnTypeHandler.setSqlArgValue(preparedStatement, e + 1, args[e]);
                }
            }
            return preparedStatement.executeQuery();
        } catch (SQLException e) {
            LOGGER.error("", e);
            throw new RuntimeException(e);
        }
    }

    public List<List<ColumnInfo>> query(Connection connection, String sql, Object... args) {
        ResultSet resultSet = executeQuery(connection, sql, args);
        return JdbcColumnResolver.resolveResultSetValue(resultSet, new FStartColumnNameTransformer());
    }

    public <T> List<T> query(Connection connection, String selectSql, Class<T> clazz, Object... args) {
        List<List<ColumnInfo>> selectResult = query(connection, selectSql, args);
        List<T> result = new ArrayList<>();
        if (!TypeHelper.isBaseType(clazz)) {
            List<Field> declaredFields = ReflectionHelper.getDeclaredFields(clazz);
            selectResult.forEach(map -> result.add(transformer(map, declaredFields, clazz)));
        } else {
            for (List<ColumnInfo> map : selectResult) {
                if (map.size() == 1) {
                    ColumnInfo data = map.get(0);
                    if (data.getJavaClass() == clazz) {
                        result.add(clazz.cast(data.getValue()));
                    }
                }
            }
        }

        return result;
    }

    public <T> T queryOne(Connection connection, String selectSql, Class<T> clazz, Object... args) {
        List<List<ColumnInfo>> selectResult = query(connection, selectSql, args);
        T result = null;
        if (selectResult.isEmpty()) {
            return null;
        }
        if (selectResult.size() <= 1) {
            if (!TypeHelper.isBaseType(clazz)) {
                List<Field> declaredFields = ReflectionHelper.getDeclaredFields(clazz);
                result = transformer(selectResult.get(0), declaredFields, clazz);
            } else {
                List<ColumnInfo> row = selectResult.get(0);
                if (row.size() == 1) {
                    ColumnInfo data = row.get(0);
                    if (data.getJavaClass() == clazz) {
                        result = clazz.cast(data.getValue());
                    } else {
                        Class type = clazz;
                        if (TypeHelper.isRawBaseType(type)) {
                            type = TypeHelper.getWrapperClass(type);
                        }
                        return (T) DataTransformerFactory.transform(data.getValue(), type);
                        // throw new ClassNotMatchedException(data.getJavaClass(), clazz);
                    }
                }
            }
        }
        return result;
    }

    private <T> T transformer(List<ColumnInfo> map, List<Field> declaredFields, Class<T> clazz) {
        T instance = ReflectionHelper.newInstance(clazz);
        SqlColumn column;
        for (var field : declaredFields) {
            column = field.getAnnotation(SqlColumn.class);
            var columnName = "";
            if (column != null) {
                columnName = column.name();
            }
            if (columnName.isBlank()) {
                columnName = field.getName();
            }
            for (var entry : map) {
                if (columnName.equals(entry.getColumnName())) {
                    assert instance != null;
                    ReflectionHelper.invokeSetMethod(instance, field, entry.getValue());
                    break;
                }
            }
        }
        return instance;
    }
}
