/**
 * Copyright (c) 2025 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.jdbc.repository;

import com.truthbean.Logger;
import com.truthbean.debbie.jdbc.column.ColumnInfo;
import com.truthbean.debbie.jdbc.datasource.DataSourceDriverName;
import com.truthbean.debbie.jdbc.entity.EntityInfo;
import com.truthbean.debbie.jdbc.entity.EntityResolver;
import com.truthbean.debbie.jdbc.transaction.TransactionException;
import com.truthbean.debbie.jdbc.transaction.TransactionInfo;
import com.truthbean.debbie.jdbc.transaction.TransactionManager;
import com.truthbean.core.util.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.truthbean.debbie.jdbc.repository.SqlKeywords.*;

/**
 * dynamic build sql
 *
 * @author TruthBean
 * @since 0.0.1
 */
public class DynamicRepository {

    private final SqlBuilder sqlBuilder;

    protected DynamicRepository(DataSourceDriverName driverName) {
        this.sqlBuilder = new SqlBuilder(driverName, this);
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
        this.sqlBuilder = new SqlBuilder(transaction.getDriverName(), this);
    }

    private RepositoryHandler repositoryHandler;

    public static class SqlBuilder {
        private final DynamicRepository dynamicRepository;

        private final StringBuilder dynamicSql = new StringBuilder();
        private final List<Object> args = new ArrayList<>();

        private final DataSourceDriverName driverName;
        private SqlBuilder(DataSourceDriverName driverName, DynamicRepository dynamicRepository) {
            this.driverName = driverName;
            this.dynamicRepository = dynamicRepository;
        }

