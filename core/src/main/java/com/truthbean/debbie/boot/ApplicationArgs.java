/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.boot;

import java.util.HashMap;
import java.util.Map;

/**
 * Parses command-line arguments of the form {@code -Dkey=value} and
 * applies them as system properties.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-10-22 22:10
 */
public class ApplicationArgs {
    /** the raw command-line arguments */
    private final String[] args;

    /** parsed key-value pairs extracted from {@code -D} arguments */
    private final Map<String, String> envArgs = new HashMap<>();

    /**
     * Parses the given arguments, extracting {@code -Dkey=value} pairs
     * and applying them as system properties.
     *
     * @param args command-line arguments
     */
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

    /** Returns the parsed {@code -Dkey=value} pairs. */
    public Map<String, String> getEnvArgs() {
        return envArgs;
    }

    /** Returns the raw command-line arguments. */
    public String[] getArgs() {
        return args;
    }

}
