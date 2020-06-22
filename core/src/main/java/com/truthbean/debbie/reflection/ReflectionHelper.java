/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.reflection;

import com.truthbean.debbie.data.transformer.DataTransformerFactory;
import com.truthbean.debbie.io.ResourceResolver;
import com.truthbean.debbie.io.ResourcesHandler;
import com.truthbean.debbie.io.StreamHelper;
import com.truthbean.debbie.util.Constants;
import com.truthbean.debbie.util.StringUtils;
import com.truthbean.Logger;
import com.truthbean.logger.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/23 11:23.
 */
public class ReflectionHelper {

    public static <T> Constructor<T> getConstructorIfAvailable(Class<T> clazz, Class<?>... paramTypes) {
        try {
            return clazz.getConstructor(paramTypes);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    public static <T> T newInstance(String className) {
        ClassLoader classLoader = ClassLoaderUtils.getDefaultClassLoader();
        return newInstance(className, classLoader);
    }

    @SuppressWarnings("unchecked")
    public static <T> T newInstance(String className, ClassLoader classLoader) {
        try {
            return (T) newInstance(classLoader.loadClass(className));
        } catch (ClassNotFoundException e) {
            LOGGER.error("", e);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T> T newInstance(Class<T> type) {
        if (Modifier.isAbstract(type.getModifiers()) && !type.isInterface()) {
            throw new IllegalStateException(type.getName() + " cannot be abstract class");
        }
        if (type.isInterface()) {
            if (type == List.class) {
                return (T) new ArrayList();
            }
            if (type == Set.class) {
                return (T) new HashSet();
            }
            if (type == Map.class) {
                return (T) new HashMap();
            }
            if (type == Queue.class) {
                return (T) new LinkedBlockingQueue();
            }
            if (type == Deque.class) {
                return (T) new ArrayDeque();
            }
            throw new IllegalStateException(type.getName() + " cannot be isInterface except List, Set, Map, Queue, Deque. ");
        }
        if (TypeHelper.isBaseType(type)) {
            throw new IllegalStateException(type.getName() + " cannot be base type. ");
        }
        return newInstance(type, Constants.EMPTY_CLASS_ARRAY, null);
    }

    public static Object[] makeDefaultValue(Class<?>[] parameterTypes) {
        final Object[] result = new Object[parameterTypes.length];
        if (parameterTypes.length > 0 && parameterTypes[0] != null) {
            int n = parameterTypes.length;
            for (int i = 0; i < n; i++) {
                Class<?> parameterType = parameterTypes[i];
                if (parameterType == byte.class) {
                    result[i] = (byte) 0;
                } else if (parameterType == char.class) {
                    result[i] = (char) 0;
                } else if (parameterType == short.class) {
                    result[i] = (short) 0;
                } else if (parameterType == int.class) {
                    result[i] = 0;
                } else if (parameterType == long.class) {
                    result[i] = 0L;
                } else if (parameterType == float.class) {
                    result[i] = 0.0F;
                } else if (parameterType == double.class) {
                    result[i] = 0.0D;
                } else if (parameterType == boolean.class) {
                    result[i] = false;
                } else {
                    result[i] = null;
                }
            }
        }
        return result;
    }

    public static Type[] getActualTypes(@SuppressWarnings("rawtypes") Class clazz, int typeNum) {
        if (clazz == Object.class || clazz == Void.class) {
            return null;
        }
        Type[] types = clazz.getTypeParameters();
        if (types != null && types.length >= typeNum) {
            return types;
        }
        Type genType = clazz.getGenericSuperclass();
        if (genType instanceof ParameterizedType) {
            Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
            if (params.length >= typeNum) {
                return params;
            }
        }

        Type[] interfaces = clazz.getGenericInterfaces();
        if (interfaces.length > 0) {
            for (var type : interfaces) {
                if (type instanceof ParameterizedType) {
                    Type[] params = ((ParameterizedType) type).getActualTypeArguments();
                    if (params != null && params.length >= typeNum) {
                        return params;
                    }
                }
            }
        } else {
            return getActualTypes(clazz.getSuperclass(), typeNum);
        }

        return null;
    }

    /**
     *
     * @param method 要获取方法参数类型的方法
     * @param declaringClass 方法所在的（子）类
     * @return 方法参数类型
     */
    public static Type[] getMethodActualTypes(Method method, Class<?> declaringClass) {
        // 类的泛型
        Map<String, Type> typeParameters = getActualTypeMap(declaringClass);

        Parameter[] parameters = method.getParameters();
        int parameterCount = parameters.length;
        if (parameterCount == 0) return new Class<?>[0];

        Type[] genericParameterTypes = method.getGenericParameterTypes();

        Type[] parameterTypes = new Type[parameterCount];

        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            parameterTypes[i] = parameter.getParameterizedType();
        }

        if (typeParameters != null && typeParameters.size() > 0) {

            for (int i = 0; i < parameters.length; i++) {
                Parameter parameter = parameters[i];
                // 方法参数的泛型
                String typeName = parameter.getType().getTypeName();
                LOGGER.trace(() -> "parameter.getType().getTypeName() = " + typeName);

                String typeName1 = parameter.getParameterizedType().getTypeName();
                LOGGER.trace(() -> "parameter.getParameterizedType().getTypeName() = " + typeName);

                String genericParameterTypeName = genericParameterTypes[i].getTypeName();
                LOGGER.trace(() -> "genericParameterTypes: " + genericParameterTypeName);

                for (Map.Entry<String, Type> nameTypeEntry : typeParameters.entrySet()) {
                    var name = nameTypeEntry.getKey();
                    var type = nameTypeEntry.getValue();

                    if (genericParameterTypeName.equals(name)) {
                        parameterTypes[i] = type;
                    }
                }
            }

        }

        // 判断泛型的名称相同来替换原型

        return parameterTypes;
    }

    /**
     * 需要注意 ParameterizedType、GenericArrayType、TypeVariable 这几个的区别
     * @see "https://www.cnblogs.com/one777/p/7833789.html"
     *
     * @param clazz target class
     * @return actual types
     */
    public static Type[] getActualTypes(Class<?> clazz) {
        if (clazz == null || clazz == Object.class || clazz == Void.class) {
            return null;
        }
        Type[] types = clazz.getTypeParameters();
        if (types != null && types.length > 0) {
            return types;
        }
        Type genType = clazz.getGenericSuperclass();

        if (genType == Object.class) {
            Type[] interfaces = clazz.getGenericInterfaces();
            for (var type : interfaces) {
                if (type instanceof ParameterizedType) {
                    Type[] params = ((ParameterizedType) type).getActualTypeArguments();
                    if (params != null && params.length > 0) {
                        return params;
                    }
                }
            }
        }
        if (!(genType instanceof ParameterizedType)) {
            return getActualTypes(clazz.getSuperclass());
        }
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        if (params == null || params.length == 0) {
            return null;
        }
        return params;
    }

    public static Map<String, Type> getActualTypeMap(Class<?> clazz) {
        if (clazz == null || clazz == Object.class || clazz == Void.class) {
            return null;
        }
        Type[] types = clazz.getTypeParameters();
        if (types != null && types.length > 0) {
            return Collections.emptyMap();
        }

        Map<String, Type> nameAndType = new HashMap<>();

        Type genType = clazz.getGenericSuperclass();
        if (genType == Object.class) {
            Type[] interfaces = clazz.getGenericInterfaces();
            Class<?>[] interfaceClasses = clazz.getInterfaces();
            for (int i = 0; i < interfaces.length; i++) {
                var type = interfaces[i];
                var clz = interfaceClasses[i];
                if (type instanceof ParameterizedType) {
                    Type[] params = ((ParameterizedType) type).getActualTypeArguments();
                    TypeVariable<? extends Class<?>>[] typeParameters = clz.getTypeParameters();

                    for (int i1 = 0; i1 < params.length; i1++) {
                        nameAndType.put(typeParameters[i1].getName(), params[i1]);
                    }

                }
            }
        }
        if (!(genType instanceof ParameterizedType)) {
            return getActualTypeMap(clazz.getSuperclass());
        }
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        if (params == null || params.length == 0) {
            return Collections.emptyMap();
        }

        TypeVariable<? extends Class<?>>[] typeParameters = clazz.getSuperclass().getTypeParameters();

        for (int i = 0; i < params.length; i++) {
            nameAndType.put(typeParameters[i].getName(), params[i]);
        }
        return nameAndType;
    }

    public static <T> T newInstance(Class<T> type,
                                    @SuppressWarnings("rawtypes") Class[] parameterTypes, Object[] args) {
        var constructor = getConstructor(type, parameterTypes);
        if (constructor != null) {
            return newInstance(constructor, args);
        }
        return null;
    }

    public static <T> T newInstance(final Constructor<T> constructor, final Object[] args) {

        boolean cannotAccess = ((!Modifier.isPublic(constructor.getModifiers())
                || !Modifier.isPublic(constructor.getDeclaringClass().getModifiers()))
                && !constructor.trySetAccessible());
        try {
            if (cannotAccess) {
                constructor.setAccessible(true);
            }
            return constructor.newInstance(args);
        } catch (InstantiationException e) {
            Throwable cause = e.getCause();
            LOGGER.trace("Is it an abstract class?");
            LOGGER.error("new instance error. \n", Objects.requireNonNullElse(cause, e));
        } catch (IllegalAccessException e) {
            Throwable cause = e.getCause();
            LOGGER.trace("Is the constructor accessible?");
            LOGGER.error("new instance error. \n", Objects.requireNonNullElse(cause, e));
        } catch (IllegalArgumentException e) {
            Throwable cause = e.getCause();
            LOGGER.trace("Illegal argument for constructor");
            LOGGER.error("new instance error. \n", Objects.requireNonNullElse(cause, e));
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            LOGGER.error("new instance error. \n", Objects.requireNonNullElse(cause, e));
        } finally {
            if (!cannotAccess) {
                constructor.setAccessible(false);
            }
        }
        return null;
    }

    public static Object invokeStaticMethod(String methodName, Class<?> targetClass) {
        try {
            Method declaredMethod = targetClass.getDeclaredMethod(methodName);
            boolean cannotAccess = ((!Modifier.isPublic(declaredMethod.getModifiers())
                    || !Modifier.isPublic(declaredMethod.getDeclaringClass().getModifiers()))
                    && !declaredMethod.trySetAccessible());
            if (cannotAccess) {
                declaredMethod.setAccessible(true);
            }
            return declaredMethod.invoke(null);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            Throwable cause = e.getCause();
            LOGGER.error("invoke static method(" + methodName + " error ). \n", Objects.requireNonNullElse(cause, e));
        }
        return null;
    }

    public static Object invokeStaticMethod(Method declaredMethod) {
        try {
            boolean cannotAccess = ((!Modifier.isPublic(declaredMethod.getModifiers())
                    || !Modifier.isPublic(declaredMethod.getDeclaringClass().getModifiers())) &&
                    !declaredMethod.trySetAccessible());
            if (cannotAccess) {
                declaredMethod.setAccessible(true);
            }
            return declaredMethod.invoke(null);
        } catch (IllegalAccessException | InvocationTargetException e) {
            Throwable cause = e.getCause();
            LOGGER.error("invoke static method(" + declaredMethod + " error ).\n", Objects.requireNonNullElse(cause, e));
        }
        return null;
    }

    public static <T> Constructor<T> getConstructor(Class<T> type, @SuppressWarnings("rawtypes") Class[] parameterTypes) {
        try {
            Constructor<T> constructor = type.getDeclaredConstructor(parameterTypes);
            boolean cannotAccess = ((!Modifier.isPublic(constructor.getModifiers())
                    || !Modifier.isPublic(constructor.getDeclaringClass().getModifiers()))
                    && !constructor.trySetAccessible());
            if (cannotAccess) {
                constructor.setAccessible(true);
            }
            return constructor;
        } catch (NoSuchMethodException e) {
            Throwable cause = e.getCause();
            LOGGER.error("no such constructor. \n", Objects.requireNonNullElse(cause, e));
        }
        return null;
    }

    public static Set<Annotation> getClassAnnotations(Class<?> clazz) {
        Set<Annotation> annotations = new HashSet<>();
        for (var superClass = clazz; superClass != null && superClass != Object.class;
             superClass = superClass.getSuperclass()) {
            Annotation[] classAnnotations = superClass.getAnnotations();
            if (classAnnotations.length > 0) {
                annotations.addAll(Arrays.asList(classAnnotations));
            }
            getInterfaceAnnotations(annotations, clazz);
        }
        return annotations;
    }

    private static void getInterfaceAnnotations(Set<Annotation> result, Class<?> clazz) {
        Class<?>[] interfaces = clazz.getInterfaces();
        if (interfaces != null) {
            for (Class<?> anInterface : interfaces) {
                Annotation[] annotations = anInterface.getAnnotations();
                if (annotations != null && annotations.length > 0) {
                    Collections.addAll(result, annotations);
                }
                getInterfaceAnnotations(result, anInterface);
            }
        }
    }

    public static List<Field> getDeclaredFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        for (var superClass = clazz; superClass != null && superClass != Object.class;
             superClass = superClass.getSuperclass()) {
            Field[] declaredFields = superClass.getDeclaredFields();
            if (declaredFields.length > 0) {
                fields.addAll(Arrays.asList(declaredFields));
            }
        }
        return fields;
    }

    public static Set<Method> getDeclaredMethods(Class<?> clazz) {
        Set<Method> methods = new HashSet<>();
        for (var superClass = clazz; superClass != null && superClass != Object.class;
             superClass = superClass.getSuperclass()) {
            Method[] declaredMethods = superClass.getDeclaredMethods();
            if (declaredMethods.length > 0) {
                methods.addAll(Arrays.asList(declaredMethods));
            }
        }
        Set<Class<?>> interfaces = getInterfaces(clazz);
        if (!interfaces.isEmpty()) {
            for (Class<?> anInterface : interfaces) {
                Method[] declaredMethods = anInterface.getDeclaredMethods();
                if (declaredMethods.length > 0) {
                    for (Method declaredMethod : declaredMethods) {
                        if (declaredMethod.isDefault()) {
                            methods.add(declaredMethod);
                        }
                    }
                }
            }
        }
        return methods;
    }

    public static Set<Method> getDeclaredMethods(Class<?> clazz, String methodName) {
        Set<Method> result = new HashSet<>();
        Set<Method> methods = getDeclaredMethods(clazz);
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                result.add(method);
            }
        }
        return result;
    }

