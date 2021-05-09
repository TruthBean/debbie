/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.io;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;

import java.io.File;
import java.nio.file.Path;

/**
 * @author TruthBean
 * @since 0.0.2
 */
public class PathUtils {

    private PathUtils() {
    }

    public static String getUserDir(ClassLoader classLoader) {
        String path = null;
        var resource = classLoader.getResource("");
        if (resource == null || "jar".equalsIgnoreCase(resource.getProtocol())) {
            resource = PathUtils.class.getResource("");
        }
        if (resource == null || "jar".equalsIgnoreCase(resource.getProtocol())) {
            path = System.getProperty("user.dir");
            if (path == null) {
                try {
                    path = new File("").getAbsolutePath();
                } catch (Exception e) {
                    LOGGER.error("", e);
                }
            }

        } else {
            if (LOGGER.isTraceEnabled())
                LOGGER.trace("resource: " + resource);
            try {
                path = Path.of(resource.toURI()).toString();
            } catch (Exception e) {
                // if run in jar
                LOGGER.error("", e);
                path = System.getProperty("user.dir");
                if (path == null) {
                    try {
                        path = new File("").getAbsolutePath();
                    } catch (Exception ex) {
                        LOGGER.error("", ex);
                    }
                }
            }
        }
        return path;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(PathUtils.class);
}
