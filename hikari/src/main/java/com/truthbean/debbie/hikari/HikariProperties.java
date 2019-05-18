package com.truthbean.debbie.hikari;

import com.truthbean.debbie.core.bean.BeanFactory;
import com.truthbean.debbie.core.bean.BeanInitialization;
import com.truthbean.debbie.jdbc.datasource.DataSourceConfiguration;
import com.truthbean.debbie.jdbc.datasource.DataSourceProperties;
import com.truthbean.debbie.jdbc.datasource.pool.DataSourcePoolProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/05/17 22:46.
 */
public class HikariProperties extends DataSourceProperties implements DataSourcePoolProperties {
    private final HikariConfiguration configuration;

    //=================================================================================================================
    /**
     * https://github.com/brettwooldridge/HikariCP/blob/dev/README.md
     *
     *  key name is snake case
     */
    private static final String HIKARI_KEY_PREFIX = "debbie.datasource.hikari.";
    private static final int HIKARI_KEY_PREFIX_LENGTH = 25;

    private static final String DATA_SOURCE_CLASS_NAME_KEY = "debbie.datasource.hikari.datasource-class-name";
    private static final String JDBC_URL_KEY = "debbie.datasource.hikari.jdbc-url";
    private static final String USERNAME_KEY = "debbie.datasource.hikari.username";
    private static final String PASSWORD_KEY = "debbie.datasource.hikari.password";
    private static final String AUTO_COMMIT_KEY = "debbie.datasource.hikari.auto-commit";
    public static final String TRANSACTION_ISOLATION_KEY = "debbie.datasource.hikari.transaction-isolation";
    //=================================================================================================================

    public HikariProperties() {
        /*Map<String, String> matchedKey = getMatchedKey(HIKARI_KEY_PREFIX);
        DataSourceConfiguration configuration = super.getConfiguration();

        Map<String, String> copy = new HashMap<>();
        String dataSourceClassName = matchedKey.get(DATA_SOURCE_CLASS_NAME_KEY);
        if (dataSourceClassName != null && !dataSourceClassName.isBlank()) {
            configuration.setDriverName(dataSourceClassName);
            copy.put("", dataSourceClassName);
        }

        String jdbcUrl = matchedKey.get(JDBC_URL_KEY);
        if (jdbcUrl != null && !jdbcUrl.isBlank()) {
            configuration.setUrl(jdbcUrl);
            copy.put(JDBC_URL_KEY.substring(HIKARI_KEY_PREFIX_LENGTH), jdbcUrl);
        }

        String username = matchedKey.get(USERNAME_KEY);
        if (username != null && !username.isBlank()) {
            configuration.setUser(username);
            copy.put(USERNAME_KEY.substring(HIKARI_KEY_PREFIX_LENGTH), username);
        }

        String password = matchedKey.get(PASSWORD_KEY);
        if (password != null && !password.isBlank()) {
            configuration.setPassword(password);
            copy.put(PASSWORD_KEY.substring(HIKARI_KEY_PREFIX_LENGTH), password);
        }

        String autoCommit = matchedKey.get(AUTO_COMMIT_KEY);
        if (autoCommit != null && !autoCommit.isBlank()) {
            configuration.setAutoCommit(Boolean.parseBoolean(autoCommit));
            copy.put(AUTO_COMMIT_KEY.substring(HIKARI_KEY_PREFIX_LENGTH), autoCommit);
        }

        String transactionIsolation = matchedKey.get(TRANSACTION_ISOLATION_KEY);
        if (transactionIsolation != null && !transactionIsolation.isBlank()) {
            configuration.setDefaultTransactionIsolationLevel(getTransactionIsolationLevelValue(transactionIsolation, TransactionIsolationLevel.READ_COMMITTED));
            copy.put(AUTO_COMMIT_KEY.substring(HIKARI_KEY_PREFIX_LENGTH), transactionIsolation);
        }

        configuration.setDataSourceFactoryClass(HikariDataSourceFactory.class);

        matchedKey.forEach((key, value) -> {
            value = StringUtils.snakeCaseToCamelCaseTo(value);
        });

        this.configuration = new HikariConfiguration(configuration, )*/

        BeanInitialization initialization = new BeanInitialization();
        initialization.init(HikariConfiguration.class);
        configuration = BeanFactory.factory(HikariConfiguration.class);
    }

    @Override
    public DataSourceConfiguration loadConfiguration() {
        return configuration;
    }

    /*public static class HikariConfiguration extends DataSourceConfiguration {
        private final Map<String, String> map;

        public HikariConfiguration() {
            super();
            map = new HashMap<>();
        }

        public HikariConfiguration(DataSourceConfiguration configuration, Map<String, String> m) {
            super(configuration);
            this.map = new HashMap<>(m);
        }

        public Map<String, String> getMap() {
            return map;
        }
    }*/
}
