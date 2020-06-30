/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.boot;

import com.truthbean.debbie.bean.BeanFactoryContext;
import com.truthbean.debbie.properties.DebbieConfigurationFactory;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public abstract class AbstractApplicationFactory {

    private boolean useProperties = true;

    public void setUseProperties(boolean useProperties) {
        this.useProperties = useProperties;
    }

    public boolean useProperties() {
        return useProperties;
    }

    public boolean isWeb() {
        return false;
    }

    /**
     * application factory
     * @param factory configurationFactory
     * @see DebbieConfigurationFactory
     * @param applicationContext applicationContext
     * @param classLoader main class's classLoader
     * @see BeanFactoryContext
     * @return DebbieApplication
     */
    public abstract DebbieApplication factory(DebbieConfigurationFactory factory, BeanFactoryContext applicationContext,
                                              ClassLoader classLoader);

}
