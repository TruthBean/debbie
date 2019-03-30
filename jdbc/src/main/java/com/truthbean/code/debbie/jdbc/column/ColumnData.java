package com.truthbean.code.debbie.jdbc.column;

import java.sql.JDBCType;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-03-19 11:18.
 */
public class ColumnData<T> {
    private String propertyName;

    private String columnName;

    private String dataType;
    private JDBCType jdbcType;

    private String columnComment;

    private String columnType;

    private String charMaxLength;

    private boolean nullable;

    private int scale;

    private int precision;

    private String classType;

    private String optionType;

    private Class javaClass;

    private T value;

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getColumnComment() {
        return columnComment;
    }

    public void setColumnComment(String columnComment) {
        this.columnComment = columnComment;
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public String getCharMaxLength() {
        return charMaxLength;
    }

    public void setCharMaxLength(String charMaxLength) {
        this.charMaxLength = charMaxLength;
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    public int getPrecision() {
        return precision;
    }

    public void setPrecision(int precision) {
        this.precision = precision;
    }

    public String getClassType() {
        return classType;
    }

    public void setClassType(String classType) {
        this.classType = classType;
    }

    public String getOptionType() {
        return optionType;
    }

    public void setOptionType(String optionType) {
        this.optionType = optionType;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public Class getJavaClass() {
        return javaClass;
    }

    public void setJavaClass(Class javaClass) {
        this.javaClass = javaClass;
    }

    public JDBCType getJdbcType() {
        return jdbcType;
    }

    public void setJdbcType(JDBCType jdbcType) {
        this.jdbcType = jdbcType;
    }

    @Override
    public String toString() {
        return "{" + "\"propertyName\":\"" + propertyName + "\"" + "," + "\"columnName\":\"" + columnName + "\"" + ","
                + "\"dataType\":\"" + dataType + "\"" + "," + "\"jdbcType\":" + jdbcType + ","
                + "\"columnComment\":\"" + columnComment + "\"" + "," + "\"columnType\":\"" + columnType + "\"" + ","
                + "\"charMaxLength\":\"" + charMaxLength + "\"" + "," + "\"nullable\":" + nullable + ","
                + "\"scale\":" + scale + "," + "\"precision\":" + precision + ","
                + "\"classType\":\"" + classType + "\"" + "," + "\"optionType\":\"" + optionType + "\"" + ","
                + "\"javaClass\":" + javaClass + "," + "\"value\":" + value + "}";
    }
}
