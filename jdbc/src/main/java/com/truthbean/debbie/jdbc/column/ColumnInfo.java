/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.jdbc.column;

import com.truthbean.debbie.jdbc.column.type.ColumnTypeHandler;
import com.truthbean.debbie.jdbc.entity.EntityPropertyGetter;
import com.truthbean.debbie.jdbc.entity.EntityPropertySetter;
import com.truthbean.debbie.lang.Copyable;
import com.truthbean.transformer.DataTransformer;

import java.sql.Date;
import java.sql.JDBCType;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Metadata describing a single database column and its mapping to a
 * Java entity property, including the column name, JDBC type, nullability,
 * primary-key flags, getter/setter references, and an optional
 * {@link DataTransformer} for value conversion.
 *
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-03-19 11:18.
 */
public class ColumnInfo implements Copyable<ColumnInfo> {
    /** the Java property name */
    private String property;

    /** the database column name */
    private String column;
    /** the column default value expression */
    private String columnDefaultValue;

    /** the database data type name */
    private String dataType;
    /** the JDBC type */
    private JDBCType jdbcType;

    /** column comment */
    private String comment;

    /** the full column type definition */
    private String columnType;

    /** maximum character length */
    private int charMaxLength;

    /** whether the column allows null values */
    private Boolean nullable;

    /** numeric scale */
    private int scale;

    /** numeric precision */
    private int precision;

    /** the Java class type name */
    private String classType;

    /** the option/enum type name */
    private String optionType;

    /** the Java class mapped to this column */
    private Class<?> javaClass;

    /** the current column value (may be populated from an entity) */
    private Object value;
    /** getter to extract the property value from an entity */
    @SuppressWarnings("rawtypes")
    private EntityPropertyGetter propertyGetter;
    /** setter to write a value onto an entity property */
    @SuppressWarnings("rawtypes")
    private EntityPropertySetter propertySetter;
    /** optional transformer applied before setting values */
    @SuppressWarnings("rawtypes")
    private DataTransformer valueTransformer;

    /** whether this column is part of the primary key */
    private boolean isPrimaryKey;
    /** the primary-key generation strategy */
    private PrimaryKeyType primaryKeyType;

    /** whether this column has a unique constraint */
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

    /**
     * Returns the column value: the cached {@link #value} if set, or
     * the value read from the given entity via the property getter.
     *
     * @param entity the entity to read from (may be {@code null})
     * @return the column value, or {@code null}
     */
    @SuppressWarnings("unchecked")
    public Object getValue(Object entity) {
        if (value != null) {
            return value;
        }
        if (entity != null && propertyGetter != null) {
            return propertyGetter.get(entity);
        }
        return null;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @SuppressWarnings("unchecked")
    public <E, P> EntityPropertyGetter<E, P> getPropertyGetter() {
        return propertyGetter;
    }

    public <E, P> void setPropertyGetter(EntityPropertyGetter<E, P> propertyGetter) {
        this.propertyGetter = propertyGetter;
    }

    /** Reads the property value from the given entity and caches it in {@link #value}. */
    @SuppressWarnings("unchecked")
    public void getPropertyValue(Object entity) {
        if (propertyGetter != null) {
            this.value = propertyGetter.get(entity);
        }
    }

    @SuppressWarnings("unchecked")
    public <E, P> EntityPropertySetter<E, P> getPropertySetter() {
        return propertySetter;
    }

    public <E, P> void setPropertySetter(EntityPropertySetter<E, P> propertySetter) {
        this.propertySetter = propertySetter;
    }

    /**
     * Sets the property value on the given entity, applying the
     * {@link DataTransformer} and type conversion (e.g. {@link Date},
     * {@link Time}, {@link Timestamp}, {@link LocalDateTime},
     * {@code Integer}↔{@code Long}) before invoking the setter.
     *
     * @param entity the target entity
     * @param value  the raw value to set
     * @param <E>    the entity type
     * @param <P>    the property type
     */
    @SuppressWarnings("unchecked")
    public <E, P> void setPropertyValue(E entity, P value) {
        if (propertySetter != null) {
            if (valueTransformer != null && valueTransformer != DataTransformer.NO) {
                value = (P) valueTransformer.transform(value);
            }
            if (javaClass != null) {
                if (value instanceof Date) {
                    Object val = ColumnTypeHandler.transformDate((Date) value, javaClass);
                    if (val != null) {
                        this.propertySetter.set(entity, val);
                    }
                } else if (value instanceof Time) {
                    Object val = ColumnTypeHandler.transformTime((Time) value, javaClass);
                    if (val != null) {
                        this.propertySetter.set(entity, val);
                    }
                } else if (value instanceof Timestamp) {
                    Object val = ColumnTypeHandler.transformTimestamp((Timestamp) value, javaClass);
                    if (val != null) {
                        this.propertySetter.set(entity, val);
                    }
                } else if (value instanceof String && javaClass == LocalDateTime.class) {
                    this.propertySetter.set(entity, LocalDateTime.parse((String) value));
                } else if (value instanceof Integer && javaClass == Long.class) {
                    this.propertySetter.set(entity, ((Integer) value).longValue());
                } else if (value instanceof Long && javaClass == Integer.class) {
                    this.propertySetter.set(entity, ((Long) value).intValue());
                } else {
                    this.propertySetter.set(entity, value);
                }
            } else {
                this.propertySetter.set(entity, value);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <S, T> DataTransformer<S, T> getValueTransformer() {
        return valueTransformer;
    }

    public <S, T> void setValueTransformer(DataTransformer<S, T> valueTransformer) {
        this.valueTransformer = valueTransformer;
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

    /**
     * Creates a shallow copy of this column info, clearing the cached
     * {@link #value}. Getter, setter and transformer references are
     * shared (not deep-copied).
     *
     * @return a new {@link ColumnInfo} with copied metadata
     */
    @Override
    public ColumnInfo copy() {
        ColumnInfo info = new ColumnInfo();
        info.property = property;
        info.propertyGetter = propertyGetter;
        info.propertySetter = propertySetter;
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
        info.value = null;
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
                + "\"javaClass\":" + javaClass + "," + "\"value\":" + value + ","
                + "\"primaryKeyType\":" + primaryKeyType + ","  + "\"columnDefaultValue\":" + columnDefaultValue + "}";
    }
}
