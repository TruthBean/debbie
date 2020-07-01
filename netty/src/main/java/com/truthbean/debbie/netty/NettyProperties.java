/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.netty;

import com.truthbean.debbie.bean.DebbieApplicationContext;
import com.truthbean.debbie.bean.BeanScanConfiguration;
import com.truthbean.debbie.mvc.MvcConfiguration;
import com.truthbean.debbie.mvc.MvcProperties;
import com.truthbean.debbie.properties.ClassesScanProperties;
import com.truthbean.debbie.server.BaseServerProperties;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/4/30 22:37.
 */
public class NettyProperties extends BaseServerProperties<NettyConfiguration> {

    private NettyConfiguration configuration;

    @Override
    public NettyConfiguration toConfiguration(DebbieApplicationContext applicationContext) {
        if (configuration != null) {
            return configuration;
        }

        var classLoader = applicationContext.getClassLoader();

        configuration = new NettyConfiguration(classLoader);

        BeanScanConfiguration beanConfiguration = ClassesScanProperties.toConfiguration(classLoader);
        MvcConfiguration mvcConfiguration = MvcProperties.toConfiguration(classLoader);
        configuration.copyFrom(mvcConfiguration);
        configuration.copyFrom(beanConfiguration);

        NettyProperties properties = new NettyProperties();
        properties.loadAndSet(properties, configuration);

        return configuration;
    }
}
