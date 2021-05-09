/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.jdbc.datasource;

import com.truthbean.debbie.jdbc.transaction.TransactionIsolationLevel;
import com.truthbean.debbie.properties.DebbieConfiguration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class DataSourceConfiguration implements Cloneable, DebbieConfiguration {
    private DataSourceDriverName driverName;
    private String url;
    private String user;
    private String password;

    private Boolean autoCommit;
    private TransactionIsolationLevel defaultTransactionIsolationLevel;

    private Map<String, Object> driverProperties;

    private Class<? extends DataSourceFactory> dataSourceFactoryClass;

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

        this.dataSourceFactoryClass = configuration.dataSourceFactoryClass;
    }

    public DataSourceDriverName getDriverName() {
        return driverName;
    }

    public void setDriverName(DataSourceDriverName driverName) {
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

    public Class<? extends DataSourceFactory> getDataSourceFactoryClass() {
        return dataSourceFactoryClass;
    }

    public void setDataSourceFactoryClass(Class<? extends DataSourceFactory> dataSourceFactoryClass) {
        this.dataSourceFactoryClass = dataSourceFactoryClass;
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

    @Override
    public void reset() {
    }

    @Override
    public String toString() {
        return "{" +
                "\"driverName\":" + driverName +
                ",\"url\":\"" + url + '\"' +
                ",\"user\":\"" + user + '\"' +
                ",\"password\":\"" + password + '\"' +
                ",\"autoCommit\":" + autoCommit +
                ",\"defaultTransactionIsolationLevel\":" + defaultTransactionIsolationLevel +
                ",\"driverProperties\":" + driverProperties +
                ",\"dataSourceFactoryClass\":" + dataSourceFactoryClass +
                '}';
    }
}
