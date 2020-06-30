package com.truthbean.debbie.jdbc.datasource;

import com.truthbean.debbie.bean.BeanFactoryContext;
import com.truthbean.debbie.jdbc.transaction.TransactionIsolationLevel;
import com.truthbean.debbie.properties.BaseProperties;
import com.truthbean.debbie.properties.DebbieProperties;
import com.truthbean.debbie.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-03-19 12:49.
 */
public class DataSourceProperties extends BaseProperties implements DebbieProperties<DataSourceConfiguration> {
    private final DataSourceConfiguration configuration;

    //=================================================================================================================
    private static final String DRIVER_NAME_KEY = "debbie.datasource.driver-name";
    private static final String URL_KEY = "debbie.datasource.url";
    private static final String USER_KEY = "debbie.datasource.user";
    private static final String PASSWORD_KEY = "debbie.datasource.password";

    private static final String AUTO_COMMIT = "debbie.datasource.auto-commit";
    private static final String DEFAULT_TRANSACTION_ISOLATION_LEVEL = "debbie.datasource.default-transaction-isolation-level";

    private static final String DRIVER_PROPERTIES_PREFIX = "debbie.datasource.driver.";

    private static final String DATA_SOURCE_FACTORY = "debbie.datasource.factory";
    //=================================================================================================================
    private static final DataSourceProperties INSTANCE = new DataSourceProperties();

    public DataSourceProperties() {
        configuration = new DataSourceConfiguration();

        String url = getStringValue(URL_KEY, "jdbc:mysql://localhost:3306");
        configuration.setUrl(url);

        String driverName = getValue(DRIVER_NAME_KEY);
        if (StringUtils.hasText(driverName)) {
            DataSourceDriverName custom = DataSourceDriverName.custom;
            custom.setDriverName(driverName);
            configuration.setDriverName(custom);
        } else if (url.startsWith("jdbc:mysql")) {
            configuration.setDriverName(DataSourceDriverName.mysql);
        } else if (url.startsWith("jdbc:oracle")) {
            configuration.setDriverName(DataSourceDriverName.oracle);
        } else if (url.startsWith("jdbc:db2")) {
            configuration.setDriverName(DataSourceDriverName.db2);
        } else if (url.startsWith("jdbc:sybase")) {
            configuration.setDriverName(DataSourceDriverName.sybase);
        } else if (url.startsWith("jdbc:mckoi")) {
            configuration.setDriverName(DataSourceDriverName.mckoi);
        } else if (url.startsWith("jdbc:h2")) {
            configuration.setDriverName(DataSourceDriverName.h2);
        } else if (url.startsWith("jdbc:ingres")) {
            configuration.setDriverName(DataSourceDriverName.ingres);
        } else if (url.startsWith("jdbc:sqlite")) {
            configuration.setDriverName(DataSourceDriverName.sqlite);
        } else if (url.startsWith("jdbc:hsqldb")) {
            configuration.setDriverName(DataSourceDriverName.hsqldb);
        } else if (url.startsWith("jdbc:postgresql")) {
            configuration.setDriverName(DataSourceDriverName.postgresql);
        } else if (url.startsWith("jdbc:jtds")) {
            configuration.setDriverName(DataSourceDriverName.jtds);
        } else if (url.startsWith("jdbc:microsoft")) {
            configuration.setDriverName(DataSourceDriverName.sqlserver);
        } else if (url.startsWith("jdbc:log4jdbc")) {
            configuration.setDriverName(DataSourceDriverName.log4jdbc);
        } else if (url.startsWith("jdbc:derby")) {
            configuration.setDriverName(DataSourceDriverName.derby);
        } else if (url.startsWith("jdbc:mariadb")) {
            configuration.setDriverName(DataSourceDriverName.mariadb);
        }

        String user = getStringValue(USER_KEY, "root");
        configuration.setUser(user);

        String password = getStringValue(PASSWORD_KEY, null);
        configuration.setPassword(password);

        Boolean autoCommit = getBooleanValue(AUTO_COMMIT, false);
        configuration.setAutoCommit(autoCommit);

        TransactionIsolationLevel transactionIsolationLevel =
                getTransactionIsolationLevelValue(DEFAULT_TRANSACTION_ISOLATION_LEVEL, TransactionIsolationLevel.TRANSACTION_READ_COMMITTED);
        configuration.setDefaultTransactionIsolationLevel(transactionIsolationLevel);

        Map<String, Object> driverProperties = new HashMap<>(getMatchedKey(DRIVER_PROPERTIES_PREFIX));
        configuration.setDriverProperties(driverProperties);

        var defaultClassName = "com.truthbean.debbie.jdbc.datasource.DefaultDataSourceFactory";
        @SuppressWarnings("unchecked")
        Class<? extends DataSourceFactory> dataSourceFactoryClass =
                (Class<? extends DataSourceFactory>) getClassValue(DATA_SOURCE_FACTORY, defaultClassName);
        configuration.setDataSourceFactoryClass(dataSourceFactoryClass);
    }

    public TransactionIsolationLevel getTransactionIsolationLevelValue(String key, TransactionIsolationLevel defaultValue) {
        String stringValue = getStringValue(key, defaultValue.name().toUpperCase());
        return TransactionIsolationLevel.valueOf(stringValue);
    }

    public static DataSourceProperties getInstance() {
        return INSTANCE;
    }

    public DataSourceConfiguration getConfiguration() {
        return configuration;
    }

    public static DataSourceConfiguration toConfiguration() {
        return INSTANCE.configuration;
    }

    @Override
    public DataSourceConfiguration toConfiguration(BeanFactoryContext applicationContext) {
        return configuration;
    }
}
