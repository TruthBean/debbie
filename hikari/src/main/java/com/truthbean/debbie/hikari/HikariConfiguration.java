package com.truthbean.debbie.hikari;

import com.truthbean.debbie.core.data.transformer.text.BooleanTransformer;
import com.truthbean.debbie.core.data.transformer.text.LongTransformer;
import com.truthbean.debbie.core.properties.BeanConfiguration;
import com.truthbean.debbie.core.properties.PropertyInject;
import com.truthbean.debbie.jdbc.datasource.DataSourceConfiguration;
import com.truthbean.debbie.jdbc.datasource.DataSourceProperties;
import com.truthbean.debbie.jdbc.transaction.TransactionIsolationLevel;
import com.zaxxer.hikari.HikariConfig;

import javax.sql.DataSource;
import java.util.concurrent.ThreadFactory;

/**
 * https://github.com/brettwooldridge/HikariCP/blob/dev/README.md
 *
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/05/17 22:28.
 */
@BeanConfiguration(keyPrefix = "debbie.datasource.hikari.")
public class HikariConfiguration extends DataSourceConfiguration {

    private final HikariConfig hikariConfig;

    @PropertyInject(value = "datasource-class-name")
    private String dataSourceClassName;

    @PropertyInject(value = "jdbc-url")
    private String jdbcUrl;

    @PropertyInject(value = "username")
    private String username;

    @PropertyInject(value = "password")
    private String hikariPassword;

    @PropertyInject(value = "auto-commit", transformer = BooleanTransformer.class)
    private Boolean hikariAutoCommit;

    /**
     *  Lowest acceptable connection timeout is 250 ms. Default: 30000 (30 seconds)
     */
    @PropertyInject(value = "connection-timeout", transformer = LongTransformer.class)
    private long connectionTimeout = 30000;

    /**
     *  The minimum allowed value is 10000ms (10 seconds). Default: 600000 (10 minutes)
     */
    @PropertyInject(value = "idle-timeout")
    private long idleTimeout = 600000;

    /**
     *  Default: 1800000 (30 minutes)
     */
    @PropertyInject(value = "max-lifetime")
    private long maxLifetime = 1800000;

    @PropertyInject(value = "connection-test-query")
    private String connectionTestQuery;

    /**
     * Default: same as maximumPoolSize
     */
    @PropertyInject(value = "min-idle")
    private int minimumIdle;

    /**
     * https://github.com/brettwooldridge/HikariCP/wiki/About-Pool-Sizing
     */
    @PropertyInject(value = "max-pool-size")
    private int maximumPoolSize = 10;

    /**
     * https://github.com/brettwooldridge/HikariCP/wiki/Dropwizard-Metrics
     */
    @PropertyInject(value = "metric-registry")
    private String metricRegistry;

    /**
     * https://github.com/brettwooldridge/HikariCP/wiki/Dropwizard-HealthChecks
     */
    @PropertyInject(value = "health-check-registry")
    private String healthCheckRegistry;

    @PropertyInject(value = "pool-name")
    private String poolName;

    @PropertyInject(value = "init-fail-timeout")
    private long initializationFailTimeout = 1;

    @PropertyInject(value = "isolate-internal-queries")
    private boolean isolateInternalQueries = false;

    @PropertyInject(value = "allow-pool-suspension")
    private boolean allowPoolSuspension = false;

    @PropertyInject(value = "read-only")
    private boolean readOnly = false;

    @PropertyInject(value = "register-mbeans")
    private boolean registerMbeans = false;

    @PropertyInject(value = "catalog")
    private String catalog;

    @PropertyInject(value = "connection-init-sql")
    private String connectionInitSql;

    @PropertyInject(value = "driver-class-name")
    private String driverClassName;

    @PropertyInject(value = "transaction-isolation")
    private TransactionIsolationLevel transactionIsolation;

    /**
     *  Lowest acceptable validation timeout is 250 ms. Default: 5000
     */
    @PropertyInject(value = "validation-timeout")
    private long validationTimeout = 5000;

    /**
     * This property controls the amount of time that a connection can be out of the pool before a message is logged indicating a possible connection leak.
     * A value of 0 means leak detection is disabled. Lowest acceptable value for enabling leak detection is 2000 (2 seconds). Default: 0
     */
    @PropertyInject(value = "leak-detection-threshold")
    private long leakDetectionThreshold = 0;

    @PropertyInject(value = "schema")
    private String schema;

    @PropertyInject(value = "datasource")
    private DataSource dataSource;

    @PropertyInject(value = "thread-factory")
    private ThreadFactory threadFactory;

    public HikariConfiguration() {
        super(DataSourceProperties.toConfiguration());
        this.hikariConfig = new HikariConfig();
        super.setDataSourceFactoryClass(HikariDataSourceFactory.class);
    }

    public HikariConfig getHikariConfig() {
        return hikariConfig;
    }

    public String getDataSourceClassName() {
        return dataSourceClassName;
    }

    public void setDataSourceClassName(String dataSourceClassName) {
        this.dataSourceClassName = dataSourceClassName;
        if (dataSourceClassName != null && !dataSourceClassName.isBlank())
            this.hikariConfig.setDataSourceClassName(dataSourceClassName);
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
        if (jdbcUrl != null && !jdbcUrl.isBlank())
            this.hikariConfig.setJdbcUrl(jdbcUrl);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        if (username != null && !username.isBlank())
            this.hikariConfig.setUsername(username);
    }

    public String getHikariPassword() {
        return hikariPassword;
    }

    public void setHikariPassword(String password) {
        this.hikariPassword = password;
        if (password != null && !password.isBlank())
            this.hikariConfig.setPassword(password);
    }

