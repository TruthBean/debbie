package com.truthbean.debbie.jdbc.datasource;

import java.sql.Connection;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.0.2
 * Created on 2020-04-12 22:50
 */
public class DriverConnection {
    private DataSourceDriverName driverName;
    private Connection connection;

    public DataSourceDriverName getDriverName() {
        return driverName;
    }

    public void setDriverName(DataSourceDriverName driverName) {
        this.driverName = driverName;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}
