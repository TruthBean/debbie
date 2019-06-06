package com.truthbean.debbie.reflection;

import com.truthbean.debbie.data.transformer.DataTransformerFactory;
import com.truthbean.debbie.io.StreamHelper;
import com.truthbean.debbie.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
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
        } catch (NoSuchMethodException var3) {
            return null;
        }
    }

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

    public static Type[] getActualTypes(@SuppressWarnings("rawtypes") Class clazz, int typeLength) {
        if (clazz == Object.class || clazz == Void.class) {
            return null;
        }
        Type[] types = clazz.getTypeParameters();
        if (types != null && types.length >= typeLength) {
            return types;
        }
        Type genType = clazz.getGenericSuperclass();
        if (genType instanceof ParameterizedType) {
            Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
            if (params.length >= typeLength) {
                return params;
            }
        }

        Type[] interfaces = clazz.getGenericInterfaces();
        if (interfaces.length > 0) {
            for (var type : interfaces) {
                if (type instanceof ParameterizedType) {
                    Type[] params = ((ParameterizedType) type).getActualTypeArguments();
                    if (params != null && params.length >= typeLength) {
                        return params;
                    }
                }
            }
        } else {
            return getActualTypes(clazz.getSuperclass(), typeLength);
        }

        return null;
    }

    public static Type[] getActualTypes(@SuppressWarnings("rawtypes") Class clazz) {
        if (clazz == Object.class || clazz == Void.class) {
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

    public static <T> T newInstance(Class<T> type, @SuppressWarnings("rawtypes") Class[] parameterTypes, Object[] args) {
        var constructor = getConstructor(type, parameterTypes);
        if (constructor != null) {
            return newInstance(constructor, args);
        }
        return null;
    }

    public static <T> T newInstance(final Constructor<T> constructor, final Object[] args) {

        boolean flag = constructor.canAccess(null);
        try {
            if (!flag) {
                constructor.setAccessible(true);
            }
            return constructor.newInstance(args);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            Throwable cause = e.getCause();
            LOGGER.error("new instance error. \n", Objects.requireNonNullElse(cause, e));
        } finally {
            if (!flag) {
                constructor.setAccessible(flag);
            }
        }
        return null;
    }

    public static Object invokeStaticMethod(String methodName, Class<?> targetClass) {
        try {
            Method declaredMethod = targetClass.getDeclaredMethod(methodName);
            if (declaredMethod.trySetAccessible()) {
                declaredMethod.setAccessible(true);
            }
            return declaredMethod.invoke(null);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            Throwable cause = e.getCause();
            LOGGER.error("invoke static method(" + methodName + " error ). \n", Objects.requireNonNullElse(cause, e));
        }
        return null;
    }

    public static <T> Constructor<T> getConstructor(Class<T> type, @SuppressWarnings("rawtypes") Class[] parameterTypes) {
        try {
            Constructor<T> constructor = type.getDeclaredConstructor(parameterTypes);
            if (constructor.trySetAccessible()) {
                constructor.setAccessible(true);
            }
            return constructor;
        } catch (NoSuchMethodException e) {
            Throwable cause = e.getCause();
            LOGGER.error("no such constructor. \n", Objects.requireNonNullElse(cause, e));
        }
        return null;
    }

    public static List<Field> getDeclaredFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        for (var superClass = clazz; superClass != null && superClass != Object.class; superClass = superClass.getSuperclass()) {
            Field[] declaredFields = superClass.getDeclaredFields();
            if (declaredFields.length > 0) {
                fields.addAll(Arrays.asList(declaredFields));
            }
        }
        return fields;
    }

    public static List<Method> getDeclaredMethods(Class<?> clazz) {
        List<Method> fields = new ArrayList<>();
        for (var superClass = clazz; superClass != null && superClass != Object.class; superClass = superClass.getSuperclass()) {
            Method[] declaredMethods = superClass.getDeclaredMethods();
            if (declaredMethods.length > 0) {
                fields.addAll(Arrays.asList(declaredMethods));
            }
        }
        return fields;
    }

    public static Set<Method> getInterfaceDefaultMethods(Class<?> clazz) {
        Set<Method> methods = new HashSet<>();

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

    public static Method getDeclaredMethod(Class<?> clazz, String methodName) {
        for (var superClass = clazz; superClass != null && superClass != Object.class; superClass = superClass.getSuperclass()) {
            Method[] declaredMethods = superClass.getDeclaredMethods();
            if (declaredMethods.length > 0) {
                for (Method declaredMethod : declaredMethods) {
                    if (methodName.equals(declaredMethod.getName())) {
                        return declaredMethod;
                    }
                }
            }
        }
        return null;
    }

    public static Object invokeSetMethod(Object target, Field field, Object arg) {
        Class<?> clazz = target.getClass();
        for (var superClass = clazz; superClass != null && superClass != Object.class; superClass = superClass.getSuperclass()) {
            try {
                return invokeSetMethod(superClass, target, field.getName(), field.getType(), arg);
            } catch (NoSuchMethodException ignored) {
            }
        }
        return null;
    }

    public static Object invokeSetMethod(Class<?> targetClass, Object target, String fieldName, Class<?> fieldType, Object arg)
            throws NoSuchMethodException {
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
                LOGGER.error(fieldName + " set method not found. \n", Objects.requireNonNullElse(cause, e));
            } else {
                LOGGER.error(fieldName + " set method not found. \n", e.getMessage());
            }

            methodName = "is" + handleFieldName(fieldName);
            LOGGER.warn("try to invoke " + methodName + " method");
            try {
                var method = targetClass.getMethod(methodName, fieldType);
                if (TypeHelper.isRawBaseType(fieldType)) {
                    fieldType = TypeHelper.getWrapperClass(fieldType);
                }
                return method.invoke(target, DataTransformerFactory.transform(arg, fieldType));
            } catch (NoSuchMethodException ex) {
                cause = ex.getCause();
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.error(methodName + " method not found \n", Objects.requireNonNullElse(cause, ex));
                } else {
                    LOGGER.error(methodName + " method not found \n" + ex.getMessage());
                }
                throw ex;
            } catch (IllegalAccessException | InvocationTargetException ex) {
                cause = ex.getCause();
                LOGGER.error(methodName + " invoke error \n", Objects.requireNonNullElse(cause, ex));
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
            if (field.trySetAccessible()) {
                field.setAccessible(true);
            }
            field.set(target, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static <T> T invokeMethod(Object target, Method method, Object... parameters) {
        try {
            if (parameters == null || parameters.length == 0) {
                return (T) method.invoke(target);
            } else {
                return (T) method.invoke(target, parameters);
            }
        } catch (IllegalAccessException | InvocationTargetException | ClassCastException e) {
            Throwable cause = e.getCause();
            LOGGER.error("invokeMethod error. \n", Objects.requireNonNullElse(cause, e));
        }
        return null;
    }

    public static Object invokeSetMethod(Object target, String fieldName, Object arg, Class<?>... parameterTypes) {
        Class<?> clazz = target.getClass();
        for (var superClass = clazz; superClass != null && superClass != Object.class; superClass = superClass.getSuperclass()) {
            try {
                return invokeSetMethod(superClass, target, fieldName, arg, parameterTypes);
            } catch (NoSuchMethodException ignored) {
            }
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
                                         Class<?>... parameterTypes) throws NoSuchMethodException {

        var methodName = "set" + handleFieldName(fieldName);
        try {
            var method = targetClass.getMethod(methodName, parameterTypes);
            return method.invoke(target, arg);
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
                var method = targetClass.getMethod(methodName, parameterTypes);
                return method.invoke(target, arg);
            } catch (NoSuchMethodException ex) {
                cause = ex.getCause();
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.error(methodName + " method not found \n", Objects.requireNonNullElse(cause, ex));
                } else {
                    LOGGER.error(methodName + " method not found \n" + ex.getMessage());
                }
                throw ex;
            } catch (IllegalAccessException | InvocationTargetException ex) {
                cause = ex.getCause();
                LOGGER.error(methodName + " invoke error \n", Objects.requireNonNullElse(cause, ex));
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            Throwable cause = e.getCause();
            LOGGER.error(methodName + " invoke error \n", Objects.requireNonNullElse(cause, e));
        }
        return null;
    }

    public static Object invokeGetMethod(Object target, String fieldName) {
        Class<?> clazz = target.getClass();
        for (var superClass = clazz; superClass != null && superClass != Object.class; superClass = superClass.getSuperclass()) {
            try {
                return invokeGetMethod(superClass, target, fieldName);
            } catch (NoSuchMethodException ignored) {
            }
        }
        return null;
    }

    /**
     * invoke get method
     * @param targetClass target class
     * @param target    target object
     * @param fieldName field name
     * @throws NoSuchMethodException if this field has no getter method
     * @return invoke method result
     */
    public static Object invokeGetMethod(Class<?> targetClass, Object target, String fieldName) throws NoSuchMethodException {

        String methodName = "get" + handleFieldName(fieldName);
        try {
            Method method = targetClass.getDeclaredMethod(methodName);
            return method.invoke(target);
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
                Method method = targetClass.getDeclaredMethod(methodName);
                return method.invoke(target);
            } catch (NoSuchMethodException ex) {
                cause = ex.getCause();
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.error(fieldName + " `is` method not found \n", Objects.requireNonNullElse(cause, ex));
                } else {
                    LOGGER.error(fieldName + " `is` method not found \n" + ex.getMessage());
                }
                throw ex;
            } catch (IllegalAccessException | InvocationTargetException ex) {
                cause = ex.getCause();
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.error(fieldName + " `is` method not found \n", Objects.requireNonNullElse(cause, ex));
                } else {
                    LOGGER.error(methodName + " invoke error \n" + ex.getMessage());
                }
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
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

    public static <T> List<Class<? extends T>> getSubClass(Class<T> parentClass) {
        List<Class<?>> allClass = new ArrayList<>();
        // TODO
        return getSubClass(allClass, parentClass);
    }

    public static <T> List<Class<? extends T>> getSubClass(List<Class<?>> allClass, Class<T> parentClass) {
        List<Class<? extends T>> result = new ArrayList<>();
        for (var clazz : allClass) {
            boolean isTarget = (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) && parentClass.isAssignableFrom(clazz);
            if (isTarget) {
                result.add((Class<? extends T>) clazz);
            }
        }
        return result;
    }

    /**
     * 从包package中获取所有的Class
     *
     * @param packageName package name
     * @return class list
     */
    public static List<Class<?>> getAllClassByPackageName(String packageName) {

        // 第一个class类的集合
        List<Class<?>> classes = new ArrayList<>();
        // 是否循环迭代
        var recursive = true;
        // 获取包的名字 并进行替换
        var packageDirName = packageName.replace('.', '/');

        var classLoader = ClassLoaderUtils.getDefaultClassLoader();

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

        return classes;
    }

    /**
     * 以文件的形式来获取包下的所有Class
     *
     * @param packageName
     * @param packagePath
     * @param recursive
     * @param classes
     */
    private static void findAndAddClassesInPackageByFile(ClassLoader classLoader, String packageName, String packagePath, final boolean recursive, List<Class<?>> classes) {
        // 获取此包的目录 建立一个File
        var dir = new File(packagePath);
        // 如果不存在或者 也不是目录就直接返回
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        // 如果存在 就获取包下的所有文件 包括目录
        // 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
        var dirfiles = dir.listFiles(file -> (recursive && file.isDirectory()) || (file.getName().endsWith(".class")));
        // 循环所有文件
        assert dirfiles != null;

        for (var file : dirfiles) {
            // 如果是目录 则继续扫描
            if (file.isDirectory()) {
                findAndAddClassesInPackageByFile(classLoader, packageName + "." + file.getName(), file.getAbsolutePath(), recursive, classes);
            } else {
                // 如果是java类文件 去掉后面的.class 只留下类名
                var className = file.getName().substring(0, file.getName().length() - 6);
                try {
                    // 添加到集合中去
                    classes.add(classLoader.loadClass(packageName + '.' + className));
                } catch (ClassNotFoundException e) {
                    LOGGER.error("", e);
                }
            }
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ReflectionHelper.class);
}
