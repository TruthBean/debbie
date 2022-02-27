/**
 * Copyright (c) 2022 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.jdbc.column.type;

import com.truthbean.debbie.jdbc.datasource.DataSourceDriverName;
import com.truthbean.debbie.reflection.ClassLoaderUtils;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;

/**
 * @author 璩诗斌
 * @since 0.0.1
 */
public class ColumnTypeHandler {

    private ColumnTypeHandler() {
    }

    public static String getType(String dataType, int precision, int scale) {
        dataType = dataType.toLowerCase();
        if (dataType.contains("char")) {
            dataType = JdbcTypeConstants.STRING;
        } else if (dataType.contains("int")) {
            dataType = "int";
        } else if (dataType.contains("float")) {
            dataType = "float";
        } else if (dataType.contains("double")) {
            dataType = "double";
        } else if (dataType.contains("number")) {
            if (scale > 0) {
                dataType = JdbcTypeConstants.BIG_INTEGER;
            } else if (precision > 6) {
                dataType = "long";
            } else {
                dataType = JdbcTypeConstants.INTEGER;
            }
        } else if (dataType.contains("decimal")) {
            dataType = JdbcTypeConstants.BIG_DECIMAL;
        } else if (dataType.contains("date")) {
            dataType = JdbcTypeConstants.DATE;
        } else if (dataType.contains("time")) {
            dataType = JdbcTypeConstants.TIMESTAMP;
        } else if (dataType.contains("clob")) {
            dataType = JdbcTypeConstants.CLOB;
        } else if (dataType.contains("blob")) {
            dataType = JdbcTypeConstants.BLOB;
        } else {
            dataType = "Object";
        }

        return dataType;
    }

    public static <T> T getColumnValue(ResultSet resultSet, int index, Class<T> columnClass, String columnClassName) throws SQLException {
        return switch (columnClassName) {
            case JdbcTypeConstants.ARRAY -> columnClass.cast(resultSet.getArray(index));
            case JdbcTypeConstants.INPUT_STREAM -> columnClass.cast(resultSet.getBinaryStream(index));
            case JdbcTypeConstants.BIG_DECIMAL -> columnClass.cast(resultSet.getBigDecimal(index));
            case JdbcTypeConstants.BLOB -> columnClass.cast(resultSet.getBlob(index));
            case JdbcTypeConstants.BOOLEAN -> columnClass.cast(resultSet.getBoolean(index));
            case JdbcTypeConstants.BYTE -> columnClass.cast(resultSet.getByte(index));
            case JdbcTypeConstants.BYTE_ARRAY -> columnClass.cast(resultSet.getBytes(index));
            case JdbcTypeConstants.READER -> columnClass.cast(resultSet.getCharacterStream(index));
            // getNCharacterStream

            case JdbcTypeConstants.CLOB -> columnClass.cast(resultSet.getClob(index));
            case JdbcTypeConstants.DATE -> columnClass.cast(resultSet.getDate(index));
            case JdbcTypeConstants.DOUBLE -> columnClass.cast(resultSet.getDouble(index));
            case JdbcTypeConstants.FLOAT -> columnClass.cast(resultSet.getFloat(index));
            case JdbcTypeConstants.INTEGER -> columnClass.cast(resultSet.getInt(index));
            case JdbcTypeConstants.LONG -> columnClass.cast(resultSet.getLong(index));
            case JdbcTypeConstants.NCLOB -> columnClass.cast(resultSet.getNClob(index));
            case JdbcTypeConstants.STRING -> columnClass.cast(resultSet.getString(index));
            // resultSet.getNString(index)

            case JdbcTypeConstants.REF -> columnClass.cast(resultSet.getRef(index));
            case JdbcTypeConstants.ROW_ID -> columnClass.cast(resultSet.getRowId(index));
            case JdbcTypeConstants.SHORT -> columnClass.cast(resultSet.getShort(index));
            case JdbcTypeConstants.SQLXML -> columnClass.cast(resultSet.getSQLXML(index));
            case JdbcTypeConstants.TIME -> columnClass.cast(resultSet.getTime(index));
            case JdbcTypeConstants.TIMESTAMP -> columnClass.cast(resultSet.getTimestamp(index));
            case JdbcTypeConstants.URL -> columnClass.cast(resultSet.getURL(index));
            default -> columnClass.cast(resultSet.getObject(index));
        };
    }

    public static <T> T getColumnValue(ResultSet resultSet, int index, String columnClassName)
            throws SQLException, ClassNotFoundException {
        var classLoader = ClassLoaderUtils.getClassLoader(ColumnTypeHandler.class);
        @SuppressWarnings("unchecked")
        Class<T> columnClass = (Class<T>) classLoader.loadClass(columnClassName);
        return getColumnValue(resultSet, index, columnClass, columnClassName);
    }

    public static JDBCType explain(Class<?> type) {
        return switch (type.getName()) {
            case JdbcTypeConstants.ARRAY -> JDBCType.ARRAY;
            case JdbcTypeConstants.INPUT_STREAM, JdbcTypeConstants.BLOB, JdbcTypeConstants.BYTE_ARRAY, JdbcTypeConstants.READER -> JDBCType.BLOB;
            case JdbcTypeConstants.BIG_DECIMAL -> JDBCType.DECIMAL;
            case JdbcTypeConstants.BOOLEAN -> JDBCType.BOOLEAN;
            case JdbcTypeConstants.BYTE -> JDBCType.BIT;
            case JdbcTypeConstants.CLOB -> JDBCType.CLOB;
            case JdbcTypeConstants.DATE -> JDBCType.DATE;
            case JdbcTypeConstants.DOUBLE -> JDBCType.DOUBLE;
            case JdbcTypeConstants.FLOAT -> JDBCType.FLOAT;
            case JdbcTypeConstants.INTEGER, JdbcTypeConstants.LONG -> JDBCType.INTEGER;
            case JdbcTypeConstants.BIG_INTEGER -> JDBCType.BIGINT;
            case JdbcTypeConstants.NCLOB -> JDBCType.NCLOB;
            case JdbcTypeConstants.STRING, JdbcTypeConstants.URL -> JDBCType.VARCHAR;
            case JdbcTypeConstants.REF -> JDBCType.REF;
            case JdbcTypeConstants.ROW_ID -> JDBCType.ROWID;
            case JdbcTypeConstants.SHORT -> JDBCType.SMALLINT;
            case JdbcTypeConstants.SQLXML -> JDBCType.SQLXML;
            case JdbcTypeConstants.TIME -> JDBCType.TIME;
            case JdbcTypeConstants.TIMESTAMP -> JDBCType.TIMESTAMP;
            default -> JDBCType.LONGVARCHAR;
        };
    }

    public static void setSqlArgValue(DataSourceDriverName driverName, PreparedStatement preparedStatement, int index,
                                      Object arg) throws SQLException {
        if (arg == null) {
            preparedStatement.setObject(index, null);
            return;
        }
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
                if (driverName != DataSourceDriverName.sqlite) {
                    preparedStatement.setURL(index, (URL) arg);
                } else {
                    preparedStatement.setString(index, arg.toString());
                }
                break;

            default:
                preparedStatement.setObject(index, arg);
        }
    }
}
