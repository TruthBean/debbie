package com.truthbean.debbie.jdbc.datasource;

import com.truthbean.debbie.core.properties.AbstractProperties;
import com.truthbean.debbie.jdbc.transaction.TransactionIsolationLevel;

import java.util.HashMap;
import java.util.Map;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-03-19 12:49.
 */
public class DataSourceProperties extends AbstractProperties {
    private final DataSourceConfiguration configuration;

    //===========================================================================
    private static final String DRIVER_NAME_KEY = "debbie.datasource.driver-name";
    private static final String URL_KEY = "debbie.datasource.url";
    private static final String USER_KEY = "debbie.datasource.user";
    private static final String PASSWORD_KEY = "debbie.datasource.password";

    private static final String AUTO_COMMIT = "debbie.datasource.auto-commit";
    private static final String DEFAULT_TRANSACTION_ISOLATION_LEVEL = "debbie.datasource.default-transaction-isolation-level";

    private static final String DRIVER_PROPERTIES_PREFIX = "debbie.datasource.driver.";

    //===========================================================================
    private static final DataSourceProperties INSTANCE = new DataSourceProperties();

    DataSourceProperties() {
        configuration = new DataSourceConfiguration();

        String driverName = getStringValue(DRIVER_NAME_KEY, "com.mysql.jdbc.Driver");
        configuration.setDriverName(driverName);

        String url = getStringValue(URL_KEY, "jdbc:mysql://localhost:3306");
        configuration.setUrl(url);

        String user = getStringValue(USER_KEY, "root");
        configuration.setUser(user);

        String password = getStringValue(PASSWORD_KEY, null);
        configuration.setPassword(password);

        Boolean autoCommit = getBooleanValue(AUTO_COMMIT, false);
        configuration.setAutoCommit(autoCommit);

        TransactionIsolationLevel transactionIsolationLevel =
                getTransactionIsolationLevelValue(DEFAULT_TRANSACTION_ISOLATION_LEVEL, TransactionIsolationLevel.READ_COMMITTED);
        configuration.setDefaultTransactionIsolationLevel(transactionIsolationLevel);

        Map<String, Object> driverProperties = new HashMap<>(getMatchedKey(DRIVER_PROPERTIES_PREFIX));
        configuration.setDriverProperties(driverProperties);
    }

    public TransactionIsolationLevel getTransactionIsolationLevelValue(String key, TransactionIsolationLevel defaultValue) {
        String stringValue = getStringValue(key, defaultValue.name().toUpperCase());
        return TransactionIsolationLevel.valueOf(stringValue);
    }

    public static DataSourceProperties getInstance() {
        return INSTANCE;
    }

    public static DataSourceConfiguration toConfiguration() {
        return INSTANCE.configuration;
    }

}
