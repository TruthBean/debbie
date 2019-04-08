package com.truthbean.code.debbie.core.reflection;

import com.truthbean.code.debbie.core.data.transformer.DataTransformerFactory;
import com.truthbean.code.debbie.core.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarFile;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/23 11:23.
 */
public class ReflectionHelper {
    private static final ReflectionHelper INSTANCE = new ReflectionHelper();

    public static <T> T newInstance(Class<T> type) {
        return newInstance(type, Constants.EMPTY_CLASS_ARRAY, null);
    }

    public static Type[] getActualTypes(Class clazz) {
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
            for (var type: interfaces) {
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

    public static <T> T newInstance(Class<T> type, Class[] parameterTypes, Object[] args) {
        var obj = getConstructor(type, parameterTypes);
        if (obj != null) {
            return newInstance(obj, args);
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
            LOGGER.error("new instance error", e);
        } finally {
            if (!flag) {
                constructor.setAccessible(flag);
            }
        }
        return null;
    }

    public static <T> Constructor<T> getConstructor(Class<T> type, Class[] parameterTypes) {
        try {
            Constructor<T> constructor = type.getDeclaredConstructor(parameterTypes);
            if (constructor.trySetAccessible()) {
                constructor.setAccessible(true);
            }
            return constructor;
        } catch (NoSuchMethodException e) {
            LOGGER.error("no such constructor", e);
        }
        return null;
    }

    public static List<Field> getDeclaredFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        for (var superClass = clazz; superClass != Object.class; superClass = superClass.getSuperclass()) {
            fields.addAll(Arrays.asList(superClass.getDeclaredFields()));
        }
        return fields;
    }

    public static List<Method> getDeclaredMethods(Class<?> clazz) {
        List<Method> fields = new ArrayList<>();
        for (var superClass = clazz; superClass != Object.class; superClass = superClass.getSuperclass()) {
            fields.addAll(Arrays.asList(superClass.getDeclaredMethods()));
        }
        return fields;
    }

    public static Object invokeSetMethod(Object target, Field field, Object arg) {
        var methodName = "set" + handleFieldName(field.getName());
        try {
            var method = target.getClass().getMethod(methodName, field.getType());
            var factory = new DataTransformerFactory();
            return method.invoke(target, factory.transform(arg, field.getType()));
        } catch (NoSuchMethodException e) {
            LOGGER.error(field.getName() + " set method not found. ", e);
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOGGER.error("", e);
        } catch (IllegalArgumentException e) {
            LOGGER.error("argument type is wrong. ", e);
        }
        return null;
    }

    /**
     * invoke set method
     *
     * @param target         target object
     * @param fieldName      field name
     * @param arg            arg
     * @param parameterTypes arg type
     * @return invoke method result
     */
    public static Object invokeSetMethod(Object target, String fieldName, Object arg, Class<?>... parameterTypes) {
        var methodName = "set" + handleFieldName(fieldName);
        try {
            var method = target.getClass().getMethod(methodName, parameterTypes);
            return method.invoke(target, arg);
        } catch (NoSuchMethodException e) {
            LOGGER.error(fieldName + " set method not found", e);
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOGGER.error("", e);
        }
        return null;
    }

    /**
     * invoke get method
     *
     * @param target    target object
     * @param fieldName field name
     * @return invoke method result
     */
    public static Object invokeGetMethod(Object target, String fieldName) {
        String methodName = "get" + handleFieldName(fieldName);
        try {
            Method method = target.getClass().getMethod(methodName);
            return method.invoke(target);
        } catch (NoSuchMethodException e) {
            LOGGER.warn(fieldName + " set method not found", e);
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOGGER.warn(fieldName + " invoke error", e);
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
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
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
                    // 如果是jar包文件
                    // 定义一个JarFile
                    JarFile jar;
                    try {
                        // 获取jar
                        jar = ((JarURLConnection) url.openConnection()).getJarFile();
                        // 从此jar包 得到一个枚举类
                        var entries = jar.entries();
                        // 同样的进行循环迭代
                        while (entries.hasMoreElements()) {
                            // 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
                            var entry = entries.nextElement();
                            var name = entry.getName();
                            // 如果是以/开头的
                            if (name.charAt(0) == '/') {
                                // 获取后面的字符串
                                name = name.substring(1);
                            }
                            // 如果前半部分和定义的包名相同
                            if (name.startsWith(packageDirName)) {
                                int idx = name.lastIndexOf('/');
                                // 如果以"/"结尾 是一个包
                                if (idx != -1) {
                                    // 获取包名 把"/"替换成"."
                                    packageName = name.substring(0, idx).replace('/', '.');
                                }
                                // 如果可以迭代下去 并且是一个包
                                // 如果是一个.class文件 而且不是目录
                                if (name.endsWith(".class") && !entry.isDirectory()) {
                                    // 去掉后面的".class" 获取真正的类名
                                    var className = name.substring(packageName.length() + 1, name.length() - 6);
                                    try {
                                        // 添加到classes
                                        classes.add(classLoader.loadClass(packageName + '.' + className));
                                    } catch (ClassNotFoundException e) {
                                        LOGGER.error("", e);
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        LOGGER.error("", e);
                    }
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
