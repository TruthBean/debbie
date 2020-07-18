/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.jdbc.entity;

import com.truthbean.debbie.jdbc.annotation.JdbcTransient;
import com.truthbean.debbie.jdbc.annotation.SqlColumn;
import com.truthbean.debbie.jdbc.annotation.SqlEntity;
import com.truthbean.debbie.jdbc.column.ColumnInfo;
import com.truthbean.debbie.jdbc.column.JdbcColumnResolver;
import com.truthbean.debbie.jdbc.datasource.DataSourceDriverName;
import com.truthbean.debbie.reflection.ClassInfo;
import com.truthbean.debbie.reflection.FieldInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 璩诗斌
 * @since 0.0.1
 */
public class EntityResolver {
    @SuppressWarnings("unchecked")
    public <E> EntityInfo<E> resolveEntity(DataSourceDriverName driverName, E entity) {
        Class<E> entityClass = (Class<E>) entity.getClass();
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
        return entityInfo;
    }

    public <E> EntityInfo<E> resolveEntityClass(Class<E> entityClass) {
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
        return entityInfo;
    }

    public static String getTableName(ClassInfo classInfo) {
        SqlEntity sqlEntity = (SqlEntity) classInfo.getClassAnnotations().get(SqlEntity.class);
        if (sqlEntity == null) {
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
}
