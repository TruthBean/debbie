package com.truthbean.debbie.jdbc.repository;

import com.truthbean.debbie.core.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
        dynamicSql.append("USE ").append(database).append(" ");
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
        dynamicSql.append(" DATABASE ").append(database).append(" ");
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

    public DynamicSqlBuilder tableIfExists(String table, boolean ifExists) {
        dynamicSql.append("TABLE ");
        if (ifExists) {
            dynamicSql.append("IF EXISTS");
        }
        dynamicSql.append(" ").append(table).append(" ");
        return this;
    }

    public DynamicSqlBuilder tableIfNotExists(String table, boolean ifNotExists) {
        dynamicSql.append("TABLE ");
        if (ifNotExists) {
            dynamicSql.append("IF NOT EXISTS");
        }
        dynamicSql.append(" ").append(table).append(" ");
        return this;
    }

    public DynamicSqlBuilder table(String table) {
        dynamicSql.append("TABLE ").append(table).append(" ");
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

    public DynamicSqlBuilder autoIncrement(){
        dynamicSql.append(" AUTO_INCREMENT ");
        return this;
    }

    public DynamicSqlBuilder autoIncrement(int begin){
        dynamicSql.append(" AUTO_INCREMENT=").append(begin).append(" ");
        return this;
    }

    public DynamicSqlBuilder engine(String engine) {
        dynamicSql.append(" ENGINE=").append(engine).append(" ");
        return this;
    }

    public DynamicSqlBuilder defaultCharset(String charset) {
        dynamicSql.append(" DEFAULT CHARSET=").append(charset).append(" ");
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

    public DynamicSqlBuilder update() {
        dynamicSql.append("UPDATE ");
        return this;
    }

    public DynamicSqlBuilder update(String table) {
        dynamicSql.append("UPDATE ").append(table).append(" ");
        return this;
    }

    public DynamicSqlBuilder set() {
        dynamicSql.append(" SET ");
        return this;
    }

    public DynamicSqlBuilder set(String columns, String value) {
        dynamicSql.append(" SET ").append(columns).append("=").append(value).append(" ");
        return this;
    }

    public DynamicSqlBuilder set(String columns) {
        dynamicSql.append(" SET ").append(columns).append("= ? ");
        return this;
    }

    public DynamicSqlBuilder set(List<String> columns) {
        dynamicSql.append(" SET ");
        int size;
        if (columns != null && (size = columns.size()) > 0) {
            for (int i = 0; i < size - 1; i++) {
                var iColumn = columns.get(i);
                if (iColumn != null) {
                    dynamicSql.append("").append(iColumn).append(" = ?, ");
                }
            }
            var iColumn = columns.get(size - 1);
            if (iColumn != null) {
                dynamicSql.append("").append(iColumn).append(" = ? ");
            }
        }
        return this;
    }

    public DynamicSqlBuilder select() {
        dynamicSql.append(" SELECT ");
        return this;
    }

    public DynamicSqlBuilder select(List<String> columns) {
        dynamicSql.append(" SELECT ");
        this.columns(columns);
        return this;
    }

    public DynamicSqlBuilder select(String columns) {
        dynamicSql.append(" SELECT ");
        this.columns(List.of(columns));
        return this;
    }

    public DynamicSqlBuilder selectAll() {
        dynamicSql.append(" SELECT * ");
        return this;
    }

    public DynamicSqlBuilder distinct() {
        dynamicSql.append(" DISTINCT ");
        return this;
    }

    public DynamicSqlBuilder count() {
        dynamicSql.append(" COUNT(*) ");
        return this;
    }

    public DynamicSqlBuilder count(String column) {
        dynamicSql.append(" COUNT(").append(column).append(") ");
        return this;
    }

    public DynamicSqlBuilder columns(List<String> columns) {
        int size;
        if (columns != null && (size = columns.size()) > 0) {
            for (int i = 0; i < size - 1; i++) {
                var iColumn = columns.get(i);
                if (iColumn != null) {
                    dynamicSql.append(" ").append(iColumn).append(", ");
                }
            }
            var iColumn = columns.get(size - 1);
            if (iColumn != null) {
                dynamicSql.append(" ").append(iColumn).append(" ");
            }
        }
        return this;
    }

    public DynamicSqlBuilder columns(String... columns) {
        int size;
        if (columns != null && (size = columns.length) > 0) {
            for (int i = 0; i < size - 1; i++) {
                var iColumn = columns[i];
                if (iColumn != null) {
                    dynamicSql.append(" ").append(iColumn).append(", ");
                }
            }
            var iColumn = columns[size - 1];
            if (iColumn != null) {
                dynamicSql.append(" ").append(iColumn).append(" ");
            }
        }
        return this;
    }

    public DynamicSqlBuilder values(List<Object> values) {
        dynamicSql.append(" VALUES (");
        value(values);
        dynamicSql.append(") ");
        return this;
    }

    public DynamicSqlBuilder signs(int length) {
        List<String> signs = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            signs.set(i, "?");
        }
        return value(signs);
    }

    public DynamicSqlBuilder value(List<?> values) {
        int size = 0;
        if (values != null && (size = values.size()) > 0) {
            for (int i = 0; i < size - 1; i++) {
                var iValue = values.get(i);
                if (iValue != null) {
                    dynamicSql.append(iValue).append(", ");
                }
            }
            var iValue = values.get(size - 1);
            if (iValue != null) {
                dynamicSql.append(iValue).append(" ");
            }
        }
        return this;
    }

    public DynamicSqlBuilder value(Object... values) {
        int size;
        if (values != null && (size = values.length) > 0) {
            for (int i = 0; i < size - 1; i++) {
                var iValue = values[i];
                if (iValue != null) {
                    dynamicSql.append(iValue).append(", ");
                }
            }
            var iValue = values[size - 1];
            if (iValue != null) {
                dynamicSql.append(iValue).append(" ");
            }
        }
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

    public DynamicSqlBuilder eq(String column, Object value) {
        dynamicSql.append(column).append(" = ").append(value);
        return this;
    }

    public DynamicSqlBuilder extra(Object extra) {
        dynamicSql.append(extra);
        return this;
    }

    public DynamicSqlBuilder foreach(String open, String close, Collection collection, String separator) {
        if (collection != null && !collection.isEmpty()) {
            dynamicSql.append(open);
            dynamicSql.append(StringUtils.joining(collection, separator));
            dynamicSql.append(close);
        }
        return this;
    }

    public DynamicSqlBuilder limit(int offset, int limit) {
        dynamicSql.append(" LIMIT ").append(limit).append(" OFFSET ").append(offset).append(" ");
        return this;
    }

    public DynamicSqlBuilder limit(int limit) {
        dynamicSql.append(" LIMIT ").append(limit).append(" ");
        return this;
    }

    public DynamicSqlBuilder orderBy(String column) {
        dynamicSql.append(" ORDER BY ").append(column);
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