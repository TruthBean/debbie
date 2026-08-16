/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.data;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.spi.SpiLoader;

/**
 * Loads {@link JsonHelper} and {@link XmlHelper} implementations via SPI.
 * <p>
 * Modules providing serialization support (e.g. {@code debbie-jackson},
 * {@code debbie-json}) register their implementations through {@code META-INF/services}
 * so that consumers never depend on a concrete serialization library at compile time.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public final class DataHelperFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataHelperFactory.class);

    private static volatile JsonHelper jsonHelper;
    private static volatile XmlHelper xmlHelper;

    private DataHelperFactory() {
    }

    /**
     * Returns the {@link JsonHelper} loaded via SPI, or {@code null} if no
     * provider is registered on the classpath/module path.
     *
     * @return the json helper, or {@code null} if none is available
     */
    public static JsonHelper getJsonHelper() {
        if (jsonHelper == null) {
            synchronized (DataHelperFactory.class) {
                if (jsonHelper == null) {
                    jsonHelper = SpiLoader.loadProvider(JsonHelper.class);
                    if (jsonHelper == null) {
                        LOGGER.warn("no JsonHelper SPI implementation found; json serialization will fall back to toString()");
                    }
                }
            }
        }
        return jsonHelper;
    }

    /**
     * Returns the {@link XmlHelper} loaded via SPI, or {@code null} if no
     * provider is registered on the classpath/module path.
     *
     * @return the xml helper, or {@code null} if none is available
     */
    public static XmlHelper getXmlHelper() {
        if (xmlHelper == null) {
            synchronized (DataHelperFactory.class) {
                if (xmlHelper == null) {
                    xmlHelper = SpiLoader.loadProvider(XmlHelper.class);
                    if (xmlHelper == null) {
                        LOGGER.warn("no XmlHelper SPI implementation found; xml serialization will fall back to toString()");
                    }
                }
            }
        }
        return xmlHelper;
    }
}