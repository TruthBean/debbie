package com.truthbean.code.debbie.jdbc.repository;

import com.truthbean.code.debbie.core.bean.BeanInitializationHandler;
import com.truthbean.code.debbie.jdbc.annotation.SqlEntity;
import com.truthbean.code.debbie.jdbc.column.ColumnInfo;
import com.truthbean.code.debbie.jdbc.datasource.DataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/4/3 23:23.
 */
public class DmlRepositoryHandler extends RepositoryHandler {

    public DmlRepositoryHandler(DataSourceFactory dataSourceFactory) {
        super(dataSourceFactory);
    }

    public DmlRepositoryHandler(Connection connection) {
        super(connection);
    }

    public int deleteBy(String column, String value) {
        String sql = DynamicSqlBuilder.sql().delete().from("type").where().eq(column, "?").builder();
        return update(sql, value);
    }

    public <E, K> K insert(E entity) {
        var entityInfo = super.resolveEntity(entity);
        var table = entityInfo.getTable();
        var columns = entityInfo.getColumnInfoList();

        List<String> columnNames = new LinkedList<>();
        List<Object> columnValues = new LinkedList<>();
        List<Object> signs = new LinkedList<>();
        for (ColumnInfo column : columns) {
            var value = column.getValue();
            if (value != null) {
                columnValues.add(column.getValue());
                columnNames.add(column.getColumnName());
                signs.add("?");
            }

        }

        var sql = DynamicSqlBuilder.sql().insert().extra(table).leftParenthesis()
                .columns(columnNames).rightParenthesis().values(signs).builder();
        LOGGER.debug(sql);
        var generatedKeys = entityInfo.getPrimaryKeyType() != null;
        return (K) super.insert(sql, generatedKeys, entityInfo.getPrimaryKeyClass(), columnValues);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DmlRepositoryHandler.class);
}
