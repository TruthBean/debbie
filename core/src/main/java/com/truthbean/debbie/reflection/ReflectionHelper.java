/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.reflection;

import com.truthbean.Logger;
import com.truthbean.core.util.ClassHelper;
import com.truthbean.core.util.ReflectionUtils;
import com.truthbean.transformer.TransformerFactory;
import com.truthbean.debbie.io.ResourceResolver;
import com.truthbean.debbie.io.ResourcesHandler;
import com.truthbean.debbie.io.StreamHelper;
import com.truthbean.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.*;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/23 11:23.
 */
public class ReflectionHelper extends ReflectionUtils {

    private ReflectionHelper() {
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
            ReflectionUtils.setField(target, field, arg);
        }
    }

    public static Object invokeSetMethod(Class<?> targetClass, Object target, String fieldName,
                                         Class<?> fieldType, Object arg) throws NoSuchMethodException {
        var methodName = "set" + ReflectionUtils.handleFieldName(fieldName);

        try {
            var method = targetClass.getMethod(methodName, fieldType);
            boolean accessible = true;
            if (method.trySetAccessible()) {
                method.setAccessible(true);
                accessible = false;
            }
            Object result = null;
            if (ClassHelper.isRawBaseType(fieldType)) {
                fieldType = ClassHelper.getWrapperClass(fieldType);
                result = method.invoke(target, TransformerFactory.transform(arg, fieldType));
                if (!accessible) {
                    method.setAccessible(false);
                }
                return result;
            }
            if (arg != null && fieldType.isAssignableFrom(arg.getClass()))
                result = method.invoke(target, arg);
            else
                result = method.invoke(target, TransformerFactory.transform(arg, fieldType));
            if (!accessible) {
                method.setAccessible(false);
            }
            return result;
        } catch (NoSuchMethodException e) {
            Throwable cause = e.getCause();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(targetClass.getName() + "." + fieldName + " set method not found. \n",
                        Objects.requireNonNullElse(cause, e));
            } else {
                LOGGER.debug(targetClass.getName() + "." + fieldName + " set method not found. \n" + e.getMessage());
            }
            throw e;
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
                    ReflectionUtils.findAndAddClassesInPackageByFile(classLoader, "", filePath, recursive, classes);
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
    public static List<Class<?>> getAllClassByPackageName(String packageName, final ClassLoader classLoader,
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
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ReflectionHelper.class);
}
