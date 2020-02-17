package com.truthbean.debbie.util;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author TruthBean
 * @since 0.0.2
 */
public class CollectionUtils {

    public static final List EMPTY_LIST = Collections.EMPTY_LIST;

    public static boolean isEmpty(Collection<?> collection) {
        return (collection == null || collection.isEmpty());
    }

    @SuppressWarnings("unchecked")
    public static final <T> List<T> emptyList() {
        return (List<T>) EMPTY_LIST;
    }
}
