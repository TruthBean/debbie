package com.truthbean.debbie.util;

import java.util.Map;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class MapUtils {
    private MapUtils() {
    }

    public static <K, V> void reverse(Map<K, V> source, Map<V, K> target) {
        for (var key : source.keySet()) {
            target.put(source.get(key), key);
        }
    }
}
