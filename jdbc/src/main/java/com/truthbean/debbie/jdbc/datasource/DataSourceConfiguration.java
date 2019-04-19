package com.truthbean.debbie.jdbc.datasource;

import com.truthbean.debbie.jdbc.transaction.TransactionIsolationLevel;

import java.util.HashMap;
import java.util.Map;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class DataSourceConfiguration implements Cloneable {
    private String driverName;
    private String url;
    private String user;
    private String password;

    private Boolean autoCommit;
    private TransactionIsolationLevel defaultTransactionIsolationLevel;

    private Map<String, Object> driverProperties;

    public DataSourceConfiguration() {
    }

    public DataSourceConfiguration(DataSourceConfiguration configuration) {
        this.driverName = configuration.driverName;
        this.url = configuration.url;
        this.user = configuration.user;
        this.password = configuration.password;

        this.autoCommit = configuration.autoCommit;
        this.defaultTransactionIsolationLevel = configuration.defaultTransactionIsolationLevel;

        this.driverProperties = new HashMap<>();
        this.driverProperties.putAll(configuration.driverProperties);
    }

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

    public Boolean getAutoCommit() {
        return autoCommit;
    }

    public void setAutoCommit(Boolean autoCommit) {
        this.autoCommit = autoCommit;
    }

    public TransactionIsolationLevel getDefaultTransactionIsolationLevel() {
        return defaultTransactionIsolationLevel;
    }

    public void setDefaultTransactionIsolationLevel(TransactionIsolationLevel defaultTransactionIsolationLevel) {
        this.defaultTransactionIsolationLevel = defaultTransactionIsolationLevel;
    }

    public Map<String, Object> getDriverProperties() {
        return driverProperties;
    }

    public void setDriverProperties(Map<String, Object> driverProperties) {
        this.driverProperties = driverProperties;
    }

    @Override
    protected DataSourceConfiguration clone() {
        DataSourceConfiguration clone;
        try {
            return (DataSourceConfiguration) super.clone();
        } catch (CloneNotSupportedException e) {
            clone = new DataSourceConfiguration(this);
        }
        return clone;
    }
}