    public static Set<Method> getInterfaceDefaultMethods(Class<?> clazz) {
        Set<Method> methods = new HashSet<>();
        if (clazz == null)
            return methods;

        if (clazz.isInterface()) {
            Method[] declaredMethods = clazz.getDeclaredMethods();
            if (declaredMethods.length > 0) {
                for (Method declaredMethod : declaredMethods) {
                    if (declaredMethod.isDefault()) {
                        methods.addAll(Arrays.asList(declaredMethods));
                    }
                }
            }
        }

        Class<?>[] interfaces = clazz.getInterfaces();
        for (Class<?> anInterface : interfaces) {
            getInterfaceDefaultMethods(anInterface, methods);
        }
        return methods;
    }

    public static void getInterfaceDefaultMethods(Class<?> clazz, Set<Method> result) {
        Method[] declaredMethods = clazz.getDeclaredMethods();
        if (declaredMethods.length > 0) {
            for (Method declaredMethod : declaredMethods) {
                if (declaredMethod.isDefault()) {
                    result.addAll(Arrays.asList(declaredMethods));
                }
            }
        }
    }

    public static Method getDeclaredMethod(Class<?> clazz, String methodName, Class<?>[] parameterTypes) {
        for (var superClass = clazz; superClass != null && superClass != Object.class;
             superClass = superClass.getSuperclass()) {
            Method[] declaredMethods = superClass.getDeclaredMethods();
            if (declaredMethods.length > 0) {
                for (Method declaredMethod : declaredMethods) {
                    Class<?>[] parameterClass = declaredMethod.getParameterTypes();
                    if (methodName.equals(declaredMethod.getName()) && Arrays.equals(parameterClass, parameterTypes)) {
                        return declaredMethod;
                    }
                }
            }
        }
        Set<Method> interfaceDefaultMethods = getInterfaceDefaultMethods(clazz);
        if (interfaceDefaultMethods.size() > 0) {
            for (Method declaredMethod : interfaceDefaultMethods) {
                Class<?>[] parameterClass = declaredMethod.getParameterTypes();
                if (methodName.equals(declaredMethod.getName()) && Arrays.equals(parameterClass, parameterTypes)) {
                    return declaredMethod;
                }
            }
        }
        return null;
    }

