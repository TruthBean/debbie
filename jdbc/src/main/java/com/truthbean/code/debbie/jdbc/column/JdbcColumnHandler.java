package com.truthbean.code.debbie.jdbc.column;

import com.truthbean.code.debbie.jdbc.column.type.ColumnTypeHandler;
import com.truthbean.code.debbie.jdbc.annotation.SqlColumn;
import com.truthbean.code.debbie.jdbc.repository.DynamicSqlBuilder;
import com.truthbean.code.debbie.jdbc.util.JdbcUtils;
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
public class JdbcColumnHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcColumnHandler.class);

    public static List<List<ColumnInfo>> getTableColumnData(ResultSet resultSet, ColumnNameTransformer columnNameTransformer) {
        List<List<ColumnInfo>> columnLists = new ArrayList<>();
        try {
            var resultSetMetaData = resultSet.getMetaData();

            int columnCount = resultSetMetaData.getColumnCount();
            LOGGER.debug("columnCount: {}", columnCount);

            ColumnInfo columnData;
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

                    columnData = new ColumnInfo();
                    columnData.setPropertyName(columnNameTransformer.columnNameToPropertyName(columnName));
                    columnData.setColumnName(columnName);

                    columnData.setDataType(type);
                    columnData.setJdbcType(JDBCType.valueOf(columnType));
                    columnData.setColumnType(columnTypeName);
                    try {
                        columnData.setJavaClass(Class.forName(columnClassName));
                    } catch (ClassNotFoundException e) {
                        LOGGER.error(columnClassName + " has no class defined ");
                    }
                    columnData.setValue(ColumnTypeHandler.getColumnValue(resultSet, i, columnClassName));

                    // columnData.setColumnComment(comment);
                    columnData.setPrecision(precision);
                    columnData.setScale(scale);

                    //cd.setCharmaxLength(charmaxLength);
                    //cd.setNullable(nullable);

                    columnList.add(columnData);
                }
                columnLists.add(columnList);
            }
        } catch (SQLException e) {
            LOGGER.error(null, e);
        }
        return columnLists;
    }



    public static List<ColumnInfo> getTableColumnData(Connection connection, String tableName,
                                                      ColumnNameTransformer columnNameTransformer) {
        List<ColumnInfo> columnList = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            var sql = DynamicSqlBuilder.sql().select("*").from(tableName).builder();
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            columnList = getTableColumnData(resultSet, columnNameTransformer).get(0);
        } catch (SQLException e) {
            LOGGER.error(null, e);
        } finally {
            JdbcUtils.close(resultSet, preparedStatement);
        }

        return columnList;
    }

    public static ColumnInfo getColumnInfo(Field field) {
        var columnData = new ColumnInfo<>();
        SqlColumn sqlColumn = field.getAnnotation(SqlColumn.class);
        if (sqlColumn != null) {
            if (sqlColumn.id()) {
                var columnName = sqlColumn.name();
                if (!"".equals(columnName.trim())) {
                    columnData.setColumnName(columnName);
                } else {
                    columnData.setColumnName(field.getName());
                }
                columnData.setNullable(false);
                if (field.getType() == UUID.class) {
                    columnData.setCharMaxLength(64);
                }
                if (!"".equals(sqlColumn.comment().trim())) {
                    columnData.setComment(sqlColumn.comment());
                }
                columnData.setPrimaryKey(true);
            } else {
                var columnName = sqlColumn.name();
                if (!"".equals(columnName.trim())) {
                    columnData.setColumnName(columnName);
                } else {
                    columnData.setColumnName(field.getName());
                }
                columnData.setNullable(sqlColumn.nullable());
                columnData.setUnique(sqlColumn.unique());
                columnData.setCharMaxLength(sqlColumn.charMaxLength());
                if (!"".equals(sqlColumn.defaultValue())) {
                    columnData.setDefaultValue(sqlColumn.defaultValue());
                }
                if (!"".equals(sqlColumn.comment().trim())) {
                    columnData.setComment(sqlColumn.comment());
                }
                columnData.setCharMaxLength(sqlColumn.charMaxLength());
            }

        } else {
            columnData.setColumnName(field.getName());
        }
        columnData.setJdbcType(ColumnTypeHandler.explain(field.getType()));
        return columnData;
    }
}
