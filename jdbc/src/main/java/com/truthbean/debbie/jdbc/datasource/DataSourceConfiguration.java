/**
 * Copyright (c) 2022 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.jdbc.datasource;

import com.truthbean.debbie.jdbc.transaction.TransactionIsolationLevel;
import com.truthbean.debbie.lang.Copyable;
import com.truthbean.debbie.properties.DebbieConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class DataSourceConfiguration implements Copyable<DataSourceConfiguration>, DebbieConfiguration {
    private String name;

    private boolean enable;
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
        this.enable = configuration.enable;
        this.name = configuration.name;
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

    @Override
    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void close() {
        // todo
    }

    public void setName(String name) {
        this.name = name;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataSourceConfiguration that = (DataSourceConfiguration) o;
        return Objects.equals(name, that.name) && driverName == that.driverName && Objects.equals(url, that.url) && Objects.equals(user, that.user) && Objects.equals(password, that.password) && Objects.equals(autoCommit, that.autoCommit) && defaultTransactionIsolationLevel == that.defaultTransactionIsolationLevel && Objects.equals(driverProperties, that.driverProperties) && Objects.equals(dataSourceFactoryClass, that.dataSourceFactoryClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, driverName, url, user, password, autoCommit, defaultTransactionIsolationLevel, driverProperties, dataSourceFactoryClass);
    }

    @Override
    public DataSourceConfiguration copy() {
        DataSourceConfiguration clone;
        try {
            return (DataSourceConfiguration) super.clone();
        } catch (CloneNotSupportedException e) {
            clone = new DataSourceConfiguration(this);
        }
        return clone;
    }

    @Override
    public String toString() {
        return "{" +
                "\"name\":" + name +
                ",\"driverName\":\"" + driverName +
                "\",\"url\":\"" + url + '\"' +
                ",\"user\":\"" + user + '\"' +
                ",\"password\":\"" + password + '\"' +
                ",\"autoCommit\":" + autoCommit +
                ",\"defaultTransactionIsolationLevel\":" + defaultTransactionIsolationLevel +
                ",\"driverProperties\":" + driverProperties +
                ",\"dataSourceFactoryClass\":" + dataSourceFactoryClass +
                '}';
    }
}
