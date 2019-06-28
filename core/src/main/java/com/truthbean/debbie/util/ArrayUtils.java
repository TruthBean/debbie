package com.truthbean.debbie.util;

import java.lang.reflect.Array;
import java.util.Arrays;

public class ArrayUtils {
    /**
     * 将多个数组合并在一起<br>
     * 忽略null的数组
     *
     * @param <T>    数组元素类型
     * @param arrays 数组集合
     * @return 合并后的数组
     */
    @SafeVarargs
    public static <T> T[] addAll(T[]... arrays) {
        if (arrays.length == 1) {
            return arrays[0];
        }

        int length = 0;
        for (T[] array : arrays) {
            if (array == null) {
                continue;
            }
            length += array.length;
        }
        T[] result = newArray(arrays.getClass().getComponentType().getComponentType(), length);

        length = 0;
        for (T[] array : arrays) {
            if (array == null) {
                continue;
            }
            System.arraycopy(array, 0, result, length, array.length);
            length += array.length;
        }
        return result;
    }

    /**
     * 新建一个空数组
     *
     * @param <T>           数组元素类型
     * @param componentType 元素类型
     * @param newSize       大小
     * @return 空数组
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] newArray(Class<?> componentType, int newSize) {
        return (T[]) Array.newInstance(componentType, newSize);
    }
}
