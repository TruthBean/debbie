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
import com.truthbean.debbie.bean.BeanScanConfiguration;
import com.truthbean.debbie.mvc.MvcConfiguration;
import com.truthbean.debbie.mvc.MvcProperties;
import com.truthbean.debbie.properties.ClassesScanProperties;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/10 17:39.
 */
public class ServletProperties extends MvcProperties {

    //========================================================================================

    //========================================================================================

    private static ServletConfiguration configuration;
    public static ServletConfiguration toConfiguration(ClassLoader classLoader) {
        if (configuration != null) {
            return configuration;
        }

        configuration = new ServletConfiguration(classLoader);

        final ServletProperties properties = new ServletProperties();

        MvcConfiguration webConfiguration = MvcProperties.toConfiguration(classLoader);
        configuration.copyFrom(webConfiguration);

        BeanScanConfiguration beanScanConfiguration = ClassesScanProperties.toConfiguration(classLoader);
        configuration.copyFrom(beanScanConfiguration);

        return configuration;
    }

    @Override
    public ServletConfiguration toConfiguration(DebbieApplicationContext applicationContext) {
        ClassLoader classLoader = applicationContext.getClassLoader();
        return toConfiguration(classLoader);
    }
}
