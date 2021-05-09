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
import com.truthbean.debbie.net.uri.AntPathMatcher;
import com.truthbean.LoggerFactory;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.0.2
 * Created on 2020-04-14 22:42
 */
public class ResourceResolver {
    private final Object value = new Object();
    private final Map<String, Object> cache = new ConcurrentHashMap<>();
    private final Map<Class<?>, Object> classCache = new ConcurrentHashMap<>();
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    public void addResource(Collection<String> resources) {
        for (String resource : resources) {
            this.cache.put(resource, value);
            try {
                this.classCache.put(Class.forName(resource), value);
            } catch (Throwable ignored) {
            }
        }
    }

    public Set<String> getMatchedResources(String pattern) {
        Set<String> result = new HashSet<>();
        Set<String> resources  = cache.keySet();
        for (String resource : resources) {
            logger.trace("resource: " + resource);
            if (antPathMatcher.match(pattern, resource)) {
                result.add(resource);
            }
        }
        return result;
    }

    public Set<Class<?>> getCachedClasses() {
        return this.classCache.keySet();
    }

    public void cleanResources() {
        cache.clear();
        this.classCache.clear();
    }

    private static final Logger logger = LoggerFactory.getLogger(ResourceResolver.class);
}