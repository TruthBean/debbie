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
import com.truthbean.debbie.jdbc.transaction.TransactionException;
import com.truthbean.debbie.jdbc.transaction.TransactionInfo;
import com.truthbean.debbie.jdbc.transaction.TransactionManager;
import com.truthbean.debbie.util.StringUtils;

import java.util.*;

import static com.truthbean.debbie.jdbc.repository.SqlKeywords.*;

/**
 * dynamic build sql
 *
 * @author TruthBean
 * @since 0.0.1
 */
public class DynamicRepository {
    private final StringBuilder dynamicSql = new StringBuilder();
    private final List<Object> args = new ArrayList<>();
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
    private DynamicRepository(DriverConnection connection, int action) {
        this.connection = connection;
        this.action = action;
    }

    public static DynamicRepository query(DriverConnection connection) {
        return new DynamicRepository(connection, SELECT);
    }

    public static DynamicRepository modify(DriverConnection connection) {
        return new DynamicRepository(connection, UPDATE);
    }

    public static DynamicRepository add(DriverConnection connection) {
        return new DynamicRepository(connection, INSERT);
    }

    public static DynamicRepository remove(DriverConnection connection) {
        return new DynamicRepository(connection, DELETE);
    }

    private static DriverConnection getConnection() {
        TransactionInfo transactionInfo = TransactionManager.peek();
        if (transactionInfo == null) {
            throw new TransactionException("No debbie transaction");
        }
        return transactionInfo.getDriverConnection();
    }

    public static DynamicRepository queryTransactional() {
        var connection = getConnection();
        return new DynamicRepository(connection, SELECT);
    }

    public static DynamicRepository modifyTransactional() {
        var connection = getConnection();
        return new DynamicRepository(connection, UPDATE);
    }

    public static DynamicRepository addTransactional() {
        var connection = getConnection();
        return new DynamicRepository(connection, INSERT);
    }

    public static DynamicRepository removeTransactional() {
        var connection = getConnection();
        return new DynamicRepository(connection, DELETE);
    }

    public static DynamicRepository sql(DataSourceDriverName driverName) {
        return new DynamicRepository(driverName);
    }

    private DynamicRepository addBlackSpace() {
        if (dynamicSql.length() > 0 && (dynamicSql.lastIndexOf(" ") == -1 || dynamicSql.lastIndexOf(" ") != dynamicSql.length() - 1)) {
            dynamicSql.append(" ");
        }
        return this;
    }

    public DynamicRepository append(SqlKeywords keywords) {
        addBlackSpace();
        dynamicSql.append(keywords.value());
        return this;
    }

    DynamicRepository appendBlackSpace() {
        dynamicSql.append(" ");
        return this;
    }

    DynamicRepository append(String str) {
        addBlackSpace();
        dynamicSql.append(str);
        return this;
    }

    DynamicRepository append(Object object) {
        addBlackSpace();
        dynamicSql.append(object);
        return this;
    }

    DynamicRepository appendWithNoSpace(Object object) {
        dynamicSql.append(object);
        return this;
    }

    public DynamicRepository show() {
        return append(SHOW);
    }

    public DynamicRepository databases() {
        return append(DATABASES);
    }

    public DynamicRepository use(String database) {
        return append(USE).append(database);
    }

    public DynamicRepository tables() {
        return append(TABLES);
    }

    public DynamicRepository create() {
        return append(CREATE);
    }

    public DynamicRepository database(String database) {
        return append(DATABASE).append(database);
    }

    public DynamicRepository database() {
        return append(DATABASE);
    }

    public DynamicRepository ifExists() {
        return addBlackSpace().append(IF).appendBlackSpace().append(EXISTS);
    }

    public DynamicRepository ifNotExists() {
        return append(IF).append(NOT.value()).append(EXISTS);
    }

    public DynamicRepository createDatabaseIfNotExists(String database) {
        addBlackSpace();
        // return dynamicSql.append("CREATE ")
        return this;
    }

    public DynamicRepository desc() {
        addBlackSpace();
        dynamicSql.append(DESC.value());
        return this;
    }

    public DynamicRepository drop() {
        addBlackSpace();
        dynamicSql.append(DROP.value());
        return this;
    }

    public DynamicRepository truncate() {
        return append(TRUNCATE);
    }

    public DynamicRepository alter() {
        return append(ALTER);
    }

    public DynamicRepository tableIfExists(String table, boolean ifExists) {
        addBlackSpace();
        dynamicSql.append(TABLE.value());
        if (ifExists) {
            dynamicSql.append(" ").append(IF.value()).append(EXISTS.value());
        }
        dynamicSql.append(" ").append(table);
        return this;
    }

    public DynamicRepository tableIfNotExists(String table, boolean ifNotExists) {
        addBlackSpace();
        dynamicSql.append(TABLE.value());
        if (ifNotExists) {
            ifNotExists();
        }
        dynamicSql.append(" ").append(table);
        return this;
    }

    public DynamicRepository table(String table) {
        addBlackSpace();
        dynamicSql.append(TABLE.value()).append(" ").append(table);
        return this;
    }

    public DynamicRepository leftParenthesis() {
        return append("(");
    }

    public DynamicRepository rightParenthesis() {
        return append(")");
    }

    public DynamicRepository add() {
        return append(ADD);
    }

    public DynamicRepository column(String columnName) {
        return append(columnName);
    }

    public DynamicRepository intDeFaultNull() {
        return append(INT).append(NULL);
    }

