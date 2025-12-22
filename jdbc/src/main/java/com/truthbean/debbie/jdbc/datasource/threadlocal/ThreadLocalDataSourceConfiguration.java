package com.truthbean.debbie.jdbc.datasource.threadlocal;

import com.truthbean.debbie.jdbc.datasource.DataSourceConfiguration;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.0
 */
public class ThreadLocalDataSourceConfiguration extends DataSourceConfiguration {

    private boolean threadLocal = true;

    public ThreadLocalDataSourceConfiguration(boolean enable) {
        super(enable);
    }

    public ThreadLocalDataSourceConfiguration(DataSourceConfiguration configuration) {
        super(configuration);
    }

    public void setThreadLocal(boolean threadLocal) {
        this.threadLocal = threadLocal;
    }

    public boolean isThreadLocal() {
        return threadLocal;
    }
}
