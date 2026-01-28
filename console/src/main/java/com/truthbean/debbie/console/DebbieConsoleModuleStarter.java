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

import com.truthbean.debbie.boot.DebbieModuleStarter;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.Environment;

import java.time.LocalDateTime;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.0
 */
public class DebbieConsoleModuleStarter implements DebbieModuleStarter {

    private final DebbieConsole debbieConsole = new DebbieConsole();
    private final DebbieConsoleConfig consoleConfig = new DebbieConsoleConfig();

    @Override
    public int getOrder() {
        return 40;
    }

    @Override
    public boolean enable(Environment environment) {
        consoleConfig.setEnable(environment.getBooleanValue("debbie.console.enable", true));
        return DebbieModuleStarter.super.enable(environment) && consoleConfig.isEnable();
    }

    @Override
    public void configure(ApplicationContext applicationContext) {
        DebbieModuleStarter.super.configure(applicationContext);
        // console config
        consoleConfig.setPrompt(applicationContext.getDefaultEnvironment().getStringValue("debbie.console.prompt", "console@truthbean [" + LocalDateTime.now() + "] :> "));
        // add console line consumer
        debbieConsole.addConsoleLineHandler(new DebbieConsoleHelpLineHandler());
    }

    @Override
    public void postStarter(ApplicationContext applicationContext) {
        DebbieModuleStarter.super.postStarter(applicationContext);
        debbieConsole.consoleListener(consoleConfig);
    }

    @Override
    public void release(ApplicationContext applicationContext) {
        debbieConsole.clearConsoleLineHandlers();
    }
}