    public Boolean getHikariAutoCommit() {
        return hikariAutoCommit;
    }

    public void setHikariAutoCommit(Boolean autoCommit) {
        this.hikariAutoCommit = autoCommit;
        if (autoCommit != null)
            this.hikariConfig.setAutoCommit(autoCommit);
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
        if (driverClassName != null && !driverClassName.isBlank())
            this.hikariConfig.setDriverClassName(driverClassName);
    }

    public TransactionIsolationLevel getTransactionIsolation() {
        return transactionIsolation;
    }

    public void setTransactionIsolation(TransactionIsolationLevel transactionIsolation) {
        this.transactionIsolation = transactionIsolation;
        if (transactionIsolation != null)
            this.hikariConfig.setTransactionIsolation(transactionIsolation.name());
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
        if (schema != null)
            this.hikariConfig.setSchema(schema);
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        if (dataSource != null)
            this.hikariConfig.setDataSource(dataSource);
    }

    public long getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(long connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
        this.hikariConfig.setConnectionTimeout(connectionTimeout);
    }

    public long getIdleTimeout() {
        return idleTimeout;
    }

    public void setIdleTimeout(long idleTimeout) {
        this.idleTimeout = idleTimeout;
        this.hikariConfig.setIdleTimeout(idleTimeout);
    }

    public long getMaxLifetime() {
        return maxLifetime;
    }

    public void setMaxLifetime(long maxLifetime) {
        this.maxLifetime = maxLifetime;
        this.hikariConfig.setMaxLifetime(maxLifetime);
    }

    public String getConnectionTestQuery() {
        return connectionTestQuery;
    }

    public void setConnectionTestQuery(String connectionTestQuery) {
        this.connectionTestQuery = connectionTestQuery;
        if (connectionTestQuery != null && !connectionTestQuery.isBlank())
            this.hikariConfig.setConnectionTestQuery(connectionTestQuery);
    }

    public long getMinimumIdle() {
        return minimumIdle;
    }

    public void setMinimumIdle(int minimumIdle) {
        this.minimumIdle = minimumIdle;
        this.hikariConfig.setMinimumIdle(minimumIdle);
    }

    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }

    public void setMaximumPoolSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
        this.hikariConfig.setMaximumPoolSize(maximumPoolSize);
    }

    public String getMetricRegistry() {
        return metricRegistry;
    }

    public void setMetricRegistry(String metricRegistry) {
        this.metricRegistry = metricRegistry;
        if (metricRegistry != null && !metricRegistry.isBlank())
            this.hikariConfig.setMetricRegistry(metricRegistry);
    }

    public String getHealthCheckRegistry() {
        return healthCheckRegistry;
    }

    public void setHealthCheckRegistry(String healthCheckRegistry) {
        this.healthCheckRegistry = healthCheckRegistry;
        if (healthCheckRegistry != null && !healthCheckRegistry.isBlank())
            this.hikariConfig.setHealthCheckRegistry(healthCheckRegistry);
    }

    public String getPoolName() {
        return poolName;
    }

    public void setPoolName(String poolName) {
        this.poolName = poolName;
        if (poolName != null && !poolName.isBlank())
            this.hikariConfig.setPoolName(poolName);
    }

    public long getInitializationFailTimeout() {
        return initializationFailTimeout;
    }

    public void setInitializationFailTimeout(long initializationFailTimeout) {
        this.initializationFailTimeout = initializationFailTimeout;
        this.hikariConfig.setInitializationFailTimeout(initializationFailTimeout);
    }

    public boolean isIsolateInternalQueries() {
        return isolateInternalQueries;
    }

    public void setIsolateInternalQueries(boolean isolateInternalQueries) {
        this.isolateInternalQueries = isolateInternalQueries;
        this.hikariConfig.setIsolateInternalQueries(isolateInternalQueries);
    }

    public boolean isAllowPoolSuspension() {
        return allowPoolSuspension;
    }

    public void setAllowPoolSuspension(boolean allowPoolSuspension) {
        this.allowPoolSuspension = allowPoolSuspension;
        this.hikariConfig.setAllowPoolSuspension(allowPoolSuspension);
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
        this.hikariConfig.setReadOnly(readOnly);
    }

    public boolean isRegisterMbeans() {
        return registerMbeans;
    }

    public void setRegisterMbeans(boolean registerMbeans) {
        this.registerMbeans = registerMbeans;
        this.hikariConfig.setRegisterMbeans(registerMbeans);
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
        if (catalog != null && !catalog.isBlank())
            this.hikariConfig.setCatalog(catalog);
    }

    public String getConnectionInitSql() {
        return connectionInitSql;
    }

    public void setConnectionInitSql(String connectionInitSql) {
        this.connectionInitSql = connectionInitSql;
        if (connectionInitSql != null && !connectionInitSql.isBlank())
            this.hikariConfig.setConnectionInitSql(connectionInitSql);
    }

    public long getValidationTimeout() {
        return validationTimeout;
    }

    public void setValidationTimeout(long validationTimeout) {
        this.validationTimeout = validationTimeout;
        this.hikariConfig.setValidationTimeout(validationTimeout);
    }

    public long getLeakDetectionThreshold() {
        return leakDetectionThreshold;
    }

    public void setLeakDetectionThreshold(long leakDetectionThreshold) {
        this.leakDetectionThreshold = leakDetectionThreshold;
        this.hikariConfig.setLeakDetectionThreshold(leakDetectionThreshold);
    }

    public ThreadFactory getThreadFactory() {
        return threadFactory;
    }

    public void setThreadFactory(ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
        if (threadFactory != null)
            this.hikariConfig.setThreadFactory(threadFactory);
    }
}
