/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
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
import java.util.List;

/**
 * DDL: Data Definition Language
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/4/3 23:31.
 */
public class DdlRepositoryHandler extends RepositoryHandler {

    public int createDatabase(TransactionInfo transaction, String database) {
        DataSourceDriverName driverName = transaction.getDriverName();
        String sql = DynamicRepository.sql(driverName).create().database(database).builder();
        return super.update(transaction, sql);
    }

    public List<String> showDatabases(TransactionInfo transaction) {
        DataSourceDriverName driverName = transaction.getDriverName();
        String sql = DynamicRepository.sql(driverName).show().databases().builder();
        return super.query(transaction, sql, String.class);
    }

    public int dropDatabase(TransactionInfo transaction, String database) {
        DataSourceDriverName driverName = transaction.getDriverName();
        String sql = DynamicRepository.sql(driverName).drop().database(database).builder();
        return super.update(transaction, sql);
    }

    public int useDatabase(TransactionInfo transaction, String database) {
        return DynamicRepository.modify(transaction).use(database).execute();
    }

    public List<String> showTables(TransactionInfo transaction) {
        DataSourceDriverName driverName = transaction.getDriverName();
        String sql = DynamicRepository.sql(driverName).show().tables().builder();
        return super.query(transaction, sql, String.class);
    }

    public <E> void createTable(TransactionInfo transaction, Class<E> entity) {
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
        createTable(transaction, entityInfo);
    }

    public <E> void createTable(TransactionInfo transaction, EntityInfo<E> entityInfo) {
        var columns = entityInfo.getColumnInfoList();
        DynamicRepository repository = DynamicRepository.modify(transaction).create()
                .tableIfNotExists(entityInfo.getTable(), true).leftParenthesis();
        if (columns != null && !columns.isEmpty()) {
            int size = columns.size();
            for (int i = 0; i < size - 1; i++) {
                var iColumn = columns.get(i);
                if (iColumn != null) {
                    buildCreateTableColumns(repository, iColumn);
                    repository.$(", ");
                }
            }
            var iColumn = columns.get(size - 1);
            if (iColumn != null) {
                buildCreateTableColumns(repository, iColumn);
            }
        }
        repository.rightParenthesis();

        var engine = entityInfo.getEngine();
        if (!engine.isBlank()) {
            repository.engine(engine);
        }

        var charset = entityInfo.getCharset();
        if (!charset.isBlank()) {
            repository.defaultCharset(charset);
        }
        repository.execute();
    }

    private void buildCreateTableColumns(DynamicRepository sqlBuilder, ColumnInfo iColumn) {
        var type = iColumn.getJdbcType().getName();
        if (iColumn.getJdbcType().equals(JDBCType.VARCHAR)) {
            type = SqlKeywords.VARCHAR.value() + "(" + iColumn.getCharMaxLength() + ")";
        }
        sqlBuilder.$("`").appendWithNoSpace(iColumn.getColumnName()).appendWithNoSpace("` ").$(type);
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

    public void dropTable(TransactionInfo transaction, String table) {
        DataSourceDriverName driverName = transaction.getDriverName();
        String sql = DynamicRepository.sql(driverName).drop().tableIfExists(table, true).builder();
        super.update(transaction, sql);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DdlRepositoryHandler.class);
}
