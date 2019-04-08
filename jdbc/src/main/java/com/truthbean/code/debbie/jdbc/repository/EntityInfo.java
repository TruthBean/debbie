package com.truthbean.code.debbie.jdbc.repository;

import com.truthbean.code.debbie.jdbc.column.ColumnInfo;

import java.util.List;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/4/4 23:07.
 */
public class EntityInfo<E> {
    private Class<E> javaType;
    private String table;

    private ColumnInfo primaryKey;

    private List<ColumnInfo> columnInfoList;

    private String charset;
    private String engine;

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public ColumnInfo getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(ColumnInfo primaryKey) {
        this.primaryKey = primaryKey;
    }

    public List<ColumnInfo> getColumnInfoList() {
        return columnInfoList;
    }

    public void setColumnInfoList(List<ColumnInfo> columnInfoList) {
        this.columnInfoList = columnInfoList;
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

    public Class<E> getJavaType() {
        return javaType;
    }

    public void setJavaType(Class<E> javaType) {
        this.javaType = javaType;
    }

    @Override
    public String toString() {
        return "{\"table\":\"" + table + "\",\"primaryKey\":" + primaryKey +
                ",\"columnInfoList\":" + columnInfoList + ",\"javaType\":" + javaType.getName() +
                ",\"charset\":\"" + charset + "\",\"engine\":\"" + engine + "\"}";
    }
}
