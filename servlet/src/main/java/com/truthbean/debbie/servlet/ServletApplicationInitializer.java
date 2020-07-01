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
import com.truthbean.debbie.boot.DebbieApplicationFactory;
import com.truthbean.debbie.watcher.Watcher;
import com.truthbean.Logger;
import com.truthbean.logger.LoggerFactory;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;
import java.util.Set;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-01-07 22:30.
 */
@HandlesTypes(value = {Watcher.class})
public class ServletApplicationInitializer extends DebbieApplicationFactory implements ServletContainerInitializer {

    private final DebbieApplicationContext applicationContext;

    public ServletApplicationInitializer() {
        super(ServletApplicationInitializer.class);
        if (debbieApplication == null) {
            LOGGER.debug("run servlet module without application");
            applicationContext = getApplicationContext();
            super.config(ServletApplicationInitializer.class);
            super.callStarter();
        } else {
            applicationContext = debbieApplicationFactory;
        }
    }

    @Override
    public void onStartup(Set<Class<?>> classes, ServletContext ctx) throws ServletException {
        LOGGER.info("ServletContainerInitializer onStartup ...");
        var handler = new ServletContextHandler(ctx, applicationContext);
        handler.registerRouter();
        handler.registerFilter(ctx);

        // if run with war package
        if (debbieApplication == null)
            super.postCallStarter();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ServletApplicationInitializer.class);
}
