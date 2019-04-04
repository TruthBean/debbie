package com.truthbean.code.debbie.jdbc.repository;

import com.truthbean.code.debbie.core.bean.BeanInitializationHandler;
import com.truthbean.code.debbie.jdbc.annotation.SqlEntity;
import com.truthbean.code.debbie.jdbc.column.ColumnInfo;
import com.truthbean.code.debbie.jdbc.column.JdbcColumnHandler;
import com.truthbean.code.debbie.jdbc.datasource.DataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.JDBCType;
import java.util.ArrayList;
import java.util.List;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/4/3 23:31.
 */
public class DdlRepositoryHandler extends RepositoryHandler {

    public DdlRepositoryHandler(DataSourceFactory dataSourceFactory) {
        super(dataSourceFactory);
    }

    public DdlRepositoryHandler(Connection connection) {
        super(connection);
    }

    public long createDatabase(String database) {
        String sql = DynamicSqlBuilder.sql().create().database(database).builder();
        LOGGER.debug(sql);
        return update(sql);
    }

    public List<String> showDatabases() {
        String sql = DynamicSqlBuilder.sql().show().databases().builder();
        LOGGER.debug(sql);
        return select(sql, String.class);
    }

    public long dropDatabase(String database) {
        String sql = DynamicSqlBuilder.sql().drop().database(database).builder();
        LOGGER.debug(sql);
        return update(sql);
    }

    public long userDatabase(String database) {
        var use = DynamicSqlBuilder.sql().use(database).builder();
        LOGGER.debug(use);
        return update(use);
    }

    public List<String> showTables() {
        String sql = DynamicSqlBuilder.sql().show().tables().builder();
        LOGGER.debug(sql);
        return select(sql, String.class);
    }

    public <E> long createTable(Class<E> entity) {
        var classInfo = BeanInitializationHandler.getRegisterBean(entity);
        var entityInfo = new EntityInfo();
        SqlEntity sqlEntity = (SqlEntity) classInfo.getClassAnnotations().get(SqlEntity.class);
        var entityClass = classInfo.getClazz();
        var table = sqlEntity.table();
        if ("".equals(table.trim())) {
            table = entityClass.getSimpleName().toLowerCase();
        }
        entityInfo.setTable(table);
        entityInfo.setCharset(sqlEntity.charset());
        entityInfo.setEngine(sqlEntity.engine());

        var columns = super.getColumnInfo(classInfo);
        entityInfo.setColumnInfoList(columns);
        return createTable(entityInfo);
    }

    public long createTable(EntityInfo entityInfo) {
        var columns = entityInfo.getColumnInfoList();
        DynamicSqlBuilder sqlBuilder = DynamicSqlBuilder.sql().create().table(entityInfo.getTable(), true).leftParenthesis();
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
        if (!"".equals(engine.trim())) {
            sqlBuilder.engine(engine);
        }

        var charset = entityInfo.getCharset();
        if (!"".equals(charset.trim())) {
            sqlBuilder.defaultCharset(charset);
        }
        var sql = sqlBuilder.builder();
        LOGGER.debug(sql);
        return super.update(sql);
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

        if (iColumn.getDefaultValue() != null) {
            sqlBuilder.defaultValue(iColumn.getDefaultValue());
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

    public long dropTable(String table) {
        String sql = DynamicSqlBuilder.sql().drop().table(table, false).builder();
        LOGGER.debug(sql);
        return update(sql);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DdlRepositoryHandler.class);
}
