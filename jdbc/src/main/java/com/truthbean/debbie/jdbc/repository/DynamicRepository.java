/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.jdbc.repository;

import com.truthbean.debbie.jdbc.datasource.DataSourceDriverName;
import com.truthbean.debbie.jdbc.datasource.DriverConnection;
import com.truthbean.debbie.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * dynamic build sql
 *
 * @author TruthBean
 * @since 0.0.1
 */
public class DynamicRepository {
    private final StringBuilder dynamicSql = new StringBuilder();
    private DataSourceDriverName driverName;

    protected DynamicRepository(DataSourceDriverName driverName) {
        this.driverName = driverName;
    }

    private static final int INSERT = 1;
    public static final int UPDATE = 2;
    public static final int DELETE = 3;
    public static final int SELECT = 4;

    /**
     * 1: insert
     * 2: update
     * 3: delete
     * 4: select
     */
    private int action;
    private DriverConnection connection;
    private DynamicRepository sqlBuilder;
    private DynamicRepository(DynamicRepository sqlBuilder, DriverConnection connection, int action) {
        this.connection = connection;
        this.sqlBuilder = sqlBuilder;
        this.action = action;
    }

    public static DynamicRepository query(DriverConnection connection) {
        return new DynamicRepository(DynamicRepository.sql(connection.getDriverName()), connection, SELECT);
    }

    public static DynamicRepository modify(DriverConnection connection) {
        return new DynamicRepository(DynamicRepository.sql(connection.getDriverName()), connection, UPDATE);
    }

    public static DynamicRepository add(DriverConnection connection) {
        return new DynamicRepository(DynamicRepository.sql(connection.getDriverName()), connection, INSERT);
    }

    public static DynamicRepository remove(DriverConnection connection) {
        return new DynamicRepository(DynamicRepository.sql(connection.getDriverName()), connection, DELETE);
    }

    public static DynamicRepository sql(DataSourceDriverName driverName) {
        return new DynamicRepository(driverName);
    }

    public DynamicRepository show() {
        dynamicSql.append("SHOW ");
        return this;
    }

    public DynamicRepository databases() {
        dynamicSql.append(" DATABASES ");
        return this;
    }

    public DynamicRepository use(String database) {
        dynamicSql.append("USE ").append(database).append(" ");
        return this;
    }

    public DynamicRepository tables() {
        dynamicSql.append(" TABLES ");
        return this;
    }

    public DynamicRepository create() {
        dynamicSql.append("CREATE ");
        return this;
    }

    public DynamicRepository database(String database) {
        dynamicSql.append("DATABASE ").append(database).append(" ");
        return this;
    }

    public DynamicRepository desc() {
        dynamicSql.append(" DESC ");
        return this;
    }

    public DynamicRepository drop() {
        dynamicSql.append("DROP ");
        return this;
    }

    public DynamicRepository tableIfExists(String table, boolean ifExists) {
        dynamicSql.append("TABLE ");
        if (ifExists) {
            dynamicSql.append("IF EXISTS");
        }
        dynamicSql.append(" ").append(table).append(" ");
        return this;
    }

    public DynamicRepository tableIfNotExists(String table, boolean ifNotExists) {
        dynamicSql.append("TABLE ");
        if (ifNotExists) {
            dynamicSql.append("IF NOT EXISTS");
        }
        dynamicSql.append(" ").append(table).append(" ");
        return this;
    }

    public DynamicRepository table(String table) {
        dynamicSql.append("TABLE ").append(table).append(" ");
        return this;
    }

    public DynamicRepository leftParenthesis() {
        dynamicSql.append("( ");
        return this;
    }

    public DynamicRepository rightParenthesis() {
        dynamicSql.append(" )");
        return this;
    }

    public DynamicRepository defaultValue(String defaultValue) {
        dynamicSql.append(" DEFAULT ").append(defaultValue).append(" ");
        return this;
    }

