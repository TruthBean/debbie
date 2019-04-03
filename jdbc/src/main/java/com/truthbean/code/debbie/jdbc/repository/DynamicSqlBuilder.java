package com.truthbean.code.debbie.jdbc.repository;

import com.truthbean.code.debbie.core.util.CollectionUtils;

/**
 * dynamic build sql
 * @author TruthBean
 * @since 0.0.1
 */
public class DynamicSqlBuilder {
    private StringBuilder dynamicSql = new StringBuilder();

    public static DynamicSqlBuilder sql() {
        return new DynamicSqlBuilder();
    }

    public DynamicSqlBuilder show() {
        dynamicSql.append("SHOW ");
        return this;
    }

    public DynamicSqlBuilder databases() {
        dynamicSql.append(" DATABASES ");
        return this;
    }

    public DynamicSqlBuilder tables() {
        dynamicSql.append(" TABLES ");
        return this;
    }

    public DynamicSqlBuilder create() {
        dynamicSql.append("CREATE ");
        return this;
    }

    public DynamicSqlBuilder database(String database) {
        dynamicSql.append(" DATABASE ").append(database);
        return this;
    }

    public DynamicSqlBuilder desc() {
        dynamicSql.append("DESC ");
        return this;
    }

    public DynamicSqlBuilder drop() {
        dynamicSql.append("DROP ");
        return this;
    }

    public DynamicSqlBuilder delete() {
        dynamicSql.append("DELETE");
        return this;
    }

    public DynamicSqlBuilder insert() {
        dynamicSql.append("INSERT INTO ");
        return this;
    }

    public DynamicSqlBuilder select(String... columns) {
        CollectionUtils.splitArray(columns, ", ", dynamicSql);
        return this;
    }

    public DynamicSqlBuilder from(String table) {
        if (table != null) {
            dynamicSql.append(" FROM ").append(table).append(" ");
        }
        return this;
    }

    public DynamicSqlBuilder where() {
        dynamicSql.append(" WHERE ");
        return this;
    }

    public DynamicSqlBuilder and(String condition) {
        dynamicSql.append(" AND ").append(condition);
        return this;
    }

    public DynamicSqlBuilder or(String condition) {
        dynamicSql.append(" OR ").append(condition);
        return this;
    }

    public DynamicSqlBuilder exist(String column) {
        dynamicSql.append(" EXIST( ").append(column).append(" ) ");
        return this;
    }

    public DynamicSqlBuilder eq(String condition1, String condition2) {
        dynamicSql.append(condition1).append(" = ").append(condition2);
        return this;
    }

    public String builder() {
        return dynamicSql.toString();
    }

    @Override
    public String toString() {
        return dynamicSql.toString();
    }
}