    public DynamicRepository defaultValue(String defaultValue) {
        addBlackSpace();
        dynamicSql.append(DEFAULT.value()).append(" ").append(defaultValue);
        return this;
    }

    public DynamicRepository comment(String comment) {
        addBlackSpace();
        if (driverName != DataSourceDriverName.sqlite)
            dynamicSql.append(COMMENT.value()).append(" '").append(comment).append("'");
        return this;
    }

    public DynamicRepository nullSql() {
        addBlackSpace();
        dynamicSql.append(NULL.value());
        return this;
    }

    public DynamicRepository primaryKey() {
        addBlackSpace();
        dynamicSql.append(PRIMARY.value()).append(" ").append(KEY.value());
        return this;
    }

    public DynamicRepository autoIncrement() {
        addBlackSpace();
        if (driverName == DataSourceDriverName.sqlite) {
            dynamicSql.append(AUTOINCREMENT.value());
        } else
            dynamicSql.append(AUTO_INCREMENT.value());
        return this;
    }

    public DynamicRepository autoIncrement(int begin) {
        return append(AUTO_INCREMENT).appendWithNoSpace("=").appendWithNoSpace(begin);
    }

    public DynamicRepository engine(String engine) {
        if (driverName != DataSourceDriverName.sqlite) {
            append(ENGINE).appendWithNoSpace("=").appendWithNoSpace(engine);
        }
        return this;
    }

    public DynamicRepository defaultCharset(String charset) {
        if (driverName != DataSourceDriverName.sqlite)
            append(DEFAULT).append(CHARSET).appendWithNoSpace("=").appendWithNoSpace(charset);
        return this;
    }

    public DynamicRepository defaultCharacterSet(String charset) {
        addBlackSpace();
        if (driverName != DataSourceDriverName.sqlite)
            dynamicSql.append(DEFAULT.value()).append(" ").append(CHARACTER.value()).append(" ").append(SET.value()).append(" ").append(charset);
        return this;
    }

    public DynamicRepository defaultCollate(String collate) {
        addBlackSpace();
        dynamicSql.append(DEFAULT.value()).append(" ").append(COLLATE).append(" ").append(collate);
        return this;
    }

    public DynamicRepository unique() {
        return addBlackSpace().append(UNIQUE);
    }

    public DynamicRepository ifSql() {
        return addBlackSpace().append(IF);
    }

    public DynamicRepository not() {
        return addBlackSpace().append(NOT);
    }

    public DynamicRepository exists() {
        return addBlackSpace().append(EXISTS);
    }

    public DynamicRepository tinyint() {
        dynamicSql.append(" TINYINT ");
        return addBlackSpace().append(T);
    }

    public DynamicRepository after(String columnName) {
        return append(AFTER).append(columnName);
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
        dynamicSql.append(" SET ").append(columns).append("= ? ");
        args.add(value);
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
                    dynamicSql.append(" ").append(iColumn).append(" = ?, ");
                }
            }
            var iColumn = columns.get(size - 1);
            if (iColumn != null) {
                dynamicSql.append(" ").append(iColumn).append(" = ? ");
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
        dynamicSql.append(column).append(" = ? ");
        args.add(value);
        return this;
    }

    public DynamicRepository eq(String condition) {
        dynamicSql.append(condition);
        return this;
    }

    public DynamicRepository in(String column, int inValueSize) {
        List<String> s = new ArrayList<>();
        for (int i = 0; i < inValueSize; i++) {
            s.add("?");
        }

        dynamicSql.append(column).append(" IN ( ").append(StringUtils.joining(s)).append(" )");
        return this;
    }

    public DynamicRepository $(Object extra) {
        return append(extra);
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

    public <T> List<T> toList(Class<T> resultClass) {
        RepositoryHandler repositoryHandler = new RepositoryHandler();
        repositoryHandler.setDriverName(driverName);
        if (action == SELECT) {
            return repositoryHandler.query(connection.getConnection(), dynamicSql.toString(), resultClass, args.toArray());
        }
        return new ArrayList<>();
    }

    public List<Map<String, Object>> toMap() {
        RepositoryHandler repositoryHandler = new RepositoryHandler();
        repositoryHandler.setDriverName(driverName);
        if (action == SELECT) {
            return repositoryHandler.queryMap(connection.getConnection(), dynamicSql.toString(), args.toArray());
        }
        return new ArrayList<>();
    }

    public <T> Optional<T> single(Class<T> resultClass) {
        RepositoryHandler repositoryHandler = new RepositoryHandler();
        repositoryHandler.setDriverName(driverName);
        if (action == SELECT) {
            T result = repositoryHandler.queryOne(connection.getConnection(), dynamicSql.toString(), resultClass, args.toArray());
            return Optional.ofNullable(result);
        }
        return Optional.empty();
    }

    public int execute() {
        if (action != SELECT) {
            RepositoryHandler repositoryHandler = new RepositoryHandler();
            repositoryHandler.setDriverName(driverName);
            return repositoryHandler.update(connection.getConnection(), dynamicSql.toString());
        } else {
            RepositoryHandler repositoryHandler = new RepositoryHandler();
            repositoryHandler.setDriverName(driverName);
            repositoryHandler.query(connection.getConnection(), dynamicSql.toString());
        }
        return 0;
    }

    @Override
    public String toString() {
        return dynamicSql.toString();
    }

    public String toSql() {
        return dynamicSql.toString();
    }
}