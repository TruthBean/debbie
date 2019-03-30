package com.truthbean.code.debbie.jdbc.column;

import com.truthbean.code.debbie.jdbc.JdbcTypeConstants;
import com.truthbean.code.debbie.jdbc.util.JdbcUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-03-19 11:21.
 */
public class ColumnDataHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ColumnDataHelper.class);

    public static List<List<ColumnData>> getTableColumnData(ResultSet resultSet, ColumnNameTransformer columnNameTransformer) {
        List<List<ColumnData>> columnLists = new ArrayList<>();
        try {
            var resultSetMetaData = resultSet.getMetaData();

            int columnCount = resultSetMetaData.getColumnCount();
            LOGGER.debug("columnCount: {}", columnCount);

            ColumnData columnData;
            List<ColumnData> columnList;

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

                    String type = getType(columnTypeName, precision, scale);

                    columnData = new ColumnData();
                    columnData.setPropertyName(columnNameTransformer.changeColumnNameToPropertyName(columnName));
                    columnData.setColumnName(columnName);

                    columnData.setDataType(type);
                    columnData.setJdbcType(JDBCType.valueOf(columnType));
                    columnData.setColumnType(columnTypeName);
                    try {
                        columnData.setJavaClass(Class.forName(columnClassName));
                    } catch (ClassNotFoundException e) {
                        LOGGER.error(columnClassName + " has no class defined ");
                    }
                    columnData.setValue(getColumnDataValue(resultSet, i, columnClassName));

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

    private static <T> T getColumnDataValue(ResultSet resultSet, int index, String columnClassName) throws SQLException {
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

    public static List<ColumnData> getTableColumnData(Connection connection, String tableName,
                                                      ColumnNameTransformer columnNameTransformer) {
        List<ColumnData> columnList = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            var sql = "SELECT * FROM " + tableName;
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
}
