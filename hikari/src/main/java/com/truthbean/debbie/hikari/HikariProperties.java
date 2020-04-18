package com.truthbean.debbie.hikari;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.jdbc.datasource.DataSourceConfiguration;
import com.truthbean.debbie.jdbc.datasource.DataSourceProperties;
import com.truthbean.debbie.properties.DebbieProperties;
import com.truthbean.debbie.util.StringUtils;

import java.util.Map;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/05/17 22:46.
 */
public class HikariProperties extends DataSourceProperties implements DebbieProperties<DataSourceConfiguration> {
    private HikariConfiguration configuration;

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
    }

    @Override
    public DataSourceConfiguration toConfiguration(final BeanFactoryHandler beanFactoryHandler) {
        if (configuration != null) {
            return configuration;
        }
        beanFactoryHandler.getBeanInitialization().init(HikariConfiguration.class);
        beanFactoryHandler.refreshBeans();
        configuration = beanFactoryHandler.factory(HikariConfiguration.class);

        final Map<String, String> matchedKey = getMatchedKey(HIKARI_X_KEY_PREFIX);
        matchedKey.forEach((key, value) -> {
            var k = key.substring(HIKARI_X_KEY_PREFIX_LENGTH);
            k = StringUtils.snakeCaseToCamelCaseTo(k);
            configuration.getHikariConfig().addDataSourceProperty(k, value);
        });
        return configuration;
    }
}
