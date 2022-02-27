/**
 * Copyright (c) 2022 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.jdbc.repository;

import com.truthbean.Logger;
import com.truthbean.debbie.jdbc.datasource.DataSourceDriverName;
import com.truthbean.debbie.jdbc.entity.EntityResolver;
import com.truthbean.debbie.jdbc.transaction.TransactionException;
import com.truthbean.debbie.jdbc.transaction.TransactionInfo;
import com.truthbean.debbie.jdbc.transaction.TransactionManager;
import com.truthbean.common.mini.util.StringUtils;

import java.util.*;
import java.util.function.Supplier;

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
    private final DataSourceDriverName driverName;

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
    private TransactionInfo transaction;
    private DynamicRepository(TransactionInfo transaction, int action) {
        this.transaction = transaction;
        this.action = action;
        this.driverName = transaction.getDriverName();
    }

    public static DynamicRepository query(TransactionInfo transaction) {
        return new DynamicRepository(transaction, SELECT);
    }

    public static DynamicRepository modify(TransactionInfo transaction) {
        return new DynamicRepository(transaction, UPDATE);
    }

    public static DynamicRepository add(TransactionInfo transaction) {
        return new DynamicRepository(transaction, INSERT);
    }

    public static DynamicRepository remove(TransactionInfo transaction) {
        return new DynamicRepository(transaction, DELETE);
    }

    private static TransactionInfo getTransaction() {
        TransactionInfo transactionInfo = TransactionManager.peek();
        if (transactionInfo == null) {
            throw new TransactionException("No debbie transaction");
        }
        return transactionInfo;
    }

    public static DynamicRepository queryTransactional() {
        var transaction = getTransaction();
        return new DynamicRepository(transaction, SELECT);
    }

    public static DynamicRepository modifyTransactional() {
        var transaction = getTransaction();
        return new DynamicRepository(transaction, UPDATE);
    }

    public static DynamicRepository addTransactional() {
        var transaction = getTransaction();
        return new DynamicRepository(transaction, INSERT);
    }

    public static DynamicRepository removeTransactional() {
        var transaction = getTransaction();
        return new DynamicRepository(transaction, DELETE);
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

    private StringBuilder justAddBlackSpace() {
        if (dynamicSql.length() > 0 && (dynamicSql.lastIndexOf(" ") == -1 || dynamicSql.lastIndexOf(" ") != dynamicSql.length() - 1)) {
            dynamicSql.append(" ");
        }
        return dynamicSql;
    }

    public DynamicRepository append(SqlKeywords keywords) {
        justAddBlackSpace().append(keywords.value());
        return this;
    }

    StringBuilder justAppend(SqlKeywords keywords) {
        justAddBlackSpace().append(keywords.value());
        return dynamicSql;
    }

    DynamicRepository appendBlackSpace() {
        dynamicSql.append(" ");
        return this;
    }

    DynamicRepository append(String str) {
        justAddBlackSpace().append(str);
        return this;
    }

    DynamicRepository append(Object object) {
        justAddBlackSpace().append(object);
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
        return append(DESC.value());
    }

    public DynamicRepository drop() {
        return append(DROP.value());
    }

    public DynamicRepository truncate() {
        return append(TRUNCATE);
    }

    public DynamicRepository alter() {
        return append(ALTER);
    }

    public DynamicRepository tableIfExists(String table, boolean ifExists) {
        append(TABLE.value());
        if (ifExists) {
            dynamicSql.append(" ").append(IF.value()).append(EXISTS.value());
        }
        dynamicSql.append(" ").append(table);
        return this;
    }

    public DynamicRepository tableIfNotExists(String table, boolean ifNotExists) {
        append(TABLE.value());
        if (ifNotExists) {
            ifNotExists();
        }
        dynamicSql.append(" ").append(table);
        return this;
    }

    public DynamicRepository table(String table) {
        justAddBlackSpace().append(TABLE.value()).append(" ").append(table);
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
        justAddBlackSpace().append(INT.value()).append(NULL.value());
        return this;
    }

    public DynamicRepository defaultValue(String defaultValue) {
        justAddBlackSpace().append(DEFAULT.value()).append(" ").append(defaultValue);
        return this;
    }

    public DynamicRepository comment(String comment) {
        addBlackSpace();
        if (driverName != DataSourceDriverName.sqlite) {
            dynamicSql.append(COMMENT.value()).append(" '").append(comment).append("'");
        }
        return this;
    }

    public DynamicRepository nullSql() {
        justAddBlackSpace().append(NULL.value());
        return this;
    }

    public DynamicRepository primaryKey() {
        justAddBlackSpace().append(PRIMARY.value()).append(" ").append(KEY.value());
        return this;
    }

    public DynamicRepository autoIncrement() {
        addBlackSpace();
        if (driverName == DataSourceDriverName.sqlite) {
            dynamicSql.append(AUTOINCREMENT.value());
        } else {
            dynamicSql.append(AUTO_INCREMENT.value());
        }
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
        if (driverName != DataSourceDriverName.sqlite) {
            append(DEFAULT).append(CHARSET).appendWithNoSpace("=").appendWithNoSpace(charset);
        }
        return this;
    }

    public DynamicRepository defaultCharacterSet(String charset) {
        addBlackSpace();
        if (driverName != DataSourceDriverName.sqlite) {
            dynamicSql.append(DEFAULT.value()).append(" ").append(CHARACTER.value()).append(" ").append(SET.value()).append(" ").append(charset);
        }
        return this;
    }

    public DynamicRepository defaultCollate(String collate) {
        addBlackSpace();
        dynamicSql.append(DEFAULT.value()).append(" ").append(COLLATE).append(" ").append(collate);
        return this;
    }

    public DynamicRepository unique() {
        return append(UNIQUE);
    }

    public DynamicRepository ifSql() {
        return append(IF);
    }

    public DynamicRepository not() {
        return append(NOT);
    }

    public DynamicRepository exists() {
        return append(EXISTS);
    }

    public DynamicRepository tinyint() {
        return append(TINYINT);
    }

    public DynamicRepository after(String columnName) {
        return append(AFTER).append(columnName);
    }

    public DynamicRepository delete() {
        dynamicSql.append(SqlKeywords.DELETE.value());
        return this;
    }

    public DynamicRepository insert() {
        dynamicSql.append("INSERT INTO");
        return this;
    }

    public DynamicRepository update() {
        dynamicSql.append(SqlKeywords.UPDATE.value());
        return this;
    }

    public DynamicRepository update(String table) {
        dynamicSql.append("UPDATE ").append(table);
        return this;
    }

    public DynamicRepository set() {
        append(SET);
        return this;
    }

    public DynamicRepository set(String columns, String value) {
        append(SET).append(columns).appendWithNoSpace(" = ?");
        args.add(value);
        return this;
    }

    public DynamicRepository set(String columns) {
        append(SET).append(columns).appendWithNoSpace(" = ?");
        return this;
    }

    public DynamicRepository set(List<String> columns) {
        append(SET);
        int size;
        if (columns != null && (size = columns.size()) > 0) {
            for (int i = 0; i < size - 1; i++) {
                var iColumn = columns.get(i);
                if (iColumn != null) {
                    append(iColumn).appendWithNoSpace(" = ?, ");
                }
            }
            var iColumn = columns.get(size - 1);
            if (iColumn != null) {
                append(iColumn).appendWithNoSpace(" = ?");
            }
        }
        return this;
    }

    public DynamicRepository select() {
        dynamicSql.append(" SELECT ");
        return this;
    }

    public DynamicRepository select(List<String> columns) {
        append(SqlKeywords.SELECT.value());
        this.joinWith(",", columns);
        return this;
    }

    public DynamicRepository select(String columns) {
        append("SELECT ").appendWithNoSpace(columns);
        return this;
    }

    public DynamicRepository select(String... columns) {
        append("SELECT ");
        this.joinWith(",", columns);
        return this;
    }

    public DynamicRepository selectAll() {
        append("SELECT * ");
        return this;
    }

    public DynamicRepository distinct() {
        append(DISTINCT);
        return this;
    }

    public DynamicRepository count() {
        append("COUNT(*) ");
        return this;
    }

    public DynamicRepository count(String column) {
        append("COUNT(").appendWithNoSpace(column).append(")");
        return this;
    }

    public DynamicRepository joinWith(String split, List<String> columns) {
        int size;
        if (columns != null && (size = columns.size()) > 0) {
            for (int i = 0; i < size - 1; i++) {
                var iColumn = columns.get(i);
                if (iColumn != null) {
                    append(iColumn).appendWithNoSpace(split);
                }
            }
            var iColumn = columns.get(size - 1);
            if (iColumn != null) {
                append(iColumn);
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
                    append(iColumn).appendWithNoSpace(split);
                }
            }
            var iColumn = columns[size - 1];
            if (iColumn != null) {
                append(iColumn);
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
                dynamicSql.append(iValue);
            }
        }
        return this;
    }

    public DynamicRepository from(String table) {
        if (table != null) {
            append(FROM).append(table);
        }
        return this;
    }

    public DynamicRepository where() {
        append(WHERE);
        return this;
    }

    public DynamicRepository doIf(boolean condition, Supplier<DynamicRepository> supplier) {
        if (condition) {
            justAddBlackSpace().append(supplier.get().toSql());
        }
        return this;
    }

    public DynamicRepository doIf(boolean condition, String sqlPart) {
        if (condition) {
            justAddBlackSpace().append(sqlPart);
        }
        return this;
    }

    public DynamicRepository and(String condition) {
        append(AND).append(condition);
        return this;
    }

    public DynamicRepository and() {
        append(AND);
        return this;
    }

    public DynamicRepository or(String condition) {
        append(OR).append(condition);
        return this;
    }

    public DynamicRepository or() {
        append(OR);
        return this;
    }

    public DynamicRepository exist(String subSql) {
        append("EXISTS( ").appendWithNoSpace(subSql).appendWithNoSpace(" )");
        return this;
    }

    public DynamicRepository eq(String column, Object value) {
        append(column).appendWithNoSpace(" = ? ");
        args.add(value);
        return this;
    }

    public DynamicRepository eq(String condition) {
        append(condition);
        return this;
    }

    public DynamicRepository in(String column, int inValueSize) {
        List<String> s = new ArrayList<>();
        for (int i = 0; i < inValueSize; i++) {
            s.add("?");
        }

        append(column).appendWithNoSpace(" IN ( ").appendWithNoSpace(StringUtils.joining(s)).appendWithNoSpace(" )");
        return this;
    }

    public DynamicRepository $(Object extra) {
        return append(extra);
    }

    public DynamicRepository foreach(String open, String close, Collection<?> collection, String separator) {
        if (collection != null && !collection.isEmpty()) {
            append(open)
                    .appendWithNoSpace(StringUtils.joining(collection, separator))
                    .appendWithNoSpace(close);
        }
        return this;
    }

    public DynamicRepository left() {
        append(LEFT);
        return this;
    }

    public DynamicRepository right() {
        append(RIGHT);
        return this;
    }

    public DynamicRepository join() {
        append(JOIN);
        return this;
    }

    public DynamicRepository join(String table) {
        append(JOIN).append(table);
        return this;
    }

    public DynamicRepository on() {
        append(ON);
        return this;
    }

    public DynamicRepository limit(int offset, int limit) {
        append(LIMIT).append(limit).append(OFFSET).append(offset);
        return this;
    }

    public DynamicRepository limit(int limit) {
        append(LIMIT).append(limit);
        return this;
    }

    public DynamicRepository orderBy(String column) {
        append(ORDER).append(BY).append(column);
        return this;
    }

    public String builder() {
        return dynamicSql.toString();
    }

    public <T> List<T> toList(Logger logger, JdbcRepositoryHandler repositoryHandler, EntityResolver entityResolver,
                              Class<T> resultClass) {
        if (action == SELECT) {
            return repositoryHandler.query(logger, transaction, entityResolver, dynamicSql.toString(), resultClass, args.toArray());
        }
        return new ArrayList<>();
    }

    public List<Map<String, Object>> toMap(Logger logger, RepositoryHandler repositoryHandler) {
        if (action == SELECT) {
            return repositoryHandler.queryMap(logger, transaction, dynamicSql.toString(), args.toArray());
        }
        return new ArrayList<>();
    }

    public <T> Optional<T> single(Logger logger, JdbcRepositoryHandler repositoryHandler, EntityResolver entityResolver,
                                  Class<T> resultClass) {
        if (action == SELECT) {
            T result = repositoryHandler.queryOne(logger, transaction, entityResolver, dynamicSql.toString(), resultClass, args.toArray());
            return Optional.ofNullable(result);
        }
        return Optional.empty();
    }

    public int execute(Logger logger, RepositoryHandler repositoryHandler) {
        if (action != SELECT) {
            return repositoryHandler.update(logger, transaction, dynamicSql.toString());
        } else {
            repositoryHandler.query(logger, transaction, dynamicSql.toString());
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