    public static Method getMethod(Class<?> clazz, String methodName, Class<?>[] parameterTypes) {
        for (var superClass = clazz; superClass != null; superClass = superClass.getSuperclass()) {
            Method[] declaredMethods = superClass.getDeclaredMethods();
            if (declaredMethods.length > 0) {
                for (Method declaredMethod : declaredMethods) {
                    Class<?>[] parameterClass = declaredMethod.getParameterTypes();
                    if (methodName.equals(declaredMethod.getName()) && Arrays.equals(parameterClass, parameterTypes)) {
                        return declaredMethod;
                    }
                }
            }
        }
        Set<Method> interfaceDefaultMethods = getInterfaceDefaultMethods(clazz);
        if (interfaceDefaultMethods.size() > 0) {
            for (Method declaredMethod : interfaceDefaultMethods) {
                Class<?>[] parameterClass = declaredMethod.getParameterTypes();
                if (methodName.equals(declaredMethod.getName()) && Arrays.equals(parameterClass, parameterTypes)) {
                    return declaredMethod;
                }
            }
        }
        return null;
    }

    public static Object invokeSetMethod(Object target, Field field, Object arg) {
        Class<?> clazz = target.getClass();
        for (var superClass = clazz; superClass != null && superClass != Object.class;
             superClass = superClass.getSuperclass()) {
            try {
                return invokeSetMethod(superClass, target, field.getName(), field.getType(), arg);
            } catch (NoSuchMethodException ignored) {
            }
        }
        return null;
    }

