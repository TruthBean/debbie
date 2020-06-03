/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author TruthBean
 * @since 0.0.2
 * Created on 2019/08/20 22:49.
 */
public class OsUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(OsUtils.class);
    private static final boolean IS_LINUX_OS;
    private static final boolean IS_WIN_OS;
    private static final boolean IS_MAC_OS;

    static {
        String osName = System.getProperty("os.name").toLowerCase();
        LOGGER.debug(" system property:" + System.getProperties().getProperty("os.name"));
        osName = osName.toLowerCase();
        IS_LINUX_OS = osName.contains("linux");
        IS_WIN_OS = osName.contains("win");
        IS_MAC_OS = osName.contains("mac");
    }

    private OsUtils() {
    }

    public static boolean isLinuxOs() {
        return IS_LINUX_OS;
    }

    public static boolean isWinOs() {
        return IS_WIN_OS;
    }

    public static boolean isMacOs() {
        return IS_MAC_OS;
    }

    public static String getLf() {
        if (isWinOs()) {
            return "\r\n";
        } else if (isLinuxOs()) {
            return "\n";
        } else {
            return "\n";
        }
    }

}
