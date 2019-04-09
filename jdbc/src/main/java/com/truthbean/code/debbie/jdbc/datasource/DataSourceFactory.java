package com.truthbean.code.debbie.jdbc.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class DataSourceFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceFactory.class);

    private SingleDataSourceConnectionContext connectionContext;

    public DataSourceFactory(DataSourceConfiguration configuration) {
        this.connectionContext = SingleDataSourceConnectionContext.createInstance(configuration);
    }

}
