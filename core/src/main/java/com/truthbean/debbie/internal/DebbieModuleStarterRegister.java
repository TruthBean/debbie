/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.internal;

import com.truthbean.debbie.boot.DebbieModuleStarter;
import com.truthbean.debbie.spi.SpiLoader;

import java.util.Set;
import java.util.TreeSet;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.0
 * Created on 2021-03-10 14:00
 */
public class DebbieModuleStarterRegister {

    private volatile Set<DebbieModuleStarter> debbieModuleStarters;
    private static volatile DebbieModuleStarterRegister moduleStarterRegister;

    private DebbieModuleStarterRegister() {
    }

    public static DebbieModuleStarterRegister getInstance() {
        if (moduleStarterRegister == null) {
            synchronized (DebbieModuleStarterRegister.class) {
                if (moduleStarterRegister == null) {
                    moduleStarterRegister = new DebbieModuleStarterRegister();
                }
            }
        }
        return moduleStarterRegister;
    }

    private void loadModuleStarter() {
        if (this.debbieModuleStarters == null) {
            Set<DebbieModuleStarter> debbieModuleStarterSet = SpiLoader.loadProviders(DebbieModuleStarter.class);
            if (!debbieModuleStarterSet.isEmpty()) {
                this.debbieModuleStarters = new TreeSet<>(debbieModuleStarterSet);
            }
        }
    }

    public Set<DebbieModuleStarter> getDebbieModuleStarters() {
        loadModuleStarter();
        return new TreeSet<>(debbieModuleStarters);
    }

    public void registerModuleStarter(DebbieModuleStarter moduleStarter) {
        loadModuleStarter();
        this.debbieModuleStarters.add(moduleStarter);
    }
}
