/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.properties;

import com.truthbean.debbie.core.ApplicationContext;

import java.util.Map;
import java.util.Set;

import static com.truthbean.debbie.environment.EnvironmentDepositoryHolder.DEFAULT_CATEGORY;
import static com.truthbean.debbie.environment.EnvironmentDepositoryHolder.DEFAULT_PROFILE;

/**
 * @author TruthBean
 * @since 0.0.2
 */
public interface DebbieProperties<C extends DebbieConfiguration> extends AutoCloseable {

    String CATEGORIES_KEY_NAME = "_categories";

    String ENABLE_KEY_NAME = "enable";

    /**
     * @return [profile: [category: configuration]]
     */
    Map<String, Map<String, C>> getAllProfiledCategoryConfiguration(final ApplicationContext applicationContext);

    Set<String> getCategories(final String profile);

    default Set<String> getCategories() {
        return getCategories(DEFAULT_PROFILE);
    }

    C getConfiguration(final String profile, final String category, final ApplicationContext applicationContext);

    default C getProfiledConfiguration(final String profile, final ApplicationContext applicationContext) {
        return getConfiguration(profile, DEFAULT_CATEGORY, applicationContext);
    }

    default C getCategoryConfiguration(final String category, final ApplicationContext applicationContext) {
        return getConfiguration(DEFAULT_PROFILE, category, applicationContext);
    }

    default C getConfiguration(final ApplicationContext applicationContext) {
        return getConfiguration(DEFAULT_PROFILE, DEFAULT_CATEGORY, applicationContext);
    }

    default Map<String, C> getCategoryConfigurationMap(final String profile, final ApplicationContext applicationContext) {
        return getAllProfiledCategoryConfiguration(applicationContext).get(profile);
    }
}
