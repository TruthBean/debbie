/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.jdbc.entity;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.annotation.AnnotationInfo;
import com.truthbean.debbie.jdbc.annotation.JdbcTransient;
import com.truthbean.debbie.jdbc.annotation.SqlColumn;
import com.truthbean.debbie.jdbc.annotation.SqlEntity;
import com.truthbean.debbie.jdbc.column.ColumnInfo;
import com.truthbean.debbie.jdbc.column.JdbcColumnResolver;
import com.truthbean.debbie.jdbc.datasource.DataSourceDriverName;
import com.truthbean.debbie.jdbc.repository.MoreRowException;
import com.truthbean.debbie.lang.Callback;
import com.truthbean.debbie.reflection.ClassInfo;
import com.truthbean.debbie.reflection.FieldInfo;
import com.truthbean.debbie.reflection.ReflectionHelper;
import com.truthbean.debbie.reflection.TypeHelper;
import com.truthbean.transformer.TransformerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 璩诗斌
 * @since 0.0.1
 */
public class EntityResolver {

    private final Map<Class<?>, ResultMap<?>> resultMapMap = new ConcurrentHashMap<>();
    private final Map<Class<?>, EntityInfo<?>> entityInfoMap = new ConcurrentHashMap<>();

    private static final EntityResolver entityResolver = new EntityResolver();
    private EntityResolver() {
    }

    public static EntityResolver getInstance() {
        return entityResolver;
    }

    public void addEntityInfo(EntityInfo<?> entityInfo) {
        entityInfoMap.put(entityInfo.getJavaType(), entityInfo);
    }

    public void addResultMap(ResultMap<?> resultMap) {
        resultMapMap.put(resultMap.getJavaType(), resultMap);
    }

