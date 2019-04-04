package com.truthbean.code.debbie.jdbc.repository;

import com.truthbean.code.debbie.core.bean.BeanInitializationHandler;
import com.truthbean.code.debbie.core.reflection.ClassInfo;
import com.truthbean.code.debbie.core.reflection.ClassNotMatchedException;
import com.truthbean.code.debbie.core.reflection.ReflectionHelper;
import com.truthbean.code.debbie.core.reflection.TypeHelper;
import com.truthbean.code.debbie.jdbc.annotation.SqlEntity;
import com.truthbean.code.debbie.jdbc.column.type.ColumnTypeHandler;
import com.truthbean.code.debbie.jdbc.column.type.JdbcTypeConstants;
import com.truthbean.code.debbie.jdbc.annotation.SqlColumn;
import com.truthbean.code.debbie.jdbc.column.ColumnInfo;
import com.truthbean.code.debbie.jdbc.column.JdbcColumnHandler;
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

    public RepositoryHandler(Connection connection) {
        this.connection = connection;
    }

    public <E> EntityInfo resolveEntity(E entity) {
        var classInfo = BeanInitializationHandler.getRegisterBean(entity.getClass());
        var table = getTableName(classInfo);
        var columns = resolveColumnInfo(classInfo, entity);
        var entityInfo = new EntityInfo();
        entityInfo.setTable(table);
        entityInfo.setColumnInfoList(columns);
        for (var column: columns) {
            if (column.isPrimaryKey()) {
                entityInfo.setPrimaryKeyType(column.getPrimaryKeyType());
                entityInfo.setPrimaryKeyClass(column.getJavaClass());
                break;
            }
        }
        return entityInfo;
    }

    public String getTableName(ClassInfo classInfo) {
        SqlEntity sqlEntity = (SqlEntity) classInfo.getClassAnnotations().get(SqlEntity.class);
        var entityClass = classInfo.getClazz();
        var table = sqlEntity.table();
        if ("".equals(table.trim())) {
            table = entityClass.getSimpleName().toLowerCase();
        }
        return table;
    }

    public List<ColumnInfo> getColumnInfo(ClassInfo classInfo) {
        List<ColumnInfo> columns = new ArrayList<>();
        var fields = classInfo.getFields();
        if (!fields.isEmpty()) {
            for (var field : fields) {
                columns.add(JdbcColumnHandler.getColumnInfo(field));
            }
        }
        return columns;
    }

    public <E> List<ColumnInfo> resolveColumnInfo(ClassInfo classInfo, E entity) {
        List<ColumnInfo> columns = new ArrayList<>();
        var fields = classInfo.getFields();
        if (!fields.isEmpty()) {
            for (var field : fields) {
                columns.add(JdbcColumnHandler.resolveField(field, entity));
            }
        }
        return columns;
    }

    public int[] batch(String sql, Object[][] args) {
        PreparedStatement preparedStatement = null;
        int[] rows = null;
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
        } finally {
            JdbcUtils.close(null, preparedStatement);
        }
        return rows;
    }

    public Object rawInsert(String sql, Object... args) {
        Object id = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            //Statement.RETURN_GENERATED_KEYS 获取自动生成的键
            preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            if (args != null) {
                for (int i = 0; i < args.length; i++) {
                    ColumnTypeHandler.setSqlArgValue(preparedStatement, i + 1, args[i]);
                }
            }
            preparedStatement.executeUpdate();
            resultSet = preparedStatement.getGeneratedKeys();
            //autoGeneratedKeys - 指示是否应该返回自动生成的键的标志
            //Statement.RETURN_GENERATED_KEYS  Statement.NO_GENERATED_KEYS 之一
            if (resultSet.next()) {
                id = resultSet.getObject(1);
            }
        } catch (SQLException ex) {
            LOGGER.error(null, ex);
        } finally {
            JdbcUtils.close(resultSet, preparedStatement);
        }
        return id;
    }

    public <K> K insert(String sql, boolean generatedKeys, Class<K> keyClass, List args) {
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
            if (args != null && !args.isEmpty()) {
                for (int i = 0; i < args.size(); i++) {
                    ColumnTypeHandler.setSqlArgValue(preparedStatement, i + 1, args.get(i));
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
                    ColumnTypeHandler.setSqlArgValue(preparedStatement, e + 1, args[e]);
                }
            }
            return preparedStatement.executeQuery();
        } catch (SQLException e) {
            LOGGER.error(null, e);
            throw new RuntimeException(e);
        }
    }

    public List<List<ColumnInfo>> select(String sql, Object... args) {
        ResultSet resultSet = preSelect(sql, args);
        return JdbcColumnHandler.getTablecolumnInfo(resultSet, new FStartColumnNameTransformer());
    }

    public <T> List<T> select(String selectSql, Class<T> clazz, Object... args) {
        List<List<ColumnInfo>> selectResult = select(selectSql, args);
        List<T> result = new ArrayList<>();
        T obj;
        if (!TypeHelper.isBaseType(clazz)) {
            List<Field> declaredFields = ReflectionHelper.getDeclaredFields(clazz);
            for (var map : selectResult) {
                obj = transformer(map, declaredFields, clazz);
                result.add(obj);
            }
        } else {
            for (List<ColumnInfo> map : selectResult) {
                if (map.size() == 1) {
                    ColumnInfo data = map.get(0);
                    if (data.getJavaClass() == clazz) {
                        result.add((T) data.getValue());
                    }
                }
            }
        }

        return result;
    }

    public <T> T selectOne(String selectSql, Class<T> clazz, Object... args) {
        List<List<ColumnInfo>> selectResult = select(selectSql, args);
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
                        result = (T) data.getValue();
                    } else {
                        throw new ClassNotMatchedException(data.getJavaClass(), clazz);
                    }
                }
            }
        }
        return result;
    }

    private <T> T transformer(List<ColumnInfo> map, List<Field> declaredFields, Class<T> clazz) {
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
