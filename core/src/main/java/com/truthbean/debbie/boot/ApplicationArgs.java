/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.boot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-10-22 22:10
 */
public class ApplicationArgs {
    private final String[] args;

    private final Map<String, String> envArgs = new HashMap<>();

    public ApplicationArgs(String... args) {
        this.args = args;
        if (this.args.length > 0) {
            for (String arg : this.args) {
                if (arg.startsWith("-D")) {
                    var prop = arg.substring(2).split("=");
                    if (prop.length == 2) {
                        envArgs.put(prop[0], prop[1]);
                    }
                }
            }
        }
        if (!envArgs.isEmpty()) {
            envArgs.forEach(System::setProperty);
        }
    }

    public Map<String, String> getEnvArgs() {
        return envArgs;
    }

    public String[] getArgs() {
        return args;
    }

}