    @SuppressWarnings("unchecked")
    public <T> ResultMap<T> getResultMap(Class<?> resultMapType) {
        if (resultMapMap.containsKey(resultMapType)) {
            var resultMap = (ResultMap<T>) resultMapMap.get(resultMapType);
            if (resultMap != null) {
                return resultMap.copy();
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T> T resolve(Class<T> clazz, List<ColumnInfo> row) {
        if (clazz == Map.class) {
            Map<String, Object> map = new HashMap<>();
            for (ColumnInfo columnInfo : row) {
                map.put(columnInfo.getColumn(), columnInfo.getValue());
            }
            return (T) map;
        }
        if (row.size() == 1) {
            ColumnInfo data = row.get(0);
            return transform(data, clazz, arg -> {
                List<Field> declaredFields = ReflectionHelper.getDeclaredFields(clazz);
                return transformer(row, declaredFields, clazz);
            });
        } else {
            ResultMap<T> resultMap = this.getResultMap(clazz);
            if (resultMap != null) {
                resultMap.addResult(row);
                return resultMap.toEntity();
            }
            List<Field> declaredFields = ReflectionHelper.getDeclaredFields(clazz);
            return transformer(row, declaredFields, clazz);
        }

    }

    @SuppressWarnings("unchecked")
    private <T> T transform(ColumnInfo data, Class<T> clazz, Callback<T> others) {
        if (TypeHelper.isBaseType(clazz)) {
            if (data.getJavaClass() == clazz) {
                return clazz.cast(data.getValue());
            } else {
                Class<?> type = clazz;
                if (TypeHelper.isRawBaseType(type)) {
                    type = TypeHelper.getWrapperClass(type);
                }
                return (T) TransformerFactory.transform(data.getValue(), type);
            }
        } else if (TypeHelper.isTimeType(clazz)) {
            Object value = data.getValue();
            if (value instanceof Timestamp timestamp) {
                return TransformerFactory.transform(timestamp, clazz);
            }
        } else {
            return others.call();
        }
        return null;
    }

    private <T> T transformer(List<ColumnInfo> map, List<Field> declaredFields, Class<T> clazz) {
        T instance = ReflectionHelper.newInstance(clazz);
        if (instance == null) {
            throw new NullPointerException("Class(" + clazz + ") reflect to new instance null");
        }
        // todo 自定义map to T
        SqlColumn column;
        for (var field : declaredFields) {
            if (field.getAnnotation(JdbcTransient.class) != null) {
                continue;
            }

            column = field.getAnnotation(SqlColumn.class);
            var columnName = EntityResolver.getColumnName(column, field.getName());
            for (var entry : map) {
                if (columnName.equals(entry.getColumn())) {
                    Class<?> type = field.getType();
                    Class<?> javaClass = entry.getJavaClass();
                    Object value = entry.getValue();
                    // todo check
                    if (javaClass != type) {
                        try {
                            value = TransformerFactory.transform(entry.getValue(), type);
                        } catch (Exception e) {
                            LOGGER.error(() -> "column (" + columnName + ") " + entry.getValue() + " (" + entry.getJavaClass() + ") transform " + type + " error \n", e);
                        }
                    }
                    ReflectionHelper.invokeSetMethod(instance, field, value);
                    break;
                }
            }
        }
        return instance;
    }

    @SuppressWarnings("unchecked")
    public <E> EntityInfo<E> resolveEntity(DataSourceDriverName driverName, E entity) {
        Class<E> entityClass = (Class<E>) entity.getClass();
        if (entityInfoMap.containsKey(entityClass)) {
            EntityInfo<E> entityInfo = (EntityInfo<E>) entityInfoMap.get(entityClass);
            entityInfo.copy().resolve(entity);
            return entityInfo;
        }
        ClassInfo<E> classInfo = new ClassInfo<>(entityClass);
        var table = getTableName(classInfo);
        var columns = resolveClassInfoAndValue(classInfo, entity);
        var entityInfo = new EntityInfo<E>();
        entityInfo.setDriverName(driverName);
        entityInfo.setTable(table);
        entityInfo.setColumnInfoList(columns);
        for (var column: columns) {
            if (column.isPrimaryKey()) {
                entityInfo.setPrimaryKey(column);
                break;
            }
        }
        entityInfoMap.put(entityClass, entityInfo);
        return entityInfo;
    }

    @SuppressWarnings("unchecked")
    public <E> EntityInfo<E> resolveEntityClass(Class<E> entityClass) {
        if (entityInfoMap.containsKey(entityClass)) {
            return (EntityInfo<E>) entityInfoMap.get(entityClass).copy();
        }
        var classInfo = new ClassInfo<>(entityClass);
        var table = getTableName(classInfo);
        var columns = resolveClassInfo(classInfo);
        var entityInfo = new EntityInfo<E>();
        entityInfo.setTable(table);
        entityInfo.setColumnInfoList(columns);
        for (var column: columns) {
            if (column.isPrimaryKey()) {
                entityInfo.setPrimaryKey(column);
                break;
            }
        }
        entityInfo.setJavaType(entityClass);
        entityInfoMap.put(entityClass, entityInfo);
        return entityInfo.copy();
    }

    public static String getTableName(ClassInfo<?> classInfo) {
        Map<Class<? extends Annotation>, AnnotationInfo> classAnnotations = classInfo.getClassAnnotations();
        SqlEntity sqlEntity;
        if (classAnnotations.containsKey(SqlEntity.class)) {
            AnnotationInfo annotationInfo = classAnnotations.get(SqlEntity.class);
            sqlEntity = (SqlEntity) annotationInfo.getOrigin();
            if (sqlEntity == null) {
                throw new SqlEntityNullException(classInfo.getClazz().getName() + " has no @SqlEntity !");
            }
        } else {
            throw new SqlEntityNullException(classInfo.getClazz().getName() + " has no @SqlEntity !");
        }
        var entityClass = classInfo.getClazz();
        return getTableName(sqlEntity, entityClass);
    }

    public static String getTableName(SqlEntity entity, Class<?> entityType) {
        var table = entity.value();
        if (table.isBlank()) {
            table = entity.table();
        }
        if (table.isBlank()) {
            table = entityType.getSimpleName().toLowerCase();
        }
        return table;
    }

    public static String getColumnName(SqlColumn column, String fieldName) {
        if (column == null) {
            // 没有SqlColumn默认使用field
            return fieldName;
        }
        var columnName = column.value();
        if (columnName.isBlank()) {
            columnName = column.name();
        }
        if (columnName.isBlank()) {
            columnName = fieldName;
        }
        return columnName;
    }

    public static List<ColumnInfo> resolveClassInfo(ClassInfo<?> classInfo) {
        List<ColumnInfo> columns = new ArrayList<>();
        List<FieldInfo> fields = classInfo.getFields();
        if (!fields.isEmpty()) {
            fields.stream()
                    .filter(field -> field.getAnnotation(JdbcTransient.class) == null)
                    .forEach(field -> columns.add(JdbcColumnResolver.resolveField(field.getField())));
        }
        return columns;
    }

    public static <E> List<ColumnInfo> resolveClassInfoAndValue(ClassInfo<?> classInfo, E entity) {
        List<ColumnInfo> columns = new ArrayList<>();
        var fields = classInfo.getFields();
        if (!fields.isEmpty()) {
            fields.stream()
                    .filter(field -> field.getAnnotation(JdbcTransient.class) == null)
                    .forEach(field -> columns.add(JdbcColumnResolver.resolveFieldAndValue(field.getField(), entity)));
        }
        return columns;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(EntityResolver.class);
}
