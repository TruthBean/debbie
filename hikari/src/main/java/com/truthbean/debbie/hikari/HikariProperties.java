package com.truthbean.debbie.hikari;

import com.truthbean.debbie.core.bean.BeanFactoryHandler;
import com.truthbean.debbie.core.bean.BeanInitialization;
import com.truthbean.debbie.core.util.StringUtils;
import com.truthbean.debbie.jdbc.datasource.DataSourceConfiguration;
import com.truthbean.debbie.jdbc.datasource.DataSourceProperties;
import com.truthbean.debbie.jdbc.datasource.pool.DataSourcePoolProperties;

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
    private static final String HIKARI_X_KEY_PREFIX = "debbie.datasource.hikari.x.";
    private static final int HIKARI_X_KEY_PREFIX_LENGTH = 27;
    //=================================================================================================================

    public HikariProperties() {
        BeanInitialization initialization = new BeanInitialization();
        initialization.init(HikariConfiguration.class);
        var handler = new BeanFactoryHandler();
        handler.refreshBeans();
        configuration = handler.factory(HikariConfiguration.class);

        Map<String, String> matchedKey = getMatchedKey(HIKARI_X_KEY_PREFIX);
        matchedKey.forEach((key, value) -> {
            var k = key.substring(HIKARI_X_KEY_PREFIX_LENGTH);
            k = StringUtils.snakeCaseToCamelCaseTo(k);
            configuration.getHikariConfig().addDataSourceProperty(k, value);
        });
    }

    @Override
    public DataSourceConfiguration loadConfiguration() {
        return configuration;
    }

}
