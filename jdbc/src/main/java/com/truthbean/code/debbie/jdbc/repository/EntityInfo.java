package com.truthbean.code.debbie.jdbc.repository;

import com.truthbean.code.debbie.jdbc.column.ColumnInfo;
import com.truthbean.code.debbie.jdbc.column.PrimaryKeyType;

import java.util.List;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/4/4 23:07.
 */
public class EntityInfo {
    private String table;
    private PrimaryKeyType primaryKeyType;
    private Class<?> primaryKeyClass;
    private List<ColumnInfo> columnInfoList;

    private String charset;
    private String engine;

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public PrimaryKeyType getPrimaryKeyType() {
        return primaryKeyType;
    }

    public void setPrimaryKeyType(PrimaryKeyType primaryKeyType) {
        this.primaryKeyType = primaryKeyType;
    }

    public List<ColumnInfo> getColumnInfoList() {
        return columnInfoList;
    }

    public void setColumnInfoList(List<ColumnInfo> columnInfoList) {
        this.columnInfoList = columnInfoList;
    }

    public Class<?> getPrimaryKeyClass() {
        return primaryKeyClass;
    }

    public void setPrimaryKeyClass(Class<?> primaryKeyClass) {
        this.primaryKeyClass = primaryKeyClass;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    @Override
    public String toString() {
        return "{" + "\"table\":\"" + table + "\"" + "," + "\"primaryKeyType\":" + primaryKeyType + ","
                + "\"primaryKeyClass\":" + primaryKeyClass + "," + "\"columnInfoList\":" + columnInfoList + ","
                + "\"charset\":\"" + charset + "\"" + "," + "\"engine\":\"" + engine + "\"" + "}";
    }
}
