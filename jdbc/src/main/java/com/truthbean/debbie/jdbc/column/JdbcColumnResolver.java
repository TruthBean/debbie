package com.truthbean.debbie.jdbc.column;

import com.truthbean.debbie.core.reflection.ReflectionHelper;
import com.truthbean.debbie.jdbc.annotation.SqlColumn;
import com.truthbean.debbie.jdbc.column.type.ColumnTypeHandler;
import com.truthbean.debbie.jdbc.repository.DynamicSqlBuilder;
import com.truthbean.debbie.jdbc.util.JdbcUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-03-19 11:21.
 */
public class JdbcColumnResolver {

    public static List<List<ColumnInfo>> resolveResultSetValue(ResultSet resultSet, ColumnNameTransformer columnNameTransformer) {
        List<List<ColumnInfo>> columnLists = new ArrayList<>();
        try {
            var resultSetMetaData = resultSet.getMetaData();

            int columnCount = resultSetMetaData.getColumnCount();
            LOGGER.debug("columnCount: {}", columnCount);

            ColumnInfo columnInfo;
            List<ColumnInfo> columnList;

            while (resultSet.next()) {
                columnList = new ArrayList<>();
                for (int i = 1; i <= columnCount; i++) {

                    int columnType = resultSetMetaData.getColumnType(i);
                    LOGGER.debug("columnType: {}", columnType);

                    String columnClassName = resultSetMetaData.getColumnClassName(i);
                    LOGGER.debug("columnClassName: {}", columnClassName);

                    String columnLabel = resultSetMetaData.getColumnLabel(i);
                    LOGGER.debug("columnLabel: {}", columnLabel);

                    String columnName = resultSetMetaData.getColumnName(i);
                    LOGGER.debug("columnName: {}", columnName);

                    String columnTypeName = resultSetMetaData.getColumnTypeName(i);
                    LOGGER.debug("columnTypeName: {}", columnTypeName);

                    int scale = resultSetMetaData.getScale(i);
                    LOGGER.debug("scale: {}", scale);

                    int precision = resultSetMetaData.getPrecision(i);
                    LOGGER.debug("precision: {}", precision);

                    String type = ColumnTypeHandler.getType(columnTypeName, precision, scale);

                    columnInfo = new ColumnInfo();
                    columnInfo.setPropertyName(columnNameTransformer.columnNameToPropertyName(columnName));
                    columnInfo.setColumnName(columnName);

                    columnInfo.setDataType(type);
                    columnInfo.setJdbcType(JDBCType.valueOf(columnType));
                    columnInfo.setColumnType(columnTypeName);
                    try {
                        columnInfo.setJavaClass(Class.forName(columnClassName));
                    } catch (ClassNotFoundException e) {
                        LOGGER.error(columnClassName + " has no class defined ");
                    }
                    columnInfo.setValue(ColumnTypeHandler.getColumnValue(resultSet, i, columnClassName));

                    // columnInfo.setColumnComment(comment);
                    columnInfo.setPrecision(precision);
                    columnInfo.setScale(scale);

                    //cd.setCharmaxLength(charmaxLength);
                    //cd.setNullable(nullable);

                    columnList.add(columnInfo);
                }
                columnLists.add(columnList);
            }
        } catch (SQLException e) {
            LOGGER.error(null, e);
        }
        return columnLists;
    }

    public static List<ColumnInfo> resolveColumns(Connection connection, String tableName,
                                                      ColumnNameTransformer columnNameTransformer) {
        List<ColumnInfo> columnList = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            var sql = DynamicSqlBuilder.sql().selectAll().from(tableName).builder();
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            columnList = resolveResultSetValue(resultSet, columnNameTransformer).get(0);
        } catch (SQLException e) {
            LOGGER.error(null, e);
        } finally {
            JdbcUtils.close(resultSet, preparedStatement);
        }

        return columnList;
    }

    public static <E> ColumnInfo resolveFieldAndValue(Field field, E entity) {
        ColumnInfo columnInfo = resolveField(field);
        var value = ReflectionHelper.invokeGetMethod(entity, field.getName());
        columnInfo.setValue(value);
        return columnInfo;
    }

    public static ColumnInfo resolveField(Field field) {
        var columnInfo = new ColumnInfo();
        SqlColumn sqlColumn = field.getAnnotation(SqlColumn.class);
        if (sqlColumn != null) {
            if (sqlColumn.id()) {
                var columnName = sqlColumn.name();
                if (!columnName.isBlank()) {
                    columnInfo.setColumnName(columnName);
                } else {
                    columnInfo.setColumnName(field.getName());
                }
                columnInfo.setNullable(false);
                if (field.getType() == UUID.class) {
                    columnInfo.setCharMaxLength(64);
                }
                if (!sqlColumn.comment().isBlank()) {
                    columnInfo.setComment(sqlColumn.comment());
                }
                columnInfo.setPrimaryKey(true);
                columnInfo.setPrimaryKeyType(sqlColumn.primaryKey());
            } else {
                var columnName = sqlColumn.name();
                if (!columnName.isBlank()) {
                    columnInfo.setColumnName(columnName);
                } else {
                    columnInfo.setColumnName(field.getName());
                }
                columnInfo.setNullable(sqlColumn.nullable());
                columnInfo.setUnique(sqlColumn.unique());
                columnInfo.setCharMaxLength(sqlColumn.charMaxLength());
                if (!sqlColumn.defaultValue().isBlank()) {
                    columnInfo.setColumnDefaultValue(sqlColumn.defaultValue());
                }
                if (!sqlColumn.comment().isBlank()) {
                    columnInfo.setComment(sqlColumn.comment());
                }
                columnInfo.setCharMaxLength(sqlColumn.charMaxLength());
            }

        } else {
            columnInfo.setColumnName(field.getName());
        }
        columnInfo.setJdbcType(ColumnTypeHandler.explain(field.getType()));
        var isCharMaxLengthZero = (columnInfo.getJdbcType() == JDBCType.VARCHAR ||
                columnInfo.getJdbcType() == JDBCType.LONGVARCHAR) && columnInfo.getCharMaxLength() == 0;
        if (isCharMaxLengthZero) {
            columnInfo.setCharMaxLength(64);
        }
        columnInfo.setPropertyName(field.getName());
        columnInfo.setJavaClass(field.getType());
        return columnInfo;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcColumnResolver.class);
}
