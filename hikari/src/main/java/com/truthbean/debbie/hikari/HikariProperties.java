/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.hikari;

import com.truthbean.debbie.bean.GlobalBeanFactory;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.properties.BaseProperties;
import com.truthbean.debbie.properties.DebbieProperties;
import com.truthbean.debbie.util.StringUtils;

import java.util.Map;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/05/17 22:46.
 */
public class HikariProperties extends BaseProperties implements DebbieProperties<HikariConfiguration> {
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
    public HikariConfiguration toConfiguration(final ApplicationContext applicationContext) {
        if (configuration != null) {
            return configuration;
        }
        applicationContext.getBeanInitialization().init(HikariConfiguration.class);
        applicationContext.refreshBeans();
        GlobalBeanFactory globalBeanFactory = applicationContext.getGlobalBeanFactory();
        configuration = globalBeanFactory.factory(HikariConfiguration.class);

        final Map<String, String> matchedKey = getMatchedKey(HIKARI_X_KEY_PREFIX);
        matchedKey.forEach((key, value) -> {
            var k = key.substring(HIKARI_X_KEY_PREFIX_LENGTH);
            k = StringUtils.snakeCaseToCamelCaseTo(k);
            configuration.getHikariConfig().addDataSourceProperty(k, value);
        });
        return configuration;
    }
}