        private SqlBuilder addBlackSpace() {
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

        public SqlBuilder append(SqlKeywords keywords) {
            justAddBlackSpace().append(keywords.value());
            return this;
        }

        StringBuilder justAppend(SqlKeywords keywords) {
            justAddBlackSpace().append(keywords.value());
            return dynamicSql;
        }

        SqlBuilder appendBlackSpace() {
            dynamicSql.append(" ");
            return this;
        }

        SqlBuilder append(String str) {
            justAddBlackSpace().append(str);
            return this;
        }

        SqlBuilder append(Object object) {
            justAddBlackSpace().append(object);
            return this;
        }

        SqlBuilder appendWithNoSpace(Object object) {
            dynamicSql.append(object);
            return this;
        }

        public SqlBuilder show() {
            return append(SHOW);
        }

        public SqlBuilder databases() {
            return append(DATABASES);
        }

        public SqlBuilder use(String database) {
            return append(USE).append(database);
        }

        public SqlBuilder tables() {
            return append(TABLES);
        }

        public SqlBuilder create() {
            return append(CREATE);
        }

        public SqlBuilder database(String database) {
            return append(DATABASE).append(database);
        }

        public SqlBuilder database() {
            return append(DATABASE);
        }

        public SqlBuilder ifExists() {
            return addBlackSpace().append(IF).appendBlackSpace().append(EXISTS);
        }

        public SqlBuilder ifNotExists() {
            return append(IF).append(NOT.value()).append(EXISTS);
        }

        public SqlBuilder createDatabaseIfNotExists(String database) {
            addBlackSpace();
            // return dynamicSql.append("CREATE ")
            return this;
        }

        public SqlBuilder desc() {
            return append(DESC.value());
        }

        public SqlBuilder drop() {
            return append(DROP.value());
        }

        public SqlBuilder truncate() {
            return append(TRUNCATE);
        }

        public SqlBuilder alter() {
            return append(ALTER);
        }

        public SqlBuilder tableIfExists(String table, boolean ifExists) {
            append(TABLE.value());
            if (ifExists) {
                dynamicSql.append(" ").append(IF.value()).append(EXISTS.value());
            }
            dynamicSql.append(" ").append(table);
            return this;
        }

        public SqlBuilder tableIfNotExists(String table, boolean ifNotExists) {
            append(TABLE.value());
            if (ifNotExists) {
                ifNotExists();
            }
            dynamicSql.append(" ").append(table);
            return this;
        }

        public SqlBuilder table(String table) {
            justAddBlackSpace().append(TABLE.value()).append(" ").append(table);
            return this;
        }

        public SqlBuilder leftParenthesis() {
            return append("(");
        }

        public SqlBuilder rightParenthesis() {
            return append(")");
        }

        public SqlBuilder add() {
            return append(ADD);
        }

        public SqlBuilder column(String columnName) {
            return append(columnName);
        }

        public SqlBuilder intDeFaultNull() {
            justAddBlackSpace().append(INT.value()).append(NULL.value());
            return this;
        }

        public SqlBuilder defaultValue(String defaultValue) {
            justAddBlackSpace().append(DEFAULT.value()).append(" ").append(defaultValue);
            return this;
        }

        public SqlBuilder comment(String comment) {
            addBlackSpace();
            if (driverName != DataSourceDriverName.sqlite) {
                dynamicSql.append(COMMENT.value()).append(" '").append(comment).append("'");
            }
            return this;
        }

        public SqlBuilder nullSql() {
            justAddBlackSpace().append(NULL.value());
            return this;
        }

        public SqlBuilder primaryKey() {
            justAddBlackSpace().append(PRIMARY.value()).append(" ").append(KEY.value());
            return this;
        }

        public SqlBuilder autoIncrement() {
            addBlackSpace();
            if (driverName == DataSourceDriverName.sqlite) {
                dynamicSql.append(AUTOINCREMENT.value());
            } else {
                dynamicSql.append(AUTO_INCREMENT.value());
            }
            return this;
        }

        public SqlBuilder autoIncrement(int begin) {
            return append(AUTO_INCREMENT).appendWithNoSpace("=").appendWithNoSpace(begin);
        }

        public SqlBuilder engine(String engine) {
            if (driverName != DataSourceDriverName.sqlite) {
                append(ENGINE).appendWithNoSpace("=").appendWithNoSpace(engine);
            }
            return this;
        }

        public SqlBuilder defaultCharset(String charset) {
            if (driverName != DataSourceDriverName.sqlite) {
                append(DEFAULT).append(CHARSET).appendWithNoSpace("=").appendWithNoSpace(charset);
            }
            return this;
        }

        public SqlBuilder defaultCharacterSet(String charset) {
            addBlackSpace();
            if (driverName != DataSourceDriverName.sqlite) {
                dynamicSql.append(DEFAULT.value()).append(" ").append(CHARACTER.value()).append(" ").append(SET.value()).append(" ").append(charset);
            }
            return this;
        }

        public SqlBuilder defaultCollate(String collate) {
            addBlackSpace();
            dynamicSql.append(DEFAULT.value()).append(" ").append(COLLATE).append(" ").append(collate);
            return this;
        }

        public SqlBuilder unique() {
            return append(UNIQUE);
        }

        public SqlBuilder ifSql() {
            return append(IF);
        }

        public SqlBuilder not() {
            return append(NOT);
        }

        public SqlBuilder exists() {
            return append(EXISTS);
        }

        public SqlBuilder tinyint() {
            return append(TINYINT);
        }

        public SqlBuilder after(String columnName) {
            return append(AFTER).append(columnName);
        }

        public SqlBuilder delete() {
            dynamicSql.append(SqlKeywords.DELETE.value());
            return this;
        }

        public SqlBuilder insert() {
            dynamicSql.append("INSERT INTO");
            return this;
        }

        public SqlBuilder update() {
            dynamicSql.append(SqlKeywords.UPDATE.value());
            return this;
        }

        public SqlBuilder update(String table) {
            dynamicSql.append("UPDATE ").append(table);
            return this;
        }

        public SqlBuilder set() {
            append(SET);
            return this;
        }

        public SqlBuilder set(String columns, String value) {
            append(SET).append(columns).appendWithNoSpace(" = ?");
            args.add(value);
            return this;
        }

        public SqlBuilder set(String columns, Object value) {
            append(SET).append(columns).appendWithNoSpace(" = ?");
            args.add(value);
            return this;
        }

        public SqlBuilder set(String columns) {
            append(SET).append(columns).appendWithNoSpace(" = ?");
            return this;
        }

        public SqlBuilder set(List<String> columns) {
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

        public SqlBuilder select() {
            dynamicSql.append(" SELECT ");
            return this;
        }

        public SqlBuilder select(List<String> columns) {
            append(SqlKeywords.SELECT.value());
            this.joinWith(",", columns);
            return this;
        }

        public SqlBuilder select(String columns) {
            append("SELECT ").appendWithNoSpace(columns);
            return this;
        }

        public SqlBuilder select(String... columns) {
            append("SELECT ");
            this.joinWith(",", columns);
            return this;
        }

        public SqlBuilder selectAll() {
            append("SELECT * ");
            return this;
        }

        public <E> SqlBuilder select(EntityInfo<E> entityInfo) {
            return append(SqlKeywords.SELECT.value())
                    .select(getColumnNames(entityInfo))
                    .from(entityInfo.getTable());
        }

        static <E> List<String> getColumnNames(EntityInfo<E> entityInfo) {
            List<ColumnInfo> columnInfoList = new ArrayList<>();
            var columns = entityInfo.getColumnInfoList();
            if (columns != null && !columns.isEmpty()) {
                columnInfoList.addAll(columns);
            }
            ColumnInfo primaryKey = entityInfo.getPrimaryKey();
            if (primaryKey != null) {
                columnInfoList.add(primaryKey);
            }

            List<String> columnNames = new LinkedList<>();
            for (ColumnInfo column : columnInfoList) {
                columnNames.add(column.getColumn());
            }
            return columnNames;
        }

        public SqlBuilder distinct() {
            append(DISTINCT);
            return this;
        }

        public SqlBuilder count() {
            append("COUNT(*) ");
            return this;
        }

        public SqlBuilder count(String column) {
            append("COUNT(").appendWithNoSpace(column).append(")");
            return this;
        }

        public SqlBuilder joinWith(String split, List<String> columns) {
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

        public SqlBuilder joinWith(String split, String... columns) {
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

        public SqlBuilder values(List<Object> values) {
            dynamicSql.append(" VALUES (");
            value(values);
            dynamicSql.append(") ");
            return this;
        }

        public SqlBuilder signs(int length) {
            List<String> signs = new ArrayList<>(length);
            for (int i = 0; i < length; i++) {
                signs.set(i, "?");
            }
            return value(signs);
        }

        public SqlBuilder value(List<?> values) {
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

        public SqlBuilder value(Object... values) {
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

        public SqlBuilder from(String table) {
            if (table != null) {
                append(FROM).append(table);
            }
            return this;
        }

        public SqlBuilder where() {
            append(WHERE);
            return this;
        }

        public SqlBuilder doIf(boolean condition, Supplier<DynamicRepository> supplier) {
            if (condition) {
                justAddBlackSpace().append(supplier.get().toSql());
            }
            return this;
        }

        public SqlBuilder doIf(boolean condition, String sqlPart) {
            if (condition) {
                justAddBlackSpace().append(sqlPart);
            }
            return this;
        }

        public SqlBuilder and(String condition) {
            append(AND).append(condition);
            return this;
        }

        public SqlBuilder and(Function<SqlBuilder, SqlBuilder> condition) {
            append(AND).leftParenthesis()
                    .append(condition.apply(new SqlBuilder(driverName, null)).toSql())
                    .rightParenthesis();
            return this;
        }

        public SqlBuilder and() {
            append(AND);
            return this;
        }

        public SqlBuilder or(Function<SqlBuilder, SqlBuilder> condition) {
            append(OR)
                    .leftParenthesis()
                    .append(condition.apply(new SqlBuilder(driverName, null)).toSql())
                    .rightParenthesis();
            return this;
        }

        public SqlBuilder or(String condition) {
            append(OR).append(condition);
            return this;
        }

        public SqlBuilder or() {
            append(OR);
            return this;
        }

        public SqlBuilder exist(String subSql) {
            append("EXISTS( ").appendWithNoSpace(subSql).appendWithNoSpace(" )");
            return this;
        }

        public SqlBuilder eq(String column, Object value) {
            append(column).appendWithNoSpace(" = ? ");
            args.add(value);
            return this;
        }

        public SqlBuilder ne(String column, Object value) {
            append(column).appendWithNoSpace(" != ? ");
            args.add(value);
            return this;
        }

        public SqlBuilder eq(String condition) {
            append(condition);
            return this;
        }

        public SqlBuilder in(String column, int inValueSize) {
            List<String> s = new ArrayList<>();
            for (int i = 0; i < inValueSize; i++) {
                s.add("?");
            }

            append(column).appendWithNoSpace(" IN ( ").appendWithNoSpace(StringUtils.joining(s)).appendWithNoSpace(" )");
            return this;
        }

        public SqlBuilder in(String column, Collection<?> values) {
            List<String> s = new ArrayList<>();
            for (int i = 0; i < values.size(); i++) {
                s.add("?");
            }

            append(column).appendWithNoSpace(" IN ( ").appendWithNoSpace(StringUtils.joining(s)).appendWithNoSpace(" )");
            args.addAll(values);
            return this;
        }

        public SqlBuilder $(Object extra) {
            return append(extra);
        }

        public SqlBuilder foreach(String open, String close, Collection<?> collection, String separator) {
            if (collection != null && !collection.isEmpty()) {
                append(open)
                        .appendWithNoSpace(StringUtils.joining(collection, separator))
                        .appendWithNoSpace(close);
            }
            return this;
        }

        public SqlBuilder left() {
            append(LEFT);
            return this;
        }

        public SqlBuilder right() {
            append(RIGHT);
            return this;
        }

        public SqlBuilder join() {
            append(JOIN);
            return this;
        }

        public SqlBuilder join(String table) {
            append(JOIN).append(table);
            return this;
        }

        public SqlBuilder on() {
            append(ON);
            return this;
        }

        public SqlBuilder limit(int offset, int limit) {
            append(LIMIT).append(limit).append(OFFSET).append(offset);
            return this;
        }

        public SqlBuilder limit(int limit) {
            append(LIMIT).append(limit);
            return this;
        }

        public SqlBuilder orderBy(String column) {
            append(ORDER).append(BY).append(column);
            return this;
        }

        private boolean isNotEmpty() {
            return !dynamicSql.isEmpty();
        }

        public List<Object> getSqlParams() {
            return args;
        }

        public Object[] getSqlParamsArray() {
            return args.toArray();
        }

        public String build() {
            return dynamicSql.toString();
        }

        public String toSql() {
            return dynamicSql.toString();
        }

        @Override
        public String toString() {
            return dynamicSql.toString();
        }

        public DynamicRepository repository() {
            dynamicRepository.repositoryHandler = RepositoryHandler.INSTANCE;
            return dynamicRepository;
        }

        public DynamicRepository dmlRepository() {
            dynamicRepository.repositoryHandler = DmlRepositoryHandler.INSTANCE;
            return dynamicRepository;
        }

        public DynamicRepository ddlRepository() {
            dynamicRepository.repositoryHandler = DdlRepositoryHandler.INSTANCE;
            return dynamicRepository;
        }
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

    public static SqlBuilder sqlBuilder(DataSourceDriverName driverName) {
        return new DynamicRepository(driverName).sqlBuilder;
    }

    public SqlBuilder sqlBuilder() {
        return sqlBuilder;
    }

    public <T> List<T> toList(Logger logger, EntityResolver entityResolver,
                              Class<T> resultClass) {
        if (action == SELECT && sqlBuilder.isNotEmpty() && repositoryHandler instanceof JdbcRepositoryHandler) {
            return ((JdbcRepositoryHandler) repositoryHandler).query(logger, transaction, entityResolver,
                    sqlBuilder.toString(), resultClass, sqlBuilder.getSqlParamsArray());
        }
        return new ArrayList<>();
    }

    public List<Map<String, Object>> toMap(Logger logger) {
        if (action == SELECT && sqlBuilder.isNotEmpty() && repositoryHandler != null) {
            return repositoryHandler.queryMap(logger, transaction, sqlBuilder.toString(), sqlBuilder.getSqlParamsArray());
        }
        return new ArrayList<>();
    }

    public <T> Optional<T> single(Logger logger, EntityResolver entityResolver,
                                  Class<T> resultClass) {
        if (action == SELECT && sqlBuilder.isNotEmpty() && repositoryHandler instanceof JdbcRepositoryHandler) {
            T result = ((JdbcRepositoryHandler) repositoryHandler).queryOne(logger, transaction, entityResolver,
                    sqlBuilder.toString(), resultClass, sqlBuilder.getSqlParamsArray());
            return Optional.ofNullable(result);
        }
        return Optional.empty();
    }

    public int execute(Logger logger) {
        if (repositoryHandler != null && sqlBuilder.isNotEmpty()) {
            if (action != SELECT) {
                return repositoryHandler.update(logger, transaction, sqlBuilder.toString());
            } else {
                repositoryHandler.query(logger, transaction, sqlBuilder.toString());
            }
        }
        return 0;
    }

    @Override
    public String toString() {
        return sqlBuilder.toString();
    }

    public String toSql() {
        return sqlBuilder.toString();
    }
}