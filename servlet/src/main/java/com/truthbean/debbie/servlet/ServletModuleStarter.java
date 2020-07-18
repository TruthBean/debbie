/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.servlet;

import com.truthbean.debbie.bean.DebbieApplicationContext;
import com.truthbean.debbie.boot.DebbieModuleStarter;
import com.truthbean.debbie.properties.DebbieConfigurationCenter;

/**
 * @author truthbean
 * @since 0.0.2
 */
public class ServletModuleStarter implements DebbieModuleStarter {

    @Override
    public void configure(DebbieConfigurationCenter configurationFactory, DebbieApplicationContext applicationContext) {
        configurationFactory.register(ServletProperties .class, ServletConfiguration.class);
    }

    @Override
    public int getOrder() {
        return 20;
    }
}
