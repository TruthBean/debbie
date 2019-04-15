package com.truthbean.debbie.jdbc.datasource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-03-14 11:50
 */
public final class SingleDataSourceConnectionContext extends ConnectionManager {

    private final DataSourceConfiguration configuration;

    private static SingleDataSourceConnectionContext instance;

    public static SingleDataSourceConnectionContext createInstance(DataSourceConfiguration configuration) {
        synchronized (SingleDataSourceConnectionContext.class) {
            if (instance == null) {
                instance = new SingleDataSourceConnectionContext(configuration);
            }
            return instance;
        }
    }

    private SingleDataSourceConnectionContext(DataSourceConfiguration configuration) {
        this.configuration = configuration;
        try {
            Class.forName(configuration.getDriverName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initConnectionAndBind() {
        try {
            Connection connection = DriverManager.getConnection(configuration.getUrl(), configuration.getUser(),
                    configuration.getPassword());
            super.bind(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从当前运行线程中获取与其绑定的数据连接
     * @return connection
     */
    @Override
    public Connection get() {
        Connection connection = super.get();
        if (connection == null) {
            initConnectionAndBind();
        }
        return super.get();
    }
}
