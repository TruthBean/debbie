package com.truthbean.debbie.jdbc.datasource.multi;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.jdbc.datasource.*;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author TruthBean
 * @since 0.5.3
 */
public class DefaultMultiDataSourceFactory implements MultiDataSourceFactory {

    private final Map<String, DataSource> dataSourceMap = new HashMap<>();

    public DefaultMultiDataSourceFactory() {
    }

    @Override
    public DataSourceFactory factory(DataSource dataSource) {
        int size = dataSourceMap.size();
        if (!dataSourceMap.containsKey(defaultDataSourceKey)) {
            dataSourceMap.put(defaultDataSourceKey, dataSource);
        }
        dataSourceMap.put(dataSourceKeyPrefix + size, dataSource);
        return new DefaultDataSourceFactory().factory(dataSource);
    }

    @Override
    public DataSourceFactory factoryDefault(DataSource dataSource) {
        dataSourceMap.put(defaultDataSourceKey, dataSource);
        return new DefaultDataSourceFactory().factory(dataSource);
    }

    @Override
    public DataSourceFactory factory(String name) {
        DataSource dataSource = dataSourceMap.get(name);
        return new DefaultDataSourceFactory().factory(dataSource);
    }

    @Override
    public DataSourceFactory factory(DataSourceConfiguration configuration) {
        int size = dataSourceMap.size();
        DataSource dataSource = new DefaultDataSource(configuration);
        if (!dataSourceMap.containsKey(defaultDataSourceKey)) {
            dataSourceMap.put(defaultDataSourceKey, dataSource);
        }
        dataSourceMap.put(dataSourceKeyPrefix + size, dataSource);
        return new DefaultDataSourceFactory().factory(dataSource);
    }

    @Override
    public String getName() {
        return "defaultMultiDataSourceFactory";
    }

    @Override
    public DataSource getDataSource() {
        return dataSourceMap.get(defaultDataSourceKey);
    }

    @Override
    public DataSource getDataSource(String name) {
        return dataSourceMap.get(name);
    }

    @Override
    public DataSource getDefaultDataSource() {
        return dataSourceMap.get(defaultDataSourceKey);
    }

    @Override
    public DataSourceDriverName getDriverName() {
        return null;
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public Map<String, DataSourceFactory> getAllDataSourceFactory() {
        return null;
    }

    @Override
    public Map<String, DataSource> getAllDataSource() {
        return dataSourceMap;
    }

    private static final Logger logger = LoggerFactory.getLogger(DefaultMultiDataSourceFactory.class);
}