    public DynamicRepository comment(String comment) {
        if (driverName != DataSourceDriverName.sqlite)
            dynamicSql.append(" COMMENT '").append(comment).append("' ");
        return this;
    }

    public DynamicRepository nullSql() {
        dynamicSql.append(" NULL ");
        return this;
    }

    public DynamicRepository primaryKey() {
        dynamicSql.append(" PRIMARY KEY ");
        return this;
    }

    public DynamicRepository autoIncrement() {
        if (driverName == DataSourceDriverName.sqlite) {
            dynamicSql.append(" AUTOINCREMENT ");
        } else
            dynamicSql.append(" AUTO_INCREMENT ");
        return this;
    }

    public DynamicRepository autoIncrement(int begin) {
        dynamicSql.append(" AUTO_INCREMENT=").append(begin).append(" ");
        return this;
    }

    public DynamicRepository engine(String engine) {
        if (driverName != DataSourceDriverName.sqlite)
            dynamicSql.append(" ENGINE=").append(engine).append(" ");
        return this;
    }

    public DynamicRepository defaultCharset(String charset) {
        if (driverName != DataSourceDriverName.sqlite)
            dynamicSql.append(" DEFAULT CHARSET=").append(charset).append(" ");
        return this;
    }

    public DynamicRepository unique() {
        dynamicSql.append(" UNIQUE ");
        return this;
    }

    public DynamicRepository ifSql() {
        dynamicSql.append(" IF ");
        return this;
    }

    public DynamicRepository not() {
        dynamicSql.append(" NOT ");
        return this;
    }

    public DynamicRepository exists() {
        dynamicSql.append(" EXISTS ");
        return this;
    }

    public DynamicRepository tinyint() {
        dynamicSql.append(" TINYINT ");
        return this;
    }

    public DynamicRepository delete() {
        dynamicSql.append("DELETE ");
        return this;
    }

    public DynamicRepository insert() {
        dynamicSql.append("INSERT INTO ");
        return this;
    }

    public DynamicRepository update() {
        dynamicSql.append("UPDATE ");
        return this;
    }

    public DynamicRepository update(String table) {
        dynamicSql.append("UPDATE ").append(table).append(" ");
        return this;
    }

    public DynamicRepository set() {
        dynamicSql.append(" SET ");
        return this;
    }

    public DynamicRepository set(String columns, String value) {
        dynamicSql.append(" SET ").append(columns).append("=").append(value).append(" ");
        return this;
    }

    public DynamicRepository set(String columns) {
        dynamicSql.append(" SET ").append(columns).append("= ? ");
        return this;
    }

