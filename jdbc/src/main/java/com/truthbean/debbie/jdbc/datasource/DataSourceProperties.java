package com.truthbean.debbie.jdbc.datasource;

import com.truthbean.debbie.core.properties.AbstractProperties;
import com.truthbean.debbie.jdbc.transaction.TransactionIsolationLevel;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-03-19 12:49.
 */
public class DataSourceProperties extends AbstractProperties {
    private DataSourceConfiguration configuration;

    //===========================================================================
    private static final String DRIVER_NAME_KEY = "debbie.datasource.driver-name";
    private static final String URL_KEY = "debbie.datasource.url";
    private static final String USER_KEY = "debbie.datasource.user";
    private static final String PASSWORD_KEY = "debbie.datasource.password";

    private static final String AUTO_COMMIT = "debbie.datasource.auto-commit";
    private static final String DEFAULT_TRANSACTION_ISOLATION_LEVEL = "debbie.datasource.default-transaction-isolation-level";

    // TODO
    private static final String DRIVER_PROPERTIES_PREFIX = "debbie.datasource.driver.";

    //===========================================================================
    public DataSourceProperties() {
        this.configuration = new DataSourceConfiguration();

        String driverName = getStringValue(DRIVER_NAME_KEY, "com.mysql.jdbc.Driver");
        this.configuration.setDriverName(driverName);

        String url = getStringValue(URL_KEY, "jdbc:mysql://localhost:3306");
        this.configuration.setUrl(url);

        String user = getStringValue(USER_KEY, "root");
        this.configuration.setUser(user);

        String password = getStringValue(PASSWORD_KEY, null);
        this.configuration.setPassword(password);

        Boolean autoCommit = getBooleanValue(AUTO_COMMIT, false);
        this.configuration.setAutoCommit(autoCommit);

        TransactionIsolationLevel transactionIsolationLevel =
                getTransactionIsolationLevelValue(DEFAULT_TRANSACTION_ISOLATION_LEVEL, TransactionIsolationLevel.READ_COMMITTED);
        this.configuration.setDefaultTransactionIsolationLevel(transactionIsolationLevel);
    }

    public TransactionIsolationLevel getTransactionIsolationLevelValue(String key, TransactionIsolationLevel defaultValue) {
        String stringValue = getStringValue(key, defaultValue.name().toUpperCase());
        return TransactionIsolationLevel.valueOf(stringValue);
    }

    public DataSourceConfiguration toConfiguration() {
        return configuration;
    }

}
