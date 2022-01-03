/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.jdbc.column;

import com.truthbean.debbie.jdbc.annotation.SqlColumn;
import com.truthbean.debbie.jdbc.column.type.ColumnTypeHandler;
import com.truthbean.debbie.jdbc.datasource.DataSourceDriverName;
import com.truthbean.debbie.jdbc.datasource.DriverConnection;
import com.truthbean.debbie.jdbc.entity.EntityResolver;
import com.truthbean.debbie.jdbc.repository.DynamicRepository;
import com.truthbean.debbie.jdbc.util.JdbcUtils;
import com.truthbean.debbie.reflection.ReflectionHelper;
import com.truthbean.Logger;
import com.truthbean.LoggerFactory;

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

    private JdbcColumnResolver() {
    }

    public static List<List<ColumnInfo>> resolveResultSetValue(ResultSet resultSet, ColumnNameTransformer columnNameTransformer) {
        List<List<ColumnInfo>> columnLists = new ArrayList<>();
        try {
            var resultSetMetaData = resultSet.getMetaData();

            int columnCount = resultSetMetaData.getColumnCount();
            LOGGER.trace(() ->"columnCount: " + columnCount);

            ColumnInfo columnInfo;
            List<ColumnInfo> columnList;

            while (resultSet.next()) {
                columnList = new ArrayList<>();
                for (int i = 1; i <= columnCount; i++) {

                    int columnType = resultSetMetaData.getColumnType(i);
                    LOGGER.trace(() -> "columnType: " + columnType);

                    String columnClassName = resultSetMetaData.getColumnClassName(i);
                    LOGGER.trace(() -> "columnClassName: " + columnClassName);

                    String columnLabel = resultSetMetaData.getColumnLabel(i);
                    LOGGER.trace(() -> "columnLabel: " + columnLabel);

                    String columnName = resultSetMetaData.getColumnName(i);
                    LOGGER.trace(() -> "columnName: " + columnName);

                    String columnTypeName = resultSetMetaData.getColumnTypeName(i);
                    LOGGER.trace(() -> "columnTypeName: " + columnTypeName);

                    int scale = resultSetMetaData.getScale(i);
                    LOGGER.trace(() -> "scale: " + scale);

                    int precision = resultSetMetaData.getPrecision(i);
                    LOGGER.trace(() -> "precision: " + precision);

                    String type = ColumnTypeHandler.getType(columnTypeName, precision, scale);

                    columnInfo = new ColumnInfo();
                    if (columnLabel != null && !"".equals(columnLabel.trim())) {
                        columnInfo.setProperty(columnNameTransformer.columnNameToPropertyName(columnLabel));
                        columnInfo.setColumn(columnLabel);
                    } else {
                        columnInfo.setProperty(columnNameTransformer.columnNameToPropertyName(columnName));
                        columnInfo.setColumn(columnName);
                    }

                    columnInfo.setDataType(type);
                    columnInfo.setJdbcType(JDBCType.valueOf(columnType));
                    columnInfo.setColumnType(columnTypeName);
                    try {
                        columnInfo.setJavaClass(Class.forName(columnClassName));
                        columnInfo.setValue(ColumnTypeHandler.getColumnValue(resultSet, i, columnClassName));
                    } catch (ClassNotFoundException e) {
                        LOGGER.error(() -> columnClassName + " has no class defined ");
                        try {
                            columnInfo.setJavaClass(Class.forName(type));
                            columnInfo.setValue(ColumnTypeHandler.getColumnValue(resultSet, i, type));
                        } catch (ClassNotFoundException ex) {
                            LOGGER.error(() -> type + " has no class defined ", e);
                        }
                    }

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
            LOGGER.error("", e);
        }
        return columnLists;
    }

    public static List<ColumnInfo> resolveColumns(DriverConnection driverConnection, String tableName,
                                                  ColumnNameTransformer columnNameTransformer) {
        List<ColumnInfo> columnList = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            Connection connection = driverConnection.getConnection();
            DataSourceDriverName driverName = driverConnection.getDriverName();
            var sql = DynamicRepository.sql(driverName).selectAll().from(tableName).builder();
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            columnList = resolveResultSetValue(resultSet, columnNameTransformer).get(0);
        } catch (SQLException e) {
            LOGGER.error("", e);
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
                var columnName = EntityResolver.getColumnName(sqlColumn, field.getName());
                columnInfo.setColumn(columnName);

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
                var columnName = EntityResolver.getColumnName(sqlColumn, field.getName());
                columnInfo.setColumn(columnName);

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
            columnInfo.setColumn(field.getName());
        }
        columnInfo.setJdbcType(ColumnTypeHandler.explain(field.getType()));
        var isCharMaxLengthZero = (columnInfo.getJdbcType() == JDBCType.VARCHAR ||
                columnInfo.getJdbcType() == JDBCType.LONGVARCHAR) && columnInfo.getCharMaxLength() == 0;
        if (isCharMaxLengthZero) {
            columnInfo.setCharMaxLength(64);
        }
        columnInfo.setProperty(field.getName());
        columnInfo.setJavaClass(field.getType());
        return columnInfo;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcColumnResolver.class);
}
