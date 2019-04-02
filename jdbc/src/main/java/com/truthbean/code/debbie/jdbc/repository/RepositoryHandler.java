package com.truthbean.code.debbie.jdbc.repository;

import com.truthbean.code.debbie.core.reflection.ClassNotMatchedException;
import com.truthbean.code.debbie.core.reflection.ReflectionHelper;
import com.truthbean.code.debbie.core.reflection.TypeHelper;
import com.truthbean.code.debbie.jdbc.JdbcTypeConstants;
import com.truthbean.code.debbie.jdbc.annotation.SqlColumn;
import com.truthbean.code.debbie.jdbc.column.ColumnData;
import com.truthbean.code.debbie.jdbc.column.ColumnDataHelper;
import com.truthbean.code.debbie.jdbc.column.FStartColumnNameTransformer;
import com.truthbean.code.debbie.jdbc.datasource.DataSourceFactory;
import com.truthbean.code.debbie.jdbc.util.JdbcUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * handle repository of curd
 *
 * TODO page
 *
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-03-14 11:53
 */
public class RepositoryHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryHandler.class);

    private Connection connection;

    public RepositoryHandler(DataSourceFactory dataSourceFactory) {
        connection = dataSourceFactory.getConnection();
    }

    public int[] batch(String sql, Object[][] args) {
        PreparedStatement preparedStatement = null;
        int[] rows = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
            int count = args == null ? 0 : args.length;
            for (int j = 0; j < count; j++) {
                for (int i = 0; i < args[j].length; i++) {
                    preparedStatement.setObject(i + 1, args[j][i]);
                }
                //jdbc自带的批量操作方法，存储sql操作集
                preparedStatement.addBatch();
            }
            //执行sql操作集
            rows = preparedStatement.executeBatch();
        } catch (SQLException ex) {
            LOGGER.error(null, ex);
        } finally {
            JdbcUtils.close(null, preparedStatement);
        }
        return rows;
    }

    public long insert(String sql, Object... args) {
        long id = 0;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            //Statement.RETURN_GENERATED_KEYS 获取自动生成的键
            preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            if (args != null) {
                for (int i = 0; i < args.length; i++) {
                    setSqlArgValue(preparedStatement, i + 1, args[i]);
                }
            }
            preparedStatement.executeUpdate();
            resultSet = preparedStatement.getGeneratedKeys();
            //autoGeneratedKeys - 指示是否应该返回自动生成的键的标志
            //Statement.RETURN_GENERATED_KEYS  Statement.NO_GENERATED_KEYS 之一
            if (resultSet.next()) {
                id = resultSet.getLong(1);
            }
        } catch (SQLException ex) {
            LOGGER.error(null, ex);
        } finally {
            JdbcUtils.close(resultSet, preparedStatement);
        }
        return id;
    }

    public int update(String sql, Object... args) {
        PreparedStatement preparedStatement = null;
        int rows = 0;
        try {
            preparedStatement = connection.prepareStatement(sql);
            int count = args == null ? 0 : args.length;
            for (int i = 0; i < count; i++) {
                preparedStatement.setObject(i + 1, args[i]);
            }
            rows = preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.error(null, ex);
        } finally {
            JdbcUtils.close(null, preparedStatement);
        }
        return rows;
    }

    public ResultSet preSelect(String sql, Object... args) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            if (args != null) {
                for (int e = 0; e < args.length; ++e) {
                    setSqlArgValue(preparedStatement, e + 1, args[e]);
                }
            }
            return preparedStatement.executeQuery();
        } catch (SQLException e) {
            LOGGER.error(null, e);
            throw new RuntimeException(e);
        }
    }

    private void setSqlArgValue(PreparedStatement preparedStatement, int index, Object arg) throws SQLException {
        switch (arg.getClass().getName()) {
            case JdbcTypeConstants.ARRAY:
                preparedStatement.setArray(index, (Array) arg);
                break;

            case JdbcTypeConstants.INPUT_STREAM:
                preparedStatement.setBinaryStream(index, (InputStream) arg);
                break;

            case JdbcTypeConstants.BIG_DECIMAL:
                preparedStatement.setBigDecimal(index, (BigDecimal) arg);
                break;

            case JdbcTypeConstants.BLOB:
                preparedStatement.setBlob(index, (InputStream) arg);
                break;

            case JdbcTypeConstants.BOOLEAN:
                preparedStatement.setBoolean(index, (Boolean) arg);
                break;

            case JdbcTypeConstants.BYTE:
                preparedStatement.setByte(index, (Byte) arg);
                break;

            case JdbcTypeConstants.BYTE_ARRAY:
                preparedStatement.setBytes(index, (byte[]) arg);
                break;

            case JdbcTypeConstants.READER:
                preparedStatement.setCharacterStream(index, (Reader) arg);
                // getNCharacterStream
                break;

            case JdbcTypeConstants.CLOB:
                preparedStatement.setClob(index, (Clob) arg);
                break;

            case JdbcTypeConstants.DATE:
                preparedStatement.setDate(index, (Date) arg);
                break;

            case JdbcTypeConstants.DOUBLE:
                preparedStatement.setDouble(index, (Double) arg);
                break;

            case JdbcTypeConstants.FLOAT:
                preparedStatement.setFloat(index, (Float) arg);
                break;

            case JdbcTypeConstants.INTEGER:
                preparedStatement.setInt(index, (Integer) arg);
                break;

            case JdbcTypeConstants.LONG:
                preparedStatement.setLong(index, (Long) arg);
                break;

            case JdbcTypeConstants.NCLOB:
                preparedStatement.setNClob(index, (NClob) arg);
                break;

            case JdbcTypeConstants.STRING:
                preparedStatement.setString(index, (String) arg);
                // resultSet.getNString(index)
                break;

            case JdbcTypeConstants.REF:
                preparedStatement.setRef(index, (Ref) arg);
                break;

            case JdbcTypeConstants.ROW_ID:
                preparedStatement.setRowId(index, (RowId) arg);
                break;

            case JdbcTypeConstants.SHORT:
                preparedStatement.setShort(index, (Short) arg);
                break;

            case JdbcTypeConstants.SQLXML:
                preparedStatement.setSQLXML(index, (SQLXML) arg);
                break;

            case JdbcTypeConstants.TIME:
                preparedStatement.setTime(index, (Time) arg);
                break;

            case JdbcTypeConstants.TIMESTAMP:
                preparedStatement.setTimestamp(index, (Timestamp) arg);
                break;

            case JdbcTypeConstants.URL:
                preparedStatement.setURL(index, (URL) arg);
                break;

            default:
                preparedStatement.setObject(index, arg);
        }
    }

    public List<List<ColumnData>> select(String sql, Object... args) {
        ResultSet resultSet = preSelect(sql, args);
        return ColumnDataHelper.getTableColumnData(resultSet, new FStartColumnNameTransformer());
    }

    public <T> List<T> select(String selectSql, Class<T> clazz, Object... args) {
        List<List<ColumnData>> selectResult = select(selectSql, args);
        List<T> result = new ArrayList<>();
        T obj;
        if (!TypeHelper.isBaseType(clazz)) {
            List<Field> declaredFields = ReflectionHelper.getDeclaredFields(clazz);
            for (var map : selectResult) {
                obj = transformer(map, declaredFields, clazz);
                result.add(obj);
            }
        } else {
            for (List<ColumnData> map : selectResult) {
                if (map.size() == 1) {
                    ColumnData data = map.get(0);
                    if (data.getJavaClass() == clazz) {
                        result.add((T) data.getValue());
                    }
                }
            }
        }

        return result;
    }

    public <T> T selectOne(String selectSql, Class<T> clazz, Object... args) {
        List<List<ColumnData>> selectResult = select(selectSql, args);
        T result = null;
        if (selectResult.isEmpty()) {
            return null;
        }
        if (selectResult.size() <= 1) {
            if (!TypeHelper.isBaseType(clazz)) {
                List<Field> declaredFields = ReflectionHelper.getDeclaredFields(clazz);
                result = transformer(selectResult.get(0), declaredFields, clazz);
            } else {
                List<ColumnData> row = selectResult.get(0);
                if (row.size() == 1) {
                    ColumnData data = row.get(0);
                    if (data.getJavaClass() == clazz) {
                        result = (T) data.getValue();
                    } else {
                        throw new ClassNotMatchedException(data.getJavaClass(), clazz);
                    }
                }
            }
        }
        return result;
    }

    private <T> T transformer(List<ColumnData> map, List<Field> declaredFields, Class<T> clazz) {
        T instance = null;
        try {
            instance = clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException
                | InvocationTargetException e) {
            LOGGER.error("getDeclaredConstructor and newInstance error when transformer", e);
        }
        SqlColumn column;
        for (var field : declaredFields) {
            column = field.getAnnotation(SqlColumn.class);
            var columnName = field.getName();
            if (column != null) {
                columnName = column.name();
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