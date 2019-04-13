package com.truthbean.code.debbie.core.util;

import java.util.List;
import java.util.Map;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public final class CollectionUtils {

    private CollectionUtils() {
    }

    public static <T> String splitList(List<T> list) {
        return splitList(list, ",");
    }

    public static <T> String splitList(List<T> list, String split) {
        StringBuilder builder = new StringBuilder();
        if (list != null && !list.isEmpty()) {
            int size = list.size();
            for (int i = 0; i < size - 1; i++) {
                if (list.get(i) != null) {
                    builder.append(list.get(i).toString()).append(split);
                }
            }
            if (list.get(size - 1) != null) {
                builder.append(list.get(size - 1).toString());
            }
        }
        return builder.toString();
    }

    public static <T> String splitArray(String[] array, String split) {
        StringBuilder builder = new StringBuilder();
        splitArray(array, split, builder);
        return builder.toString();
    }

    public static <T> void splitArray(String[] array, String split, StringBuilder builder) {
        if (array != null && array.length > 0) {
            int size = array.length;
            for (int i = 0; i < size - 1; i++) {
                if (array[i] != null) {
                    builder.append(array[i]).append(split);
                }
            }
            if (array[size - 1] != null) {
                builder.append(array[size - 1]);
            }
        }
    }

    public static <K, V> void reverse(Map<K, V> source, Map<V, K> target) {
        for (var key : source.keySet()) {
            target.put(source.get(key), key);
        }
    }
}