    public static void invokeFieldBySetMethod(Object target, Field field, Object arg) {
        Class<?> clazz = target.getClass();
        boolean invoked = false;
        for (var superClass = clazz; superClass != null && superClass != Object.class;
             superClass = superClass.getSuperclass()) {
            try {
                invokeSetMethod(superClass, target, field.getName(), field.getType(), arg);
                invoked = true;
                break;
            } catch (NoSuchMethodException ignored) {
            }
        }
        if (!invoked) {
            setField(target, field, arg);
        }
    }

    public static Object invokeSetMethod(Class<?> targetClass, Object target, String fieldName,
                                         Class<?> fieldType, Object arg) throws NoSuchMethodException {
        var methodName = "set" + handleFieldName(fieldName);

        try {
            var method = targetClass.getMethod(methodName, fieldType);
            if (TypeHelper.isRawBaseType(fieldType)) {
                fieldType = TypeHelper.getWrapperClass(fieldType);
            }
            return method.invoke(target, DataTransformerFactory.transform(arg, fieldType));
        } catch (NoSuchMethodException e) {
            Throwable cause = e.getCause();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.error(targetClass.getName() + "." + fieldName + " set method not found. \n",
                        Objects.requireNonNullElse(cause, e));
            } else {
                LOGGER.error(targetClass.getName() + "." + fieldName + " set method not found. \n", e.getMessage());
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            Throwable cause = e.getCause();
            LOGGER.error("", Objects.requireNonNullElse(cause, e));
        } catch (IllegalArgumentException e) {
            Throwable cause = e.getCause();
            LOGGER.error("argument type is wrong. \n", Objects.requireNonNullElse(cause, e));
            throw e;
        }
        return null;
    }

