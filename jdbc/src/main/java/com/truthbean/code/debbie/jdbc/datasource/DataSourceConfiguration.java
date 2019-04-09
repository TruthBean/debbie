package com.truthbean.code.debbie.jdbc.datasource;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class DataSourceConfiguration implements Cloneable {
    private String driverName;
    private String url;
    private String user;
    private String password;

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    protected DataSourceConfiguration clone() throws CloneNotSupportedException {
        return (DataSourceConfiguration) super.clone();
    }
}
