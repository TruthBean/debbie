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

    public DynamicSqlBuilder use(String database) {
        dynamicSql.append("USE `").append(database).append("` ");
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
        dynamicSql.append(" DATABASE `").append(database).append("` ");
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

    public DynamicSqlBuilder table(String table, boolean ifNotExists) {
        dynamicSql.append("TABLE ");
        if (ifNotExists) {
            dynamicSql.append("IF NOT EXISTS");
        }
        dynamicSql.append(" `").append(table).append("` ");
        return this;
    }

    public DynamicSqlBuilder leftParenthesis(){
        dynamicSql.append(" ( ");
        return this;
    }

    public DynamicSqlBuilder rightParenthesis(){
        dynamicSql.append(" ) ");
        return this;
    }

    public DynamicSqlBuilder defaultValue(String defaultValue) {
        dynamicSql.append(" DEFAULT ").append(defaultValue).append(" ");
        return this;
    }

    public DynamicSqlBuilder comment(String comment) {
        dynamicSql.append(" COMMENT '").append(comment).append("' ");
        return this;
    }

    public DynamicSqlBuilder nullSql() {
        dynamicSql.append(" NULL ");
        return this;
    }

    public DynamicSqlBuilder primaryKey() {
        dynamicSql.append(" PRIMARY KEY ");
        return this;
    }

    public DynamicSqlBuilder unique() {
        dynamicSql.append(" UNIQUE ");
        return this;
    }

    public DynamicSqlBuilder ifSql() {
        dynamicSql.append(" IF ");
        return this;
    }

    public DynamicSqlBuilder not() {
        dynamicSql.append(" NOT ");
        return this;
    }

    public DynamicSqlBuilder exists() {
        dynamicSql.append(" EXISTS ");
        return this;
    }

    public DynamicSqlBuilder tinyint() {
        dynamicSql.append(" TINYINT ");
        return this;
    }

    public DynamicSqlBuilder delete() {
        dynamicSql.append("DELETE ");
        return this;
    }

    public DynamicSqlBuilder insert() {
        dynamicSql.append("INSERT INTO ");
        return this;
    }

    public DynamicSqlBuilder select(String... columns) {
        dynamicSql.append(" SELECT ");
        if (columns != null && columns.length > 0) {
            int size = columns.length;
            for (int i = 0; i < size - 1; i++) {
                if (columns[i] != null) {
                    dynamicSql.append("`").append(columns[i]).append("`, ");
                }
            }
            if (columns[size - 1] != null) {
                dynamicSql.append("`").append(columns[size - 1]).append("` ");
            }
        }
        return this;
    }

    public DynamicSqlBuilder from(String table) {
        if (table != null) {
            dynamicSql.append(" FROM `").append(table).append("` ");
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
        dynamicSql.append(" EXIST( `").append(column).append("` ) ");
        return this;
    }

    public DynamicSqlBuilder eq(String condition1, String condition2) {
        dynamicSql.append(condition1).append(" = ").append(condition2);
        return this;
    }

    public DynamicSqlBuilder extra(String extra) {
        dynamicSql.append(extra);
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