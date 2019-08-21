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

    static {
        String osName = System.getProperty("os.name").toLowerCase();
        LOGGER.debug(" system property:" + System.getProperties().getProperty("os.name"));
        IS_LINUX_OS = osName.contains("linux");
        IS_WIN_OS = osName.contains("win");
    }

    private OsUtils() {
    }

    public static boolean isLinuxOs() {
        return IS_LINUX_OS;
    }

    public static boolean isWinOs() {
        return IS_WIN_OS;
    }

}
