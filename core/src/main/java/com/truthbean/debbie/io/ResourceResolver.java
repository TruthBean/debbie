package com.truthbean.debbie.io;

import com.truthbean.debbie.net.uri.AntPathMatcher;

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
    private final Object VALUE = new Object();
    private final Map<String, Object> cache = new ConcurrentHashMap<>();
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    public void addResource(Collection<String> resources) {
        for (String resource : resources) {
            this.cache.put(resource, VALUE);
        }
    }

    public Set<String> getMatchedResources(String pattern) {
        Set<String> result = new HashSet<>();
        Set<String> resources  = cache.keySet();
        for (String resource : resources) {
            if (antPathMatcher.match(pattern, resource)) {
                result.add(resource);
            }
        }
        return result;
    }

    public void cleanResources() {
        cache.clear();
    }
}