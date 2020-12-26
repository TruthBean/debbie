/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.boot;

import com.truthbean.debbie.properties.BaseProperties;
import com.truthbean.logger.LogLevel;
import com.truthbean.logger.LoggerConfig;
import com.truthbean.logger.SystemOutLogger;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.2.0
 * Created on 2020-11-19 09:38
 */
public class DebbieLoggerConfig implements LoggerConfig {

    private final Properties properties;
    private final Map<String, LogLevel> levelMap = new ConcurrentHashMap<>();

    public DebbieLoggerConfig() {
        var logger = new SystemOutLogger()
                .setName("com.truthbean.debbie.properties.BaseProperties")
                .setDefaultLevel(LogLevel.DEBUG);
        BaseProperties baseProperties = new BaseProperties(logger);
        this.properties = baseProperties.getProperties();
        Runtime.getRuntime().addShutdownHook(new Thread(levelMap::clear));
    }

    @Override
    public Map<String, LogLevel> getLoggers() {
        Map<String, LogLevel> map = new HashMap<>();
        properties.forEach((key, value) -> {
            var name = (String) key;
            if (name.startsWith(LoggerConfig.LOGGING_LEVEL)) {
                var level = (String) value;
                var l = LogLevel.of(level);
                l.ifPresent(logLevel -> map.put(name.substring(14), logLevel));
            }
        });
        if (!map.isEmpty()) {
            levelMap.putAll(map);
        }
        return map;
    }

    @Override
    public Optional<LogLevel> getLevel(String name) {
        if (levelMap.isEmpty()) {
            getLoggers();
        }
        if (!levelMap.isEmpty()) {
            for (Map.Entry<String, LogLevel> entry : levelMap.entrySet()) {
                var key = entry.getKey();
                var level = entry.getValue();
                if (key.equals(name)) {
                    return Optional.of(level);
                }
            }
            LogLevel result = null;
            for (Map.Entry<String, LogLevel> entry : levelMap.entrySet()) {
                var key = entry.getKey();
                var level = entry.getValue();
                if (name.startsWith(key + ".")) {
                    result = level;
                    break;
                }
            }
            if (result != null) {
                levelMap.put(name, result);
                return Optional.of(result);
            }
        }
        return Optional.empty();
    }
}
