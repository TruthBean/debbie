package com.truthbean.debbie.jdbc.repository;

import com.truthbean.debbie.core.bean.BeanInitialization;
import com.truthbean.debbie.core.reflection.ClassInfo;
import com.truthbean.debbie.jdbc.annotation.SqlEntity;
import com.truthbean.debbie.jdbc.column.ColumnInfo;
import com.truthbean.debbie.jdbc.column.JdbcColumnResolver;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 璩诗斌
 * @since 0.0.1
 */
public class EntityResolver {
    private BeanInitialization initialization = new BeanInitialization();
    public <E> EntityInfo<E> resolveEntity(E entity) {
        var classInfo = initialization.getRegisterBean(entity.getClass());
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
        var classInfo = initialization.getRegisterBean(entityClass);
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

    public static List<ColumnInfo> resolveClassInfo(ClassInfo classInfo) {
        List<ColumnInfo> columns = new ArrayList<>();
        var fields = classInfo.getFields();
        if (!fields.isEmpty()) {
            fields.forEach(field -> columns.add(JdbcColumnResolver.resolveField(field)));
        }
        return columns;
    }

    public static <E> List<ColumnInfo> resolveClassInfoAndValue(ClassInfo classInfo, E entity) {
        List<ColumnInfo> columns = new ArrayList<>();
        var fields = classInfo.getFields();
        if (!fields.isEmpty()) {
            fields.forEach(field -> columns.add(JdbcColumnResolver.resolveFieldAndValue(field, entity)));
        }
        return columns;
    }
}
