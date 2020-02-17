package com.truthbean.debbie.util;

import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author TruthBean
 * @since 0.0.2
 */
public class Assert {

    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void notEmpty(Collection collection, String message) {
        if (collection == null || collection.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void notEmpty(Map map, String message) {
        if (map == null || map.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Assert that an array contains elements; that is, it must not be
     * {@code null} and must contain at least one element.
     * <pre class="code">Assert.notEmpty(array, "The array must contain elements");</pre>
     * @param array the array to check
     * @param message the exception message to use if the assertion fails
     * @throws IllegalArgumentException if the object array is {@code null} or contains no elements
     */
    public static void notEmpty(Object[] array, String message) {
        if (array == null || array.length == 0) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Assert that an array contains elements; that is, it must not be
     * {@code null} and must contain at least one element.
     * <pre class="code">
     * Assert.notEmpty(array, () -&gt; "The " + arrayType + " array must contain elements");
     * </pre>
     * @param array the array to check
     * @param messageSupplier a supplier for the exception message to use if the
     * assertion fails
     * @throws IllegalArgumentException if the object array is {@code null} or contains no elements
     */
    public static void notEmpty(Object[] array, Supplier<String> messageSupplier) {
        if (array == null || array.length == 0) {
            throw new IllegalArgumentException(nullSafeGet(messageSupplier));
        }
    }

    /**
     * Assert that an array contains elements; that is, it must not be
     * {@code null} and must contain at least one element.
     * @param array object array
     * @deprecated as of 4.3.7, in favor of {@link #notEmpty(Object[], String)}
     */
    @Deprecated
    public static void notEmpty(Object[] array) {
        notEmpty(array, "[Assertion failed] - this array must not be empty: it must contain at least 1 element");
    }

    public static void hasText(String text, String message) {
        if (StringUtils.isBlank(text)) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }

    private static String nullSafeGet(Supplier<String> messageSupplier) {
        return (messageSupplier != null ? messageSupplier.get() : null);
    }
}
