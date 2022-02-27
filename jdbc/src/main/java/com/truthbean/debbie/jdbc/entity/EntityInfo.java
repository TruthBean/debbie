/**
 * Copyright (c) 2022 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.jdbc.entity;

import com.truthbean.debbie.jdbc.column.ColumnInfo;
import com.truthbean.debbie.jdbc.column.PrimaryKeyType;
import com.truthbean.debbie.jdbc.datasource.DataSourceDriverName;
import com.truthbean.debbie.lang.Copyable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/4/4 23:07.
 */
public class EntityInfo<E> implements Copyable<EntityInfo<E>> {
    private Class<E> javaType;

    private DataSourceDriverName driverName;
    private String table;

    private ColumnInfo primaryKey;

    private List<ColumnInfo> columnInfoList;

    private String charset;
    private String engine;

    public EntityInfo() {
    }

    public EntityInfo(EntityInfo<E> entityInfo) {
        this.javaType = entityInfo.javaType;
        this.driverName = entityInfo.driverName;
        this.table = entityInfo.table;
        this.primaryKey = entityInfo.primaryKey;
        this.columnInfoList = entityInfo.columnInfoList;
        this.charset = entityInfo.charset;
        this.engine = entityInfo.engine;
    }

    public DataSourceDriverName getDriverName() {
        return driverName;
    }

    public void setDriverName(DataSourceDriverName driverName) {
        this.driverName = driverName;
    }

    public String getTable() {
        return table;
    }

    public EntityInfo<E> setTable(String table) {
        this.table = table;
        return this;
    }

    public ColumnInfo getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(ColumnInfo primaryKey) {
        this.primaryKey = primaryKey;
    }

    public <ID> EntityInfo<E> setPrimaryKey(String name, Class<ID> javaClass, PrimaryKeyType type,
                                            EntityPropertyGetter<E, ID> getter, EntityPropertySetter<E, ID> setter) {
        ColumnInfo primaryKey = new ColumnInfo();
        primaryKey.setPrimaryKey(true);
        primaryKey.setColumn(name);
        primaryKey.setPrimaryKeyType(type);
        primaryKey.setJavaClass(javaClass);
        primaryKey.setPropertyGetter(getter);
        primaryKey.setPropertySetter(setter);
        this.primaryKey = primaryKey;
        return this;
    }

    public List<ColumnInfo> getColumnInfoList() {
        return columnInfoList;
    }

    public void setColumnInfoList(List<ColumnInfo> columnInfoList) {
        this.columnInfoList = columnInfoList;
    }

    public <P> EntityInfo<E> addColumn(String name, EntityPropertyGetter<E, P> getter, EntityPropertySetter<E, P> setter) {
        if (this.columnInfoList == null) {
            this.columnInfoList = new ArrayList<>();
        }
        ColumnInfo columnInfo = new ColumnInfo();
        columnInfo.setPrimaryKey(false);
        columnInfo.setColumn(name);
        columnInfo.setPropertyGetter(getter);
        columnInfo.setPropertySetter(setter);
        this.columnInfoList.add(columnInfo);
        return this;
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

    public EntityInfo<E> setJavaType(Class<E> javaType) {
        this.javaType = javaType;
        return this;
    }

    public void resolve(E entity) {
        for (ColumnInfo columnInfo : columnInfoList) {
            columnInfo.getPropertyValue(entity);
        }
    }

    @Override
    public EntityInfo<E> copy() {
        EntityInfo<E> info = new EntityInfo<>();
        info.javaType = javaType;
        info.driverName = driverName;
        info.table = table;
        info.primaryKey = primaryKey;
        info.charset = charset;
        info.engine = engine;
        List<ColumnInfo> list = new ArrayList<>();
        if (this.columnInfoList != null) {
            for (ColumnInfo columnInfo : this.columnInfoList) {
                list.add(columnInfo.copy());
            }
            info.columnInfoList = list;
        }
        return info;
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
