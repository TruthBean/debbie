/**
 * Copyright (c) 2024 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.lang;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.0
 * Created on 2021-04-29 16:57
 */
public class JvmHelper {
    protected JvmHelper() {
    }

    public static Map<String, String> getInputArguments() {
        Map<String, String> result = new HashMap<>();
        List<String> inputArgs = ManagementFactory.getRuntimeMXBean().getInputArguments();
        for (String inputArg : inputArgs) {
            LOGGER.trace(inputArg);
            if (inputArg.startsWith("-D")) {
                String[] split = inputArg.substring(2).split("=");
                result.put(split[0], split[1]);
            }
        }
        return result;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(JvmHelper.class);
}
