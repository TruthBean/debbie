package com.truthbean.debbie.jdbc.repository;

import com.truthbean.debbie.core.bean.BeanInitialization;
import com.truthbean.debbie.jdbc.annotation.SqlEntity;
import com.truthbean.debbie.jdbc.column.ColumnInfo;
import com.truthbean.debbie.jdbc.transaction.TransactionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.JDBCType;
import java.util.List;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/4/3 23:31.
 */
public class DdlRepositoryHandler extends RepositoryHandler {

    public int createDatabase(Connection connection, String database) throws TransactionException {
        String sql = DynamicSqlBuilder.sql().create().database(database).builder();
        LOGGER.debug(sql);
        return super.update(connection, sql);
    }

    public List<String> showDatabases(Connection connection) {
        String sql = DynamicSqlBuilder.sql().show().databases().builder();
        LOGGER.debug(sql);
        return super.select(connection, sql, String.class);
    }

    public int dropDatabase(Connection connection, String database) throws TransactionException {
        String sql = DynamicSqlBuilder.sql().drop().database(database).builder();
        LOGGER.debug(sql);
        return super.update(connection, sql);
    }

    public int userDatabase(Connection connection, String database) throws TransactionException {
        var use = DynamicSqlBuilder.sql().use(database).builder();
        LOGGER.debug(use);
        return super.update(connection, use);
    }

    public List<String> showTables(Connection connection) {
        String sql = DynamicSqlBuilder.sql().show().tables().builder();
        LOGGER.debug(sql);
        return super.select(connection, sql, String.class);
    }

    public <E> void createTable(Connection connection, Class<E> entity) throws TransactionException {
        BeanInitialization beanInitialization = new BeanInitialization();
        var classInfo = beanInitialization.getRegisterBean(entity);
        var entityInfo = new EntityInfo<E>();
        SqlEntity sqlEntity = (SqlEntity) classInfo.getClassAnnotations().get(SqlEntity.class);
        var entityClass = classInfo.getClazz();
        var table = sqlEntity.table();
        if (table.isBlank()) {
            table = entityClass.getSimpleName().toLowerCase();
        }
        entityInfo.setTable(table);
        entityInfo.setCharset(sqlEntity.charset());
        entityInfo.setEngine(sqlEntity.engine());

        var columns = EntityResolver.resolveClassInfo(classInfo);
        entityInfo.setColumnInfoList(columns);
        createTable(connection, entityInfo);
    }

    public <E> void createTable(Connection connection, EntityInfo<E> entityInfo) throws TransactionException {
        var columns = entityInfo.getColumnInfoList();
        DynamicSqlBuilder sqlBuilder = DynamicSqlBuilder.sql().create()
                .tableIfNotExists(entityInfo.getTable(), true).leftParenthesis();
        if (columns != null && !columns.isEmpty()) {
            int size = columns.size();
            for (int i = 0; i < size - 1; i++) {
                var iColumn = columns.get(i);
                if (iColumn != null) {
                    buildCreateTableColumns(sqlBuilder, iColumn);
                    sqlBuilder.extra(", ");
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
        var sql = sqlBuilder.builder();
        LOGGER.debug(sql);
        super.update(connection, sql);
    }

    private void buildCreateTableColumns(DynamicSqlBuilder sqlBuilder, ColumnInfo iColumn) {
        var type = iColumn.getJdbcType().getName();
        if (iColumn.getJdbcType().equals(JDBCType.VARCHAR)) {
            type = "VARCHAR(" + iColumn.getCharMaxLength() + ")";
        }
        sqlBuilder.extra("`").extra(iColumn.getColumnName()).extra("` ").extra(type).extra(" ");
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

    public void dropTable(Connection connection, String table) throws TransactionException {
        String sql = DynamicSqlBuilder.sql().drop().tableIfExists(table, true).builder();
        LOGGER.debug(sql);
        super.update(connection, sql);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DdlRepositoryHandler.class);
}
