package com.truthbean.debbie.data.transformer;

import com.truthbean.debbie.data.transformer.text.DefaultTextTransformer;
import com.truthbean.debbie.reflection.ClassInfo;
import com.truthbean.debbie.reflection.ReflectionHelper;
import com.truthbean.debbie.reflection.TypeHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class DataTransformerFactory {

    private static final Map<DataTransformer<?,?>, Type[]> cache = new LinkedHashMap<>();

    public static void register(Collection<Class<?>> classes) {
        if (classes != null && !classes.isEmpty()) {
            for (Class<?> clazz : classes) {
                registerDataTransformer(clazz);
            }
        }
    }

    public static  <DT> void registerDataTransformer(Class<DT> transformerClass) {
        var support = transformerClass != null && !transformerClass.isInterface()
                && !transformerClass.isAnnotation() && !transformerClass.isEnum()
                && !Modifier.isAbstract(transformerClass.getModifiers())
                && DataTransformer.class.isAssignableFrom(transformerClass)
                && transformerClass.getAnnotation(Transformer.class) != null;
        if (support) {
            ClassInfo<DT> classInfo = new ClassInfo<>(transformerClass);
            DT transformer = ReflectionHelper.newInstance(transformerClass);
            Type[] argsType = ReflectionHelper.getActualTypes(transformerClass);
            DataTransformerFactory.register((DataTransformer<?, ?>) transformer, argsType);
        }
    }

    public static void register(DataTransformer<?,?> transformer, Type[] types) {
        cache.put(transformer, types);
    }

    @SuppressWarnings("unchecked")
    public static <O, T> T transform(final O origin, final Class<T> target) {
        if (origin == null) return null;
        final Class<?> originType = origin.getClass();

        if (originType == target) {
            return target.cast(origin);
        }
        if (TypeHelper.isSameType(originType, target) && TypeHelper.isRawBaseType(target)) {
            return (T) origin;
        }

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
        Map<DataTransformer<?,?>, Type[]> cache = DataTransformerFactory.cache;
        for (Map.Entry<DataTransformer<?,?>, Type[]> entry : cache.entrySet()) {
            DataTransformer<?,?> transformer = entry.getKey();
            Type[] value = entry.getValue();
            T result = cast(value, transformer, originType, target, origin);
            if (result != null) {
                LOGGER.trace("transformer : " + transformer);
                return result;
            }
        }
        if (target == String.class) {
            return (T) new DefaultTextTransformer().transform(origin);
        }
        return transformUseClassInstanceTransformer(origin, originType, target);
    }

    private static <O, T> T transformUseClassInstanceTransformer(final O origin, final Class<?> originType, final Class<T> target) {
        Type[] types = new Type[2];
        types[0] = Object.class;
        types[1] = String.class;
        return cast(types, new ClassInstanceTransformer(), originType, target, origin);
    }

    /*@SuppressWarnings("unchecked")
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
    }*/

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

            boolean canTransform = false;
            boolean type0IsClass = argsType[0] instanceof Class;
            boolean type1IsClass = argsType[1] instanceof Class;

            boolean type0IsParameterizedType = argsType[0] instanceof ParameterizedType;
            boolean type1IsParameterizedType = argsType[1] instanceof ParameterizedType;

            if (type0IsClass && type1IsParameterizedType) {
                Class<?> type0 = (Class<?>) argsType[0];
                ParameterizedType parameterizedType1 = (ParameterizedType) argsType[1];
                Class<?> rawType = (Class<?>) parameterizedType1.getRawType();
                canTransform = type0.isAssignableFrom(newOriginType) && rawType.isAssignableFrom(newTarget);
            } else if (type0IsParameterizedType && type1IsClass) {
                Class<?> type1 = (Class<?>) argsType[1];
                ParameterizedType parameterizedType0 = (ParameterizedType) argsType[0];
                Class<?> rawType = (Class<?>) parameterizedType0.getRawType();
                canTransform = type1.isAssignableFrom(newTarget) && rawType.isAssignableFrom(newOriginType);
            } else if (type0IsClass && type1IsClass) {
                Class<?> type0 = (Class<?>) argsType[0];
                Class<?> type1 = (Class<?>) argsType[1];
                canTransform = type0.isAssignableFrom(newOriginType) && type1.isAssignableFrom(newTarget);
            } else if (type1IsParameterizedType && type0IsParameterizedType){
                ParameterizedType parameterizedType1 = (ParameterizedType) argsType[1];
                Class<?> rawType1 = (Class<?>) parameterizedType1.getRawType();

                ParameterizedType parameterizedType0 = (ParameterizedType) argsType[0];
                Class<?> rawType0 = (Class<?>) parameterizedType0.getRawType();

                canTransform = rawType1.isAssignableFrom(newTarget) && rawType0.isAssignableFrom(newOriginType);
            }

            if (canTransform) {
                try {
                    Object transform = transformer.transform(origin);
                    if (isOriginWrapped) {
                        return (T) TypeHelper.unwarp(transform);
                    }
                    if (isTargetWrapped) {
                        return (T) transform;
                    }
                    return target.cast(transform);
                } catch (Exception e) {
                    LOGGER.warn(e.getMessage());
                }
            }

            boolean canReverse = false;
            if (type0IsClass && type1IsParameterizedType) {
                Class<?> type0 = (Class<?>) argsType[0];
                ParameterizedType parameterizedType1 = (ParameterizedType) argsType[1];
                Class<?> rawType = (Class<?>) parameterizedType1.getRawType();
                canReverse = type0.isAssignableFrom(newTarget) && rawType.isAssignableFrom(newOriginType);
            } else if (type0IsParameterizedType && type1IsClass) {
                Class<?> type1 = (Class<?>) argsType[1];
                ParameterizedType parameterizedType0 = (ParameterizedType) argsType[0];
                Class<?> rawType = (Class<?>) parameterizedType0.getRawType();
                canReverse = type1.isAssignableFrom(newOriginType) && rawType.isAssignableFrom(newTarget);
            } else if (type0IsClass && type1IsClass) {
                Class<?> type0 = (Class<?>) argsType[0];
                Class<?> type1 = (Class<?>) argsType[1];
                canReverse = type0.isAssignableFrom(newTarget) && type1.isAssignableFrom(newOriginType);
            } else if (type1IsParameterizedType && type0IsParameterizedType){
                ParameterizedType parameterizedType1 = (ParameterizedType) argsType[1];
                Class<?> rawType1 = (Class<?>) parameterizedType1.getRawType();

                ParameterizedType parameterizedType0 = (ParameterizedType) argsType[0];
                Class<?> rawType0 = (Class<?>) parameterizedType0.getRawType();

                canReverse = rawType1.isAssignableFrom(newOriginType) && rawType0.isAssignableFrom(newTarget);
            }

            if (canReverse) {
                try {
                    Object reverse = transformer.reverse(origin);
                    if (isTargetWrapped) {
                        return (T) TypeHelper.unwarp(reverse);
                    }
                    if (isOriginWrapped) {
                        return (T) reverse;
                    }
                    return target.cast(reverse);
                } catch (Exception e) {
                    LOGGER.warn(e.getMessage());
                }
            }
        }
        return null;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DataTransformerFactory.class);
}
