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

import com.truthbean.Logger;
import com.truthbean.logger.LoggerFactory;

/**
 * @author TruthBean
 * @since 0.0.2
 * Created on 2019/08/20 22:49.
 */
public class OsUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(OsUtils.class);
    private static final String OS_NAME;
    private static final boolean IS_LINUX_OS;
    private static final boolean IS_WIN_OS;
    private static final boolean IS_MAC_OS;

    private static final String OS_ARCH;

    private static final String JAVA_VERSION;
    private static final int JVM_VERSION;

    static {
        String osName = System.getProperty("os.name").toLowerCase();
        LOGGER.debug(() -> " system property:" + System.getProperties().getProperty("os.name"));
        String javaVersion = System.getProperty("java.version");
        LOGGER.debug(() -> " java version:" + javaVersion);
        String osArch = System.getProperty("os.arch");
        LOGGER.debug(() -> " os arch:" + osArch);
        String jvmVersion = System.getProperty("java.vm.specification.version");
        int version = 0;
        try {
            version = Integer.parseInt(jvmVersion);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        JVM_VERSION = version;

        osName = osName.toLowerCase();
        IS_LINUX_OS = osName.contains("linux");
        IS_WIN_OS = osName.contains("win");
        IS_MAC_OS = osName.contains("mac");
        OS_NAME = osName;
        JAVA_VERSION = javaVersion;
        OS_ARCH = osArch;
    }

    private OsUtils() {
    }

    public static String getOsName() {
        return OS_NAME;
    }

    public static String getJavaVersion() {
        return JAVA_VERSION;
    }

    public static boolean isIsJava11() {
        return JVM_VERSION == 11;
    }

    public static boolean isIsJava12() {
        return JVM_VERSION == 12;
    }

    public static boolean isIsJava13() {
        return JVM_VERSION == 13;
    }

    public static boolean isIsJava14() {
        return JVM_VERSION == 14;
    }

    public static boolean isIsJava15() {
        return JVM_VERSION == 15;
    }

    public static String getOsArch() {
        return OS_ARCH;
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
