package com.truthbean.debbie.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;

/**
 * @author TruthBean
 * @since 0.0.2
 */
public class PathUtils {

    public static String getUserDir(ClassLoader classLoader) {
        String path = null;
        var resource = classLoader.getResource("");
        if (resource == null) {
            resource = PathUtils.class.getResource("");
        }
        if (resource == null) {
            path = System.getProperty("user.dir");
            if (path == null) {
                try {
                    path = new File("").getAbsolutePath();
                } catch (Exception e) {
                    LOGGER.error("", e);
                }
            }

        } else {
            LOGGER.debug("resource: " + resource);
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
