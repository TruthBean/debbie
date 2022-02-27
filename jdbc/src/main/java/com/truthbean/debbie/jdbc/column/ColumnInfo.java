/**
 * Copyright (c) 2022 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.jdbc.column;

import com.truthbean.debbie.jdbc.entity.EntityPropertyGetter;
import com.truthbean.debbie.jdbc.entity.EntityPropertySetter;
import com.truthbean.debbie.lang.Copyable;

import java.sql.JDBCType;
import java.util.function.Consumer;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-03-19 11:18.
 */
public class ColumnInfo implements Copyable<ColumnInfo> {
    private String property;

    private String column;
    private String columnDefaultValue;

    private String dataType;
    private JDBCType jdbcType;

    private String comment;

    private String columnType;

    private int charMaxLength;

    private Boolean nullable;

    private int scale;

    private int precision;

    private String classType;

    private String optionType;

    private Class<?> javaClass;

    private Object value;
    private EntityPropertyGetter propertyGetter;
    private EntityPropertySetter propertySetter;

    private boolean isPrimaryKey;
    private PrimaryKeyType primaryKeyType;

    private boolean unique;

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public int getCharMaxLength() {
        return charMaxLength;
    }

    public void setCharMaxLength(int charMaxLength) {
        this.charMaxLength = charMaxLength;
    }

    public Boolean isNullable() {
        return nullable;
    }

    public void setNullable(Boolean nullable) {
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

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public <E, P> EntityPropertyGetter<E, P> getPropertyGetter() {
        return propertyGetter;
    }

    public <E, P> void setPropertyGetter(EntityPropertyGetter<E, P> propertyGetter) {
        this.propertyGetter = propertyGetter;
    }

    public void getPropertyValue(Object entity) {
        this.value = propertyGetter.get(entity);
    }

    public <E, P> EntityPropertySetter<E, P> getPropertySetter() {
        return propertySetter;
    }

    public <E, P> void setPropertySetter(EntityPropertySetter<E, P> propertySetter) {
        this.propertySetter = propertySetter;
    }

    public <E, P> void setPropertyValue(E entity, P value) {
        this.propertySetter.set(entity, value);
    }

    public Class<?> getJavaClass() {
        return javaClass;
    }

    public void setJavaClass(Class<?> javaClass) {
        this.javaClass = javaClass;
    }

    public JDBCType getJdbcType() {
        return jdbcType;
    }

    public void setJdbcType(JDBCType jdbcType) {
        this.jdbcType = jdbcType;
    }

    public String getColumnDefaultValue() {
        return columnDefaultValue;
    }

    public void setColumnDefaultValue(String defaultValue) {
        this.columnDefaultValue = defaultValue;
    }

    public Boolean getNullable() {
        return nullable;
    }

    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        isPrimaryKey = primaryKey;
    }

    public PrimaryKeyType getPrimaryKeyType() {
        return primaryKeyType;
    }

    public void setPrimaryKeyType(PrimaryKeyType primaryKeyType) {
        this.primaryKeyType = primaryKeyType;
    }

    public boolean isUnique() {
        return unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }

    @Override
    public ColumnInfo copy() {
        ColumnInfo info = new ColumnInfo();
        info.property = property;
        info.column = column;
        info.columnDefaultValue = columnDefaultValue;
        info.dataType = dataType;
        info.jdbcType = jdbcType;
        info.comment = comment;
        info.columnType = columnType;
        info.charMaxLength = charMaxLength;
        info.nullable = nullable;
        info.scale = scale;
        info.precision = precision;
        info.classType = classType;
        info.optionType = optionType;
        info.javaClass = javaClass;
        info.value = value;
        info.isPrimaryKey = isPrimaryKey;
        info.primaryKeyType = primaryKeyType;
        info.unique = unique;
        return info;
    }

    @Override
    public String toString() {
        return "{" + "\"propertyName\":\"" + getProperty() + "\"" + "," + "\"columnName\":\"" + getColumn() + "\"" + ","
                + "\"dataType\":\"" + dataType + "\"" + "," + "\"jdbcType\":" + jdbcType + ","
                + "\"comment\":\"" + comment + "\"" + "," + "\"columnType\":\"" + columnType + "\"" + ","
                + "\"charMaxLength\":\"" + charMaxLength + "\"" + "," + "\"nullable\":" + nullable + ","
                + "\"scale\":" + scale + "," + "\"precision\":" + precision + ","
                + "\"classType\":\"" + classType + "\"" + "," + "\"optionType\":\"" + optionType + "\"" + ","
                + "\"javaClass\":" + javaClass + "," + "\"VALUE\":" + value + ","
                + "\"primaryKeyType\":" + primaryKeyType + ","  + "\"columnDefaultValue\":" + columnDefaultValue + "}";
    }
}
