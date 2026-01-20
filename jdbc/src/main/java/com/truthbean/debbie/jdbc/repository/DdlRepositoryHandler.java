/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.jdbc.repository;

import com.truthbean.debbie.jdbc.annotation.SqlEntity;
import com.truthbean.debbie.jdbc.column.ColumnInfo;
import com.truthbean.debbie.jdbc.datasource.DataSourceDriverName;
import com.truthbean.debbie.jdbc.entity.EntityInfo;
import com.truthbean.debbie.jdbc.entity.EntityResolver;
import com.truthbean.debbie.jdbc.transaction.TransactionInfo;
import com.truthbean.debbie.reflection.ClassInfo;
import com.truthbean.Logger;
import com.truthbean.LoggerFactory;

import java.sql.JDBCType;
import java.util.ArrayList;
import java.util.List;

/**
 * DDL: Data Definition Language
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/4/3 23:31.
 */
public class DdlRepositoryHandler extends RepositoryHandler {

    public static final DdlRepositoryHandler INSTANCE = new DdlRepositoryHandler();

    protected DdlRepositoryHandler() {
    }

    public int createDatabase(Logger logger, TransactionInfo transaction, String database) {
        DataSourceDriverName driverName = transaction.getDriverName();
        String sql = DynamicRepository.sqlBuilder(driverName).create().database(database).build();
        return super.update(logger, transaction, sql);
    }

    public List<String> showDatabases(Logger logger, TransactionInfo transaction) {
        DataSourceDriverName driverName = transaction.getDriverName();
        String sql = DynamicRepository.sqlBuilder(driverName).show().databases().build();
        return getStrings(logger, transaction, sql);
    }

    private List<String> getStrings(Logger logger, TransactionInfo transaction, String sql) {
        List<ColumnInfo> list = super.querySingle(logger, transaction, sql);
        List<String> result = new ArrayList<>();
        for (ColumnInfo columnInfo : list) {
            if (columnInfo.getJavaClass() == String.class) {
                result.add((String) columnInfo.getValue());
            }
        }
        return result;
    }

    public int dropDatabase(Logger logger, TransactionInfo transaction, String database) {
        DataSourceDriverName driverName = transaction.getDriverName();
        String sql = DynamicRepository.sqlBuilder(driverName).drop().database(database).build();
        return super.update(logger, transaction, sql);
    }

    public int useDatabase(Logger logger, TransactionInfo transaction, String database) {
        return DynamicRepository.modify(transaction).sqlBuilder().use(database).ddlRepository().execute(logger);
    }

    public List<String> showTables(Logger logger, TransactionInfo transaction) {
        DataSourceDriverName driverName = transaction.getDriverName();
        String sql = DynamicRepository.sqlBuilder(driverName).show().tables().build();
        return getStrings(logger, transaction, sql);
    }

    public <E> void createTable(Logger logger, TransactionInfo transaction, Class<E> entity) {
        ClassInfo<E> classInfo = new ClassInfo<>(entity);
        var entityInfo = new EntityInfo<E>();
        SqlEntity sqlEntity = (SqlEntity) classInfo.getClassAnnotations().get(SqlEntity.class).getOrigin();
        var entityClass = classInfo.getClazz();
        var table = EntityResolver.getTableName(sqlEntity, entityClass);
        entityInfo.setTable(table);
        entityInfo.setCharset(sqlEntity.charset());
        entityInfo.setEngine(sqlEntity.engine());

        var columns = EntityResolver.resolveClassInfo(classInfo);
        entityInfo.setColumnInfoList(columns);
        createTable(logger, transaction, entityInfo);
    }

    public <E> void createTable(Logger logger, TransactionInfo transaction, EntityInfo<E> entityInfo) {
        var columns = entityInfo.getColumnInfoList();
        DynamicRepository.SqlBuilder sqlBuilder = DynamicRepository.modify(transaction)
                .sqlBuilder()
                .create().tableIfNotExists(entityInfo.getTable(), true).leftParenthesis();
        if (columns != null && !columns.isEmpty()) {
            int size = columns.size();
            for (int i = 0; i < size - 1; i++) {
                var iColumn = columns.get(i);
                if (iColumn != null) {
                    buildCreateTableColumns(sqlBuilder, iColumn);
                    sqlBuilder.$(", ");
                }
            }
            var iColumn = columns.get(size - 1);
            if (iColumn != null) {
                buildCreateTableColumns(sqlBuilder, iColumn);
            }
        }
        sqlBuilder.rightParenthesis();

        var engine = entityInfo.getEngine();
        if (!engine.isBlank()) {
            sqlBuilder.engine(engine);
        }

        var charset = entityInfo.getCharset();
        if (!charset.isBlank()) {
            sqlBuilder.defaultCharset(charset);
        }
        sqlBuilder.ddlRepository().execute(logger);
    }

    private void buildCreateTableColumns(DynamicRepository.SqlBuilder sqlBuilder, ColumnInfo iColumn) {
        var type = iColumn.getJdbcType().getName();
        if (iColumn.getJdbcType().equals(JDBCType.VARCHAR)) {
            type = SqlKeywords.VARCHAR.value() + "(" + iColumn.getCharMaxLength() + ")";
        }
        sqlBuilder.$("`").appendWithNoSpace(iColumn.getColumn()).appendWithNoSpace("` ").$(type);
        if (iColumn.isNullable() != null) {
            if (iColumn.isNullable()) {
                sqlBuilder.nullSql();
            } else {
                sqlBuilder.not().nullSql();
            }
        }

        if (iColumn.isUnique()) {
            sqlBuilder.unique();
        }

        if (iColumn.getColumnDefaultValue() != null) {
            sqlBuilder.defaultValue(iColumn.getColumnDefaultValue());
        }

        if (iColumn.isPrimaryKey()) {
            sqlBuilder.primaryKey();

            if (iColumn.getPrimaryKeyType() != null) {
                switch (iColumn.getPrimaryKeyType()) {
                    case UUID:
                    case NONE:
                    default:
                        break;
                    case AUTO_INCREMENT:
                        sqlBuilder.autoIncrement();
                        break;
                }
            }
        }

        if (iColumn.getComment() != null) {
            sqlBuilder.comment(iColumn.getComment());
        }
    }

    public void dropTable(Logger logger, TransactionInfo transaction, String table) {
        DataSourceDriverName driverName = transaction.getDriverName();
        String sql = DynamicRepository.sqlBuilder(driverName).drop().tableIfExists(table, true).build();
        super.update(logger, transaction, sql);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DdlRepositoryHandler.class);
}
