/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.jdbc.entity;

import com.truthbean.debbie.jdbc.column.ColumnInfo;
import com.truthbean.debbie.jdbc.datasource.DataSourceDriverName;

import java.util.List;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/4/4 23:07.
 */
public class EntityInfo<E> {
    private Class<E> javaType;

    private DataSourceDriverName driverName;
    private String table;

    private ColumnInfo primaryKey;

    private List<ColumnInfo> columnInfoList;

    private String charset;
    private String engine;

    public DataSourceDriverName getDriverName() {
        return driverName;
    }

    public void setDriverName(DataSourceDriverName driverName) {
        this.driverName = driverName;
    }

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
        var javaTypeName = "";
        if (javaType != null) {
            javaTypeName = javaType.getName();
        }
        return "{\"table\":\"" + table + "\",\"primaryKey\":" + primaryKey +
                ",\"columnInfoList\":" + columnInfoList + ",\"javaType\":" + javaTypeName +
                ",\"charset\":\"" + charset + "\",\"engine\":\"" + engine + "\"}";
    }
}
