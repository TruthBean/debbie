/**
 * Copyright (c) 2024 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.properties;

import java.util.UUID;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/4/20 22:23.
 */
public interface DebbieConfiguration extends AutoCloseable {

    boolean isEnable();

    default String getUuid() {
        return UUID.randomUUID().toString();
    }

    String getProfile();

    String getCategory();

    <T extends DebbieConfiguration> T copy();

    @Override
    void close();
}
