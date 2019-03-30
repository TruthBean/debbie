package com.truthbean.code.debbie.jdbc.datasource;

import com.truthbean.code.debbie.core.properties.AbstractProperties;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-03-19 12:49.
 */
public class DataSourceProperties extends AbstractProperties {
    private final String driverName;
    private final String url;
    private final String user;
    private final String password;

    //===========================================================================
    private static final String DRIVER_NAME_KEY = "debbie.datasource.driver-name";
    private static final String URL_KEY = "debbie.datasource.url";
    private static final String USER_KEY = "debbie.datasource.user";
    private static final String PASSWORD_KEY = "debbie.datasource.password";
    //===========================================================================
    public DataSourceProperties(){
        driverName = getStringValue(DRIVER_NAME_KEY, "com.mysql.jdbc.Driver");
        url = getStringValue(URL_KEY, "jdbc:mysql://localhost:3306");
        user = getStringValue(USER_KEY, "root");
        password = getStringValue(PASSWORD_KEY, null);

    }

    public String getDriverName() {
        return driverName;
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

}
