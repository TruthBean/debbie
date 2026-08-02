/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.console;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.bean.GlobalBeanFactory;
import com.truthbean.debbie.boot.AbstractApplication;
import com.truthbean.debbie.boot.ApplicationArgs;
import com.truthbean.debbie.boot.DebbieApplication;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.Environment;

import java.time.Instant;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.0
 */
public class DebbieConsoleApplication extends AbstractApplication {
    private final Logger logger = LoggerFactory.getLogger(DebbieConsoleApplication.class);

    private ApplicationContext applicationContext;

    public DebbieConsoleApplication() {
        super();
    }

    @Override
    public boolean isEnable(Environment environment) {
        return super.isEnable(environment) && environment.getBooleanValue("debbie.console.enable", true);
    }

    @Override
    public DebbieApplication init(ApplicationContext applicationContext, ClassLoader classLoader) {
        super.setLogger(logger);
        this.applicationContext = applicationContext;
        return this;
    }

    @Override
    protected void start(Instant beforeStartTime, ApplicationArgs args) {
        super.printStartTime();
        postBeforeStart();
        GlobalBeanFactory globalBeanFactory = applicationContext.getGlobalBeanFactory();
        DebbieConsoleConfig consoleConfig = globalBeanFactory.factory(DebbieConsoleConfig.class);
        DebbieConsole debbieConsole = globalBeanFactory.factory(DebbieConsole.class);
        debbieConsole.consoleListener(consoleConfig, this);
    }

    @Override
    public void exit(Instant beforeStartTime, ApplicationArgs args) {
        super.printExitTime();
    }
}
