package com.truthbean.debbie.graalvm.service;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;

/**
 * @author TruthBean
 * @since 0.5.3
 * Created on 2021/11/25 17:43.
 */
public class LogService {

    public void log(String message) {
        LOGGER.info(message);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(LogService.class);
}
