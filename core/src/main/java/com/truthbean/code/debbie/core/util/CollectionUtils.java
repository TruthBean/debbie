package com.truthbean.code.debbie.core.util;

import java.util.Iterator;
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
        StringBuilder builder = new StringBuilder();
        if (list != null && !list.isEmpty()) {
            int size = list.size();
            for (int i = 0; i < size - 1; i++) {
                if (list.get(i) != null) {
                    builder.append(list.get(i).toString()).append(",");
                }
            }
            if (list.get(size - 1) != null) {
                builder.append(list.get(size - 1).toString());
            }
        }
        return builder.toString();
    }

    public static void reverse(Map source, Map target) {
        for (Object key : source.keySet()) {
            target.put(source.get(key), key);
        }
    }
}