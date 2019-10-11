package com.truthbean.debbie.data.transformer;

import com.truthbean.debbie.reflection.TypeHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class DataTransformerFactory {

    private static final Map<DataTransformer<?,?>, Type[]> cache = new LinkedHashMap<>();

    public static void register(DataTransformer<?,?> transformer, Type[] types) {
        cache.put(transformer, types);
    }

    public static <O, T> T transform(final O origin, final Class<T> target) {
        if (origin == null) return null;
        final Class<?> originType = origin.getClass();
        T r = transformWithCache(origin, originType, target);
        if (r != null) return r;

        var error = originType.getTypeName() + " with " + target.getName() + " has no transformer";
        LOGGER.warn("", new NoDataTransformerMatchedException(error));
        return target.cast(origin);
    }

    public static <O, T> T transform(final O origin, final Class<T> target, Map<DataTransformer<?,?>, Type[]> dataTransformerMap) {
        if (origin == null) return null;

        final Class<?> originType = origin.getClass();
        T r = transformWithCache(origin, originType, target);
        if (r != null) return r;

        for (Map.Entry<DataTransformer<?,?>, Type[]> entry : dataTransformerMap.entrySet()) {
            DataTransformer<?,?> transformer = entry.getKey();
            Type[] argsType = entry.getValue();
            LOGGER.debug(transformer.getClass().getName());
            cache.put(transformer, argsType);
            T result = cast(argsType, transformer, originType, target, origin);
            if (result != null) {
                return result;
            }
        }
        var error = originType.getTypeName() + " with " + target.getName() + " has no transformer";
        LOGGER.warn("", new NoDataTransformerMatchedException(error));
        return target.cast(origin);
    }

    private static <O, T> T transformWithCache(final O origin, final Class<?> originType, final Class<T> target) {
        if (originType == target) {
            return target.cast(origin);
        }

        for (Map.Entry<DataTransformer<?,?>, Type[]> entry : cache.entrySet()) {
            DataTransformer<?,?> key = entry.getKey();
            Type[] value = entry.getValue();
            T result = cast(value, key, originType, target, origin);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static <O, T> T cast(Type[] argsType, DataTransformer transformer, final Class<?> originType,
                         final Class<T> target, final O origin) {
        if (argsType != null && argsType.length == 2) {
            Class<?> newOriginType = originType;
            boolean isOriginWrapped = false;
            if (TypeHelper.isRawBaseType(originType)) {
                newOriginType = TypeHelper.getWrapperClass(originType);
                isOriginWrapped = true;
            }
            boolean isTargetWrapped = false;
            Class<?> newTarget = target;
            if (TypeHelper.isRawBaseType(target)) {
                newTarget = TypeHelper.getWrapperClass(target);
                isTargetWrapped = true;
            }
            if (argsType[0] == newOriginType && argsType[1] == newTarget) {
                try {
                    if (isOriginWrapped) {
                        return (T) TypeHelper.unwarp(transformer.transform(origin));
                    }
                    return target.cast(transformer.transform(origin));
                } catch (Exception e) {
                    LOGGER.warn(e.getMessage());
                }
            }
            if (argsType[1] == newOriginType && argsType[0] == newTarget) {
                try {
                    if (isTargetWrapped) {
                        return (T) TypeHelper.unwarp(transformer.reverse(origin));
                    }
                    return target.cast(transformer.reverse(origin));
                } catch (Exception e) {
                    LOGGER.warn(e.getMessage());
                }
            }
        }
        return null;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DataTransformerFactory.class);
}
