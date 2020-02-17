package com.truthbean.debbie.jdbc.entity;

import com.truthbean.debbie.reflection.ClassInfo;
import com.truthbean.debbie.jdbc.annotation.JdbcTransient;
import com.truthbean.debbie.jdbc.annotation.SqlEntity;
import com.truthbean.debbie.jdbc.column.ColumnInfo;
import com.truthbean.debbie.jdbc.column.JdbcColumnResolver;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 璩诗斌
 * @since 0.0.1
 */
public class EntityResolver {
    @SuppressWarnings("unchecked")
    public <E> EntityInfo<E> resolveEntity(E entity) {
        Class<E> entityClass = (Class<E>) entity.getClass();
        ClassInfo<E> classInfo = new ClassInfo<>(entityClass);
        var table = getTableName(classInfo);
        var columns = resolveClassInfoAndValue(classInfo, entity);
        var entityInfo = new EntityInfo<E>();
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
        var entityClass = classInfo.getClazz();
        var table = sqlEntity.table();
        if (table.isBlank()) {
            table = entityClass.getSimpleName().toLowerCase();
        }
        return table;
    }

    public static List<ColumnInfo> resolveClassInfo(ClassInfo<?> classInfo) {
        List<ColumnInfo> columns = new ArrayList<>();
        List<Field> fields = classInfo.getFields();
        if (!fields.isEmpty()) {
            fields.stream()
                    .filter(field -> field.getAnnotation(JdbcTransient.class) == null)
                    .forEach(field -> columns.add(JdbcColumnResolver.resolveField(field)));
        }
        return columns;
    }

    public static <E> List<ColumnInfo> resolveClassInfoAndValue(ClassInfo<?> classInfo, E entity) {
        List<ColumnInfo> columns = new ArrayList<>();
        var fields = classInfo.getFields();
        if (!fields.isEmpty()) {
            fields.stream()
                    .filter(field -> field.getAnnotation(JdbcTransient.class) == null)
                    .forEach(field -> columns.add(JdbcColumnResolver.resolveFieldAndValue(field, entity)));
        }
        return columns;
    }
}
