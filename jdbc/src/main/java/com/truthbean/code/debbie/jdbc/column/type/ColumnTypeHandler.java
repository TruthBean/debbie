package com.truthbean.code.debbie.jdbc.column.type;

import java.sql.JDBCType;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author 璩诗斌
 * @since 0.0.1
 */
public class ColumnTypeHandler {

    public static String getType(String dataType, int precision, int scale) {
        dataType = dataType.toLowerCase();
        if (dataType.contains("char")) {
            dataType = "String";
        } else if (dataType.contains("int")) {
            dataType = "int";
        } else if (dataType.contains("float")) {
            dataType = "float";
        } else if (dataType.contains("double")) {
            dataType = "double";
        } else if (dataType.contains("number")) {
            if (scale > 0) {
                dataType = "BigDecimal";
            } else if (precision > 6) {
                dataType = "long";
            } else {
                dataType = "integer";
            }
        } else if (dataType.contains("decimal")) {
            dataType = "BigDecimal";
        } else if (dataType.contains("date")) {
            dataType = "Date";
        } else if (dataType.contains("time")) {
            dataType = "Timestamp";
        } else if (dataType.contains("clob")) {
            dataType = "Clob";
        } else if (dataType.contains("blob")) {
            dataType = "Blob";
        } else {
            dataType = "Object";
        }

        return dataType;
    }

    public static <T> T getColumnValue(ResultSet resultSet, int index, String columnClassName) throws SQLException {
        switch (columnClassName) {
            case JdbcTypeConstants.ARRAY:
                return (T) resultSet.getArray(index);

            case JdbcTypeConstants.INPUT_STREAM:
                return (T) resultSet.getBinaryStream(index);

            case JdbcTypeConstants.BIG_DECIMAL:
                return (T) resultSet.getBigDecimal(index);

            case JdbcTypeConstants.BLOB:
                return (T) resultSet.getBlob(index);

            case JdbcTypeConstants.BOOLEAN:
                return (T) Boolean.valueOf(resultSet.getBoolean(index));

            case JdbcTypeConstants.BYTE:
                return (T) Byte.valueOf(resultSet.getByte(index));

            case JdbcTypeConstants.BYTE_ARRAY:
                return (T) resultSet.getBytes(index);

            case JdbcTypeConstants.READER:
                return (T) resultSet.getCharacterStream(index);
            // getNCharacterStream

            case JdbcTypeConstants.CLOB:
                return (T) resultSet.getClob(index);

            case JdbcTypeConstants.DATE:
                return (T) resultSet.getDate(index);

            case JdbcTypeConstants.DOUBLE:
                return (T) Double.valueOf(resultSet.getDouble(index));

            case JdbcTypeConstants.FLOAT:
                return (T) Double.valueOf(resultSet.getFloat(index));

            case JdbcTypeConstants.INTEGER:
                return (T) Integer.valueOf(resultSet.getInt(index));

            case JdbcTypeConstants.LONG:
                return (T) Long.valueOf(resultSet.getLong(index));

            case JdbcTypeConstants.NCLOB:
                return (T) resultSet.getNClob(index);

            case JdbcTypeConstants.STRING:
                return (T) resultSet.getString(index);
            // resultSet.getNString(index)

            case JdbcTypeConstants.REF:
                return (T) resultSet.getRef(index);

            case JdbcTypeConstants.ROW_ID:
                return (T) resultSet.getRowId(index);

            case JdbcTypeConstants.SHORT:
                return (T) Short.valueOf(resultSet.getShort(index));

            case JdbcTypeConstants.SQLXML:
                return (T) resultSet.getSQLXML(index);

            case JdbcTypeConstants.TIME:
                return (T) resultSet.getTime(index);

            case JdbcTypeConstants.TIMESTAMP:
                return (T) resultSet.getTimestamp(index);

            case JdbcTypeConstants.URL:
                return (T) resultSet.getURL(index);

            default:
                return (T) resultSet.getObject(index);
        }
    }

    public static JDBCType explain(Class<?> type) {
        switch (type.getName()) {
            case JdbcTypeConstants.ARRAY:
                return JDBCType.ARRAY;

            case JdbcTypeConstants.INPUT_STREAM:
            case JdbcTypeConstants.BLOB:
            case JdbcTypeConstants.BYTE_ARRAY:
            case JdbcTypeConstants.READER:
                return JDBCType.BLOB;

            case JdbcTypeConstants.BIG_DECIMAL:
                return JDBCType.DECIMAL;

            case JdbcTypeConstants.BOOLEAN:
                return JDBCType.BOOLEAN;

            case JdbcTypeConstants.BYTE:
                return JDBCType.BIT;

            case JdbcTypeConstants.CLOB:
                return JDBCType.CLOB;

            case JdbcTypeConstants.DATE:
                return JDBCType.DATE;

            case JdbcTypeConstants.DOUBLE:
                return JDBCType.DOUBLE;

            case JdbcTypeConstants.FLOAT:
                return JDBCType.FLOAT;

            case JdbcTypeConstants.INTEGER:
            case JdbcTypeConstants.LONG:
                return JDBCType.INTEGER;

            case JdbcTypeConstants.BIG_INTEGER:
                return JDBCType.BIGINT;

            case JdbcTypeConstants.NCLOB:
                return JDBCType.NCLOB;

            case JdbcTypeConstants.STRING:
            case JdbcTypeConstants.URL:
                return JDBCType.VARCHAR;

            case JdbcTypeConstants.REF:
                return JDBCType.REF;

            case JdbcTypeConstants.ROW_ID:
                return JDBCType.ROWID;

            case JdbcTypeConstants.SHORT:
                return JDBCType.SMALLINT;

            case JdbcTypeConstants.SQLXML:
                return JDBCType.SQLXML;

            case JdbcTypeConstants.TIME:
                return JDBCType.TIME;

            case JdbcTypeConstants.TIMESTAMP:
                return JDBCType.TIMESTAMP;

            default:
                return JDBCType.LONGVARCHAR;
        }
    }
}