    public static void setField(Object target, Field field, Object value) {
        try {
            boolean cannotAccess = ((!Modifier.isPublic(field.getModifiers())
                    || !Modifier.isPublic(field.getDeclaringClass().getModifiers())) && !field.trySetAccessible());
            if (cannotAccess) {
                field.setAccessible(true);
            }
            field.set(target, value);
        } catch (IllegalAccessException e) {
            LOGGER.error("", e);
        }
    }

    public static void setField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getField(fieldName);
            boolean cannotAccess = ((!Modifier.isPublic(field.getModifiers())
                    || !Modifier.isPublic(field.getDeclaringClass().getModifiers()))
                    && !field.trySetAccessible());
            if (cannotAccess) {
                field.setAccessible(true);
            }
            field.set(target, value);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            LOGGER.error("", e);
        }
    }

    public static Object getField(Object target, Field field) {
        try {
            boolean cannotAccess = ((!Modifier.isPublic(field.getModifiers())
                    || !Modifier.isPublic(field.getDeclaringClass().getModifiers()))
                    && !field.trySetAccessible());
            if (cannotAccess) {
                field.setAccessible(true);
            }
            return field.get(target);
        } catch (IllegalAccessException e) {
            LOGGER.error("", e);
        }
        return null;
    }

    public static Object invokeMethod(Object target, String methodName, Object... parameters) {
        Method declaredMethod = null;
        try {
            Class<?> targetClass = target.getClass();
            if (parameters != null && parameters.length > 0) {
                List<Class<?>> parameterTypes = new LinkedList<>();
                for (Object parameter : parameters) {
                    parameterTypes.add(parameter.getClass());
                }
                declaredMethod = getMethod(targetClass, methodName, parameterTypes.toArray(new Class<?>[]{}));
            } else {
                declaredMethod = getMethod(targetClass, methodName, new Class[]{});
            }
            if (declaredMethod != null) {
                boolean cannotAccess = ((!Modifier.isPublic(declaredMethod.getModifiers())
                        || !Modifier.isPublic(declaredMethod.getDeclaringClass().getModifiers()))
                        && !declaredMethod.trySetAccessible());
                if (cannotAccess)
                    declaredMethod.setAccessible(true);
            } else {
                throw new NoSuchMethodException(methodName);
            }
            if (parameters != null && parameters.length > 0)
                return declaredMethod.invoke(target, parameters);
            else
                return declaredMethod.invoke(target);
        } catch (IllegalAccessException | InvocationTargetException | UndeclaredThrowableException | NoSuchMethodException e) {
            Throwable cause = e.getCause();
            if (declaredMethod != null)
                LOGGER.error("invoke method(" + declaredMethod + " error ). \n", Objects.requireNonNullElse(cause, e));
            else
                LOGGER.error("invoke method(" + methodName + " error ). \n", Objects.requireNonNullElse(cause, e));
        }
        return null;
    }

    public static Object invokeMethod(Object target, String methodName, Object[] parameters,
                                      Class<?>[] parameterTypes) {
        try {
            Class<?> targetClass = target.getClass();
            Method declaredMethod;
            if (parameters != null && parameters.length > 0) {
                declaredMethod = targetClass.getDeclaredMethod(methodName, parameterTypes);
            } else {
                declaredMethod = targetClass.getDeclaredMethod(methodName);
            }

            if (declaredMethod != null) {
                boolean cannotAccess = ((!Modifier.isPublic(declaredMethod.getModifiers())
                        || !Modifier.isPublic(declaredMethod.getDeclaringClass().getModifiers()))
                        && !declaredMethod.trySetAccessible());
                if (cannotAccess)
                    declaredMethod.setAccessible(true);
            }

            return declaredMethod.invoke(target, parameters);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException
                | UndeclaredThrowableException e) {
            Throwable cause = e.getCause();
            LOGGER.error("invoke method(" + methodName + " error ). \n", Objects.requireNonNullElse(cause, e));
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T> T invokeMethod(Object target, Method method, Object... parameters) {
        try {
            return invokeMethod(false, target, method, parameters);
        } catch (Throwable ignored) {
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T> T invokeMethod(boolean throwException, Object target, Method method,
                                     Object... parameters) throws Throwable {
        if (method == null)
            return null;
        boolean cannotAccess = ((!Modifier.isPublic(method.getModifiers())
                || !Modifier.isPublic(method.getDeclaringClass().getModifiers()))
                && !method.trySetAccessible());
        if (cannotAccess) {
            method.setAccessible(true);
        }
        try {
            if (parameters == null || parameters.length == 0) {
                return (T) method.invoke(target);
            } else {
                return (T) method.invoke(target, parameters);
            }
        } catch (IllegalAccessException | InvocationTargetException | ClassCastException
                | UndeclaredThrowableException e) {
            Throwable cause = e.getCause();
            Throwable throwable = Objects.requireNonNullElse(cause, e);
            LOGGER.error("invokeMethod error. \n", throwable);
            if (throwException) {
                throw throwable;
            }
        }
        return null;
    }

    public static Object invokeSetMethod(Object target, String fieldName, Object arg, Class<?> parameterTypes) {
        Class<?> clazz = target.getClass();
        try {
            return invokeSetMethod(clazz, target, fieldName, arg, parameterTypes);
        } catch (NoSuchMethodException ignored) {
            LOGGER.error("", ignored);
        }
        return null;
    }

    /**
     * invoke set method
     * @param targetClass target class
     * @param target         target object
     * @param fieldName      field name
     * @param arg            arg
     * @param parameterTypes arg type
     * @throws NoSuchMethodException if this field has no getter method
     * @return invoke method result
     */
    public static Object invokeSetMethod(Class<?> targetClass, Object target, String fieldName, Object arg,
                                         Class<?> parameterTypes) throws NoSuchMethodException {

        var methodName = "set" + handleFieldName(fieldName);
        try {
            return invokeMethod(targetClass, target, methodName, new Class<?>[]{ parameterTypes }, new Object[]{arg});
        } catch (NoSuchMethodException e) {
            Throwable cause = e.getCause();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.error(fieldName + " set method not found \n", Objects.requireNonNullElse(cause, e));
            } else {
                LOGGER.error(fieldName + " set method not found \n" + e.getMessage());
            }
            methodName = fieldName;
            LOGGER.warn("try to invoke " + methodName + " method");
            try {
                return invokeMethod(targetClass, target, methodName, new Class<?>[]{ parameterTypes }, new Object[]{arg});
            } catch (NoSuchMethodException ex) {
                cause = ex.getCause();
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.error(methodName + " method not found \n", Objects.requireNonNullElse(cause, ex));
                } else {
                    LOGGER.error(methodName + " method not found \n" + ex.getMessage());
                }
                throw ex;
            } catch (InvocationTargetException ex) {
                cause = ex.getCause();
                LOGGER.error(methodName + " invoke error \n", Objects.requireNonNullElse(cause, ex));
            }
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            LOGGER.error(methodName + " invoke error \n", Objects.requireNonNullElse(cause, e));
        }
        return null;
    }

    public static Object invokeGetMethod(Object target, String fieldName) {
        Class<?> clazz = target.getClass();
        try {
            return invokeGetMethod(clazz, target, fieldName);
        } catch (NoSuchMethodException ignored) {
            LOGGER.error("", ignored);
        }
        return null;
    }

    private static Object invokeMethod(Class<?> targetClass, Object target, String methodName,
                                       Class<?>[] parameterTypes, Object[] parameters)
            throws NoSuchMethodException, InvocationTargetException {
        Method method = null;
        try {
            if (parameterTypes == null || parameterTypes.length == 0) {
                method = targetClass.getDeclaredMethod(methodName);
            } else
                method = targetClass.getDeclaredMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
            Class<?> superclass = targetClass.getSuperclass();
            if (superclass != Object.class) {
                return invokeMethod(superclass, target, methodName, parameterTypes, parameters);
            }
            throw new NoSuchMethodException("No " + methodName + " method");
        }
        if (method != null) {
            try {
                if (parameterTypes == null || parameterTypes.length == 0) {
                    return method.invoke(target);
                } else {
                    return method.invoke(target, parameters);
                }
            } catch (IllegalAccessException e) {
                boolean cannotAccess = ((!Modifier.isPublic(method.getModifiers())
                        || !Modifier.isPublic(method.getDeclaringClass().getModifiers()))
                        && !method.trySetAccessible());
                if (cannotAccess)
                    method.setAccessible(true);

                try {
                    if (parameterTypes == null || parameterTypes.length == 0) {
                        return method.invoke(target);
                    } else {
                        return method.invoke(target, parameters);
                    }
                } catch (IllegalAccessException ex) {
                    LOGGER.error(methodName + " invoke error \n" + ex.getMessage());
                    throw new RuntimeException(ex);
                }
            }
        }
        throw new NoSuchMethodException("No " + methodName + " method");
    }

    /**
     * invoke get method
     * @param targetClass target class
     * @param target    target object
     * @param fieldName field name
     * @throws NoSuchMethodException if this field has no getter method
     * @return invoke method result
     */
    public static Object invokeGetMethod(Class<?> targetClass, Object target, String fieldName)
            throws NoSuchMethodException {

        String methodName = "get" + handleFieldName(fieldName);
        try {
            return invokeMethod(targetClass, target, methodName, null, null);
        } catch (NoSuchMethodException e) {
            Throwable cause = e.getCause();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.error(fieldName + " `get` method not found\n ", Objects.requireNonNullElse(cause, e));
            } else {
                LOGGER.error(fieldName + " `get` method not found\n " + e.getMessage());
            }
            methodName = "is" + handleFieldName(fieldName);
            LOGGER.warn("try to invoke " + methodName + " method ");
            try {
                return invokeMethod(targetClass, target, methodName, null, null);
            } catch (NoSuchMethodException ex) {
                cause = ex.getCause();
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.error(fieldName + " `is` method not found \n", Objects.requireNonNullElse(cause, ex));
                } else {
                    LOGGER.error(fieldName + " `is` method not found \n" + ex.getMessage());
                }
                throw ex;
            } catch (InvocationTargetException ex) {
                cause = ex.getCause();
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.error(fieldName + " `is` method not found \n", Objects.requireNonNullElse(cause, ex));
                } else {
                    LOGGER.error(methodName + " invoke error \n" + ex.getMessage());
                }
            }
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            LOGGER.error(methodName + " invoke error \n", Objects.requireNonNullElse(cause, e));
        }
        return null;
    }

    /**
     * make char of field name upper
     *
     * @param fieldName filed name
     * @return string
     */
    private static String handleFieldName(String fieldName) {
        var string = fieldName.substring(0, 1).toUpperCase();
        if (fieldName.length() > 1) {
            string += fieldName.substring(1);
        }
        return string;
    }

    public static Set<Class<?>> getInterfaces(Class<?> clazz) {
        Set<Class<?>> result = new HashSet<>();

        for (var superClass = clazz; superClass != null && superClass != Object.class;
             superClass = superClass.getSuperclass()) {
            getInterfaces(result, superClass);
        }

        return result;
    }

    private static void getInterfaces(Set<Class<?>> result, Class<?> clazz) {
        Class<?>[] interfaces = clazz.getInterfaces();
        if (interfaces != null && interfaces.length > 0) {
            for (Class<?> anInterface : interfaces) {
                getInterfaces(result, anInterface);
                result.add(anInterface);
            }
        }
    }

    public static <T> List<Class<? extends T>> getSubClass(Class<T> parentClass) {
        List<Class<?>> allClass = new ArrayList<>();
        // TODO
        return getSubClass(allClass, parentClass);
    }

    @SuppressWarnings("unchecked")
    public static <T> List<Class<? extends T>> getSubClass(List<Class<?>> allClass, Class<T> parentClass) {
        List<Class<? extends T>> result = new ArrayList<>();
        for (var clazz : allClass) {
            boolean isTarget = (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers()))
                    && parentClass.isAssignableFrom(clazz);
            if (isTarget) {
                result.add((Class<? extends T>) clazz);
            }
        }
        return result;
    }

    /**
     * 从包package中获取所有的Class
     *
     * @param resources resources
     * @param classLoader class loader
     * @return class list
     */
    public static List<Class<?>> getAllClassByResources(String resources, ClassLoader classLoader) {
        // 第一个class类的集合
        List<Class<?>> classes = new ArrayList<>();
        // 是否循环迭代
        var recursive = true;

        if (classLoader == null)
            classLoader = ClassLoaderUtils.getClassLoader(ReflectionHelper.class);

        // 定义一个枚举的集合 并进行循环来处理这个目录下的things
        Enumeration<URL> dirs;
        try {
            dirs = classLoader.getResources(resources);
            // 循环迭代下去
            while (dirs.hasMoreElements()) {
                // 获取下一个元素
                var url = dirs.nextElement();
                // 得到协议的名称
                var protocol = url.getProtocol();
                // 如果是以文件的形式保存在服务器上
                if ("file".equals(protocol)) {
                    // 获取包的物理路径
                    var filePath = URLDecoder.decode(url.getFile(), StandardCharsets.UTF_8);
                    // 以文件的方式扫描整个包下的文件 并添加到集合中
                    findAndAddClassesInPackageByFile(classLoader, "", filePath, recursive, classes);
                } else if ("jar".equals(protocol) || "war".equals(protocol)) {
                    var tmp = StreamHelper.getClassFromJarByPackageName("", url, "", classLoader);
                    classes.addAll(tmp);
                }
            }
        } catch (IOException e) {
            LOGGER.error("", e);
        }

        return classes;
    }

    /**
     * 从包package中获取所有的Class
     *
     * @param packageName package name
     * @param classLoader class loader
     * @param resourceResolver resource resolver
     * @return class list
     */
    public static List<Class<?>> getAllClassByPackageName(String packageName, ClassLoader classLoader,
                                                          ResourceResolver resourceResolver) {
        LOGGER.trace(() -> "packageName: " + packageName);
        var packageDirName = packageName.replace('.', '/');
        if (!packageName.endsWith("**")) {
            packageDirName = packageDirName + "/**";
        }
        Set<String> resources = resourceResolver.getMatchedResources(packageDirName);
        return ResourcesHandler.getClassesByResources(resources, classLoader);
    }

    /**
     * 从包package中获取所有的Class
     *
     * @param packageName package name
     * @param classLoader class loader
     * @return class list
     */
    public static List<Class<?>> getAllClassByPackageName(String packageName, ClassLoader classLoader) {
        LOGGER.trace(() -> "packageName: " + packageName);
        var packageDirName = packageName.replace('.', '/');
        List<String> resources = ResourcesHandler.getAllClassPathResources(packageDirName, classLoader);
        return ResourcesHandler.getClassesByResources(resources, classLoader);

        /*// 第一个class类的集合
        List<Class<?>> classes = new ArrayList<>();
        // 是否循环迭代
        var recursive = true;
        // 获取包的名字 并进行替换
        var packageDirName = packageName.replace('.', '/');

        if (classLoader == null)
            classLoader = ClassLoaderUtils.getClassLoader(ReflectionHelper.class);

        // 定义一个枚举的集合 并进行循环来处理这个目录下的things
        Enumeration<URL> dirs;
        try {
            dirs = classLoader.getResources(packageDirName);
            // 循环迭代下去
            while (dirs.hasMoreElements()) {
                // 获取下一个元素
                var url = dirs.nextElement();
                // 得到协议的名称
                var protocol = url.getProtocol();
                // 如果是以文件的形式保存在服务器上
                if ("file".equals(protocol)) {
                    // 获取包的物理路径
                    var filePath = URLDecoder.decode(url.getFile(), StandardCharsets.UTF_8);
                    // 以文件的方式扫描整个包下的文件 并添加到集合中
                    findAndAddClassesInPackageByFile(classLoader, packageName, filePath, recursive, classes);
                } else if ("jar".equals(protocol)) {
                    var tmp = StreamHelper.getClassFromJarByPackageName(packageName, url, packageDirName, classLoader);
                    classes.addAll(tmp);
                }
            }
        } catch (IOException e) {
            LOGGER.error("", e);
        }

        return classes;*/
    }

    /**
     * 以文件的形式来获取包下的所有Class
     *
     * @param packageName 包名
     * @param packagePath 包路径
     * @param recursive 是否递归
     * @param classes 结果
     */
    private static void findAndAddClassesInPackageByFile(ClassLoader classLoader, String packageName,
                                                         String packagePath, final boolean recursive,
                                                         List<Class<?>> classes) {
        // 获取此包的目录 建立一个File
        var dir = new File(packagePath);
        // 如果不存在或者 也不是目录就直接返回
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        // 如果存在 就获取包下的所有文件 包括目录
        // 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
        var dirFiles = dir.listFiles(file -> (recursive && file.isDirectory()) || (file.getName().endsWith(".class")));
        // 循环所有文件
        assert dirFiles != null;

        for (var file : dirFiles) {
            // 如果是目录 则继续扫描
            if (file.isDirectory()) {
                String newPackageName = packageName + "." + file.getName();
                if (StringUtils.isBlank(packageName)) {
                    newPackageName = file.getName();
                }
                findAndAddClassesInPackageByFile(classLoader, newPackageName, file.getAbsolutePath(), recursive, classes);
            } else {
                String fileName = file.getName();
                if (fileName.endsWith(".class")) {
                    // 如果是java类文件 去掉后面的.class 只留下类名
                    var className = fileName.substring(0, file.getName().length() - 6);
                    try {
                        String newPackageName = packageName + "." + className;
                        if (StringUtils.isBlank(packageName)) {
                            newPackageName = className;
                        }
                        // 添加到集合中去
                        classes.add(classLoader.loadClass(newPackageName));
                    } catch (ClassNotFoundException e) {
                        LOGGER.error("", e);
                    }
                }
            }
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ReflectionHelper.class);
}