    public DynamicRepository set(List<String> columns) {
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

    public DynamicRepository select() {
        dynamicSql.append(" SELECT ");
        return this;
    }

    public DynamicRepository select(List<String> columns) {
        dynamicSql.append(" SELECT ");
        this.joinWith(",", columns);
        return this;
    }

    public DynamicRepository select(String columns) {
        dynamicSql.append(" SELECT ").append(columns).append(" ");
        return this;
    }

    public DynamicRepository select(String... columns) {
        dynamicSql.append(" SELECT ");
        this.joinWith(",", columns);
        return this;
    }

    public DynamicRepository selectAll() {
        dynamicSql.append(" SELECT * ");
        return this;
    }

    public DynamicRepository distinct() {
        dynamicSql.append(" DISTINCT ");
        return this;
    }

    public DynamicRepository count() {
        dynamicSql.append(" COUNT(*) ");
        return this;
    }

    public DynamicRepository count(String column) {
        dynamicSql.append(" COUNT(").append(column).append(") ");
        return this;
    }

    public DynamicRepository joinWith(String split, List<String> columns) {
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

    public DynamicRepository joinWith(String split, String... columns) {
        int size;
        if (columns != null && (size = columns.length) > 0) {
            for (int i = 0; i < size - 1; i++) {
                var iColumn = columns[i];
                if (iColumn != null) {
                    dynamicSql.append(" ").append(iColumn).append(split).append(" ");
                }
            }
            var iColumn = columns[size - 1];
            if (iColumn != null) {
                dynamicSql.append(" ").append(iColumn).append(" ");
            }
        }
        return this;
    }

    public DynamicRepository values(List<Object> values) {
        dynamicSql.append(" VALUES (");
        value(values);
        dynamicSql.append(") ");
        return this;
    }

    public DynamicRepository signs(int length) {
        List<String> signs = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            signs.set(i, "?");
        }
        return value(signs);
    }

    public DynamicRepository value(List<?> values) {
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

    public DynamicRepository value(Object... values) {
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

    public DynamicRepository from(String table) {
        if (table != null) {
            dynamicSql.append(" FROM ").append(table).append(" ");
        }
        return this;
    }

    public DynamicRepository where() {
        dynamicSql.append(" WHERE ");
        return this;
    }

    public DynamicRepository and(String condition) {
        dynamicSql.append(" AND ").append(condition);
        return this;
    }

    public DynamicRepository and() {
        dynamicSql.append(" AND ");
        return this;
    }

    public DynamicRepository or(String condition) {
        dynamicSql.append(" OR ").append(condition);
        return this;
    }

    public DynamicRepository or() {
        dynamicSql.append(" OR ");
        return this;
    }

    public DynamicRepository exist(String subSql) {
        dynamicSql.append(" EXISTS( ").append(subSql).append(" ) ");
        return this;
    }

    public DynamicRepository eq(String column, Object value) {
        dynamicSql.append(column).append(" = ").append(value);
        return this;
    }

    public <T> DynamicRepository in(String column, int inValueSize) {
        List<String> s = new ArrayList<>();
        for (int i = 0; i < inValueSize; i++) {
            s.add("?");
        }

        dynamicSql.append(column).append(" IN ( ").append(StringUtils.joining(s)).append(" )");
        return this;
    }

    public DynamicRepository extra(Object extra) {
        dynamicSql.append(extra);
        return this;
    }

    public DynamicRepository foreach(String open, String close, Collection<?> collection, String separator) {
        if (collection != null && !collection.isEmpty()) {
            dynamicSql.append(open);
            dynamicSql.append(StringUtils.joining(collection, separator));
            dynamicSql.append(close);
        }
        return this;
    }

    public DynamicRepository left() {
        dynamicSql.append(" LEFT ");
        return this;
    }

    public DynamicRepository right() {
        dynamicSql.append(" RIGHT ");
        return this;
    }

    public DynamicRepository join() {
        dynamicSql.append(" JOIN ");
        return this;
    }

    public DynamicRepository join(String table) {
        dynamicSql.append(" JOIN ").append(table).append(" ");
        return this;
    }

    public DynamicRepository on() {
        dynamicSql.append(" ON ");
        return this;
    }

    public DynamicRepository limit(int offset, int limit) {
        dynamicSql.append(" LIMIT ").append(limit).append(" OFFSET ").append(offset).append(" ");
        return this;
    }

    public DynamicRepository limit(int limit) {
        dynamicSql.append(" LIMIT ").append(limit).append(" ");
        return this;
    }

    public DynamicRepository orderBy(String column) {
        dynamicSql.append(" ORDER BY ").append(column);
        return this;
    }

    public String builder() {
        return dynamicSql.toString();
    }

    public <T> List<T> result(Class<T> resultClass) {
        RepositoryHandler repositoryHandler = new RepositoryHandler();
        repositoryHandler.setDriverName(driverName);
        if (action == SELECT) {
            return repositoryHandler.query(connection.getConnection(), dynamicSql.toString(), resultClass);
        }
        return new ArrayList<>();
    }

    @Override
    public String toString() {
        return dynamicSql.toString();
    }
}