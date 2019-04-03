package com.truthbean.code.debbie.jdbc.repository;

import com.truthbean.code.debbie.jdbc.datasource.DataSourceFactory;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/4/3 23:23.
 */
public class DmlRepositoryHandler extends RepositoryHandler {

    public DmlRepositoryHandler(DataSourceFactory dataSourceFactory) {
        super(dataSourceFactory);
    }

    public int deleteBy(String column, String value) {
        String sql = DynamicSqlBuilder.sql().delete().from("type").where().eq(column, "?").builder();
        return update(sql, value);
    }

    public <E> Object insert(E entity) {
        String sql = DynamicSqlBuilder.sql().insert().builder();
        // todo
        return insert(sql, null);
    }
}
