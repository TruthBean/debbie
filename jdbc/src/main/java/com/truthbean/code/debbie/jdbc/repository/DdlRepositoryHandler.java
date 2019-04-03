package com.truthbean.code.debbie.jdbc.repository;

import com.truthbean.code.debbie.jdbc.datasource.DataSourceFactory;

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

    public long createDatabase(String database) {
        String sql = DynamicSqlBuilder.sql().create().database(database).builder();
        return update(sql);
    }

    public List<String> showDatabases() {
        String sql = DynamicSqlBuilder.sql().show().databases().builder();
        return select(sql, String.class);
    }

    public long dropDatabase(String database) {
        String sql = DynamicSqlBuilder.sql().drop().database(database).builder();
        return update(sql);
    }

    public List<String> showTables() {
        String sql = DynamicSqlBuilder.sql().show().tables().builder();
        return select(sql, String.class);
    }
}
