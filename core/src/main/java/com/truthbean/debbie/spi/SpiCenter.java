/**
 * Copyright (c) 2024 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.spi;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.0
 * Created on 2021-05-27 18:00
 */
public class SpiCenter {
    private static final Map<Class<?>, Set<Object>> SPI_CACHE = new ConcurrentHashMap<>();

    public static <T, O extends T> void register(Class<T> service, O provider) {
        if (SPI_CACHE.containsKey(service)) {
            Set<Object> providers = SPI_CACHE.get(service);
            providers.add(provider);
        } else {
            Set<Object> providers = new HashSet<>();
            providers.add(provider);
            SPI_CACHE.put(service, providers);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getProvider(Class<T> service) {
        if (SPI_CACHE.containsKey(service)) {
            Set<Object> providers = SPI_CACHE.get(service);
            return (T) providers.iterator().next();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T> Set<T> getProviders(Class<T> service) {
        if (SPI_CACHE.containsKey(service)) {
            Set<Object> providers = SPI_CACHE.get(service);
            return (Set<T>) providers;
        }
        return null;
    }

    public static <T, O extends T> boolean unregister(Class<T> service, O provider) {
        if (SPI_CACHE.containsKey(service)) {
            Set<Object> providers = SPI_CACHE.get(service);
            return providers.remove(provider);
        }
        return false;
    }

    public static <T> void unregister(Class<T> service) {
        SPI_CACHE.remove(service);
    }
}
