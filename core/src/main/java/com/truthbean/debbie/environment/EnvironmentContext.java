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

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.0
 * Created on 2021-02-22 21:56
 */
public interface EnvironmentContext {

    String SYSTEM = "sys";

    String JVM = "jvm";

    Environment getEnvironment(String profile);

    boolean hashProfile(String profile);

    List<Environment> getEnvironments();

    Set<String> getProfiles();

    Map<String, Environment> getProfileEnvironments();
}
