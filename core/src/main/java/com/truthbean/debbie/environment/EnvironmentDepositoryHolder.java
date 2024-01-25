/**
 * Copyright (c) 2024 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.environment;

import com.truthbean.Logger;

import java.util.Optional;
import java.util.Properties;
import java.util.Set;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.0
 * Created on 2021-02-19 21:25
 */
public interface EnvironmentDepositoryHolder {

    String ORIGIN_PROFILE = "default";

    String DEFAULT_PROFILE = "all";

    String DEFAULT_CATEGORY = "default";

    default int getPriority() {
        return -1;
    }

    default String getProfile() {
        return "all";
    }

    Set<String> getProfiles();

    void setDefaultProfile(String profile);

    default String getDefaultProfile() {
        return DEFAULT_PROFILE;
    }

    EnvironmentDepositoryHolder setLogger(Logger logger);

    Logger getLogger();

    void addProperty(String name, String value);

    boolean containKey(String key);

    boolean containKey(String profile, String key);

    boolean isPropertiesEmpty();

    Properties getAllProperties();

    Optional<Environment> getEnvironment(String profile);

    default Environment getEnvironmentIfPresent(String profile) {
        return getEnvironment(profile)
                .orElseThrow(() -> new NoSuchEnvironmentException("No environment '" + profile + "' present"));
    }

    default Environment getEnvironment() {
        return getEnvironment(getDefaultProfile()).orElseGet(EmptyEnvironment::new);
    }

    Properties getAllProperties(String profile);

    void clear();
}
