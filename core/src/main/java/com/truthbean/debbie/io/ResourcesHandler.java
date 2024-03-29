/**
 * Copyright (c) 2024 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.io;

import com.truthbean.debbie.reflection.ClassLoaderUtils;
import com.truthbean.debbie.util.Constants;
import com.truthbean.core.util.StringUtils;
import com.truthbean.Logger;
import com.truthbean.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.jar.JarEntry;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-02-18 19:52
 */
public final class ResourcesHandler {
    private ResourcesHandler() {
    }

    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    /**
     * Find all class location resources with the given resource via the ClassLoader
     *
     * @param resource resource
     * @param classLoader class loader
     * @return String list
     */
    public static List<String> getAllClassPathResources(String resource, ClassLoader classLoader) {
        List<String> result = new ArrayList<>();
        // 是否循环迭代
        // var recursive = true;

        if (classLoader == null)
            classLoader = ClassLoaderUtils.getClassLoader(ResourcesHandler.class);

        // 定义一个枚举的集合 并进行循环来处理这个目录下的文件或文件夹
        Enumeration<URL> dirs;
        try {
            dirs = classLoader.getResources(resource);
            // 循环迭代下去
            while (dirs.hasMoreElements()) {
                // 获取下一个元素
                var url = dirs.nextElement();
                LOGGER.trace("scan resource: " + url);
                // 得到协议的名称
                var protocol = url.getProtocol();
                // graalvm protocol: file、resource
                // 如果是以文件的形式保存在服务器上
                if ("file".equals(protocol)) {
                    // 获取包的物理路径
                    var filePath = URLDecoder.decode(url.getFile(), StandardCharsets.UTF_8);
                    // 以文件的方式扫描整个包下的文件 并添加到集合中
                    findAllResourcesInPackageByFile(resource, filePath, result);
                } else if ("jar".equals(protocol) || "war".equals(protocol) || "resource".equals(protocol)) {
                    getResourcesFromJarByPackageName(url, resource, result);
                }
            }
        } catch (IOException e) {
            LOGGER.error("", e);
        }

        return result;
    }

    /**
     * 获取包下的所有文件
     *
     * @param path    文件路径
     * @param dirPath 包路径
     * @param resources 结果
     */
    public static void findAllResourcesInPackageByFile(String path, String dirPath, List<String> resources) {
        // 获取此包的目录 建立一个File
        var dir = new File(dirPath);
        // 如果不存在或者 也不是目录就直接返回
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        var dirFiles = dir.listFiles();
        // 循环所有文件
        assert dirFiles != null;

        for (var file : dirFiles) {
            // 如果是目录 则继续扫描
            if (file.isDirectory()) {
                String newPackageName;
                if (StringUtils.isBlank(path)) {
                    newPackageName = file.getName();
                } else {
                    newPackageName = path + "/" + file.getName();
                }
                findAllResourcesInPackageByFile(newPackageName, file.getAbsolutePath(), resources);
            } else {
                String fileName = file.getName();
                if (StringUtils.hasText(path)) {
                    resources.add(path + "/" + fileName);
                } else {
                    resources.add(fileName);
                }
            }
        }
    }

    public static void getResourcesFromJarByPackageName(URL url, String packageDirName, List<String> result) {
        List<JarEntry> filesInJar = StreamHelper.getFilesInJar(url);
        for (JarEntry entry : filesInJar) {
            getResourcesUnderPackageInJar(entry, packageDirName, result);
        }
    }

    private static void getResourcesUnderPackageInJar(JarEntry entry, String dirName, List<String> result) {
        var name = entry.getName();
        // 如果是以/开头的
        if (name.charAt(0) == '/') {
            // 获取后面的字符串
            name = name.substring(1);
        }
        // 如果前半部分和定义的包名相同
        if (name.startsWith(dirName) && !entry.isDirectory()) {
            LOGGER.trace("resource: " + name);
            result.add(name);
        }
    }

    public static synchronized List<Class<?>> getClassesByResources(final Collection<String> resources,
                                                                    final ClassLoader classLoader) {
        final List<Class<?>> classes = new ArrayList<>();
        if (resources == null || resources.isEmpty() || classLoader == null)
            return classes;
        for (String name : resources) {
            // 如果是以/或.开头
            while (name.charAt(0) == '/' || name.charAt(0) == '.') {
                // 获取后面的字符串
                name = name.substring(1);
            }
            if (name.startsWith("META-INF") || "module-info.class".equals(name) || "package-info.class".equals(name) || name.endsWith("/")) {
                continue;
            }
            if (name.endsWith(".class")) {
                var className = name.replace('/', '.').substring(0, name.length() - 6);
                Class<?> clazz = null;
                try {
                    // 添加到classes
                    clazz = classLoader.loadClass(className);
                    LOGGER.trace("load class<" + className + "> to preparing registered. ");
                } catch (NoClassDefFoundError | ClassNotFoundException e) {
                    LOGGER.debug(() -> "load class<" + className + "> error because " + e.getMessage());
                }
                if (clazz != null) {
                    classes.add(clazz);
                }
            }
        }
        return classes;
    }

    private static InputStream handle(String resource) {
        String tempResource = resource;
        if (resource.contains(Constants.CLASSPATH)) {
            tempResource = resource.substring(10);
        }
        if (resource.contains(Constants.CLASSPATHS)) {
            tempResource = resource.substring(11);
        }
        InputStream in;
        in = ClassLoaderUtils.getDefaultClassLoader().getResourceAsStream(tempResource);
        if (in == null) {
            in = ResourcesHandler.class.getResourceAsStream(tempResource);
        }
        if (in == null && tempResource.startsWith("/")) {
            tempResource = tempResource.substring(1);
        }
        in = ClassLoaderUtils.getDefaultClassLoader().getResourceAsStream(tempResource);
        if (in == null) {
            in = ResourcesHandler.class.getResourceAsStream(tempResource);
        }

        return in;
    }

    public static String handleStaticResource(String resource) {
        try {
            InputStream in = handle(resource);
            if (in == null) return null;
            String result = null;
            try (InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8);
                 StringWriter writer = new StringWriter()) {

                char[] buffer = new char[DEFAULT_BUFFER_SIZE];
                int n;
                while (-1 != (n = reader.read(buffer))) {
                    writer.write(buffer, 0, n);
                }

                writer.flush();
                result = writer.toString();
            }

            return result;
        } catch (Exception e) {
            LOGGER.error("", e);
        }

        return null;
    }

    public static byte[] handleStaticBytesResource(String resource) {
        InputStream inputStream = handle(resource);
        if (inputStream == null) return null;
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            // inputStream.transferTo(output);
            copy(inputStream, output);
            return output.toByteArray();
        } catch (IOException e) {
            LOGGER.error("", e);
        }
        return new byte[0];
    }

    public static long copy(InputStream input, OutputStream output) throws IOException {
        final int eof = -1;

        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];

        long count = 0;
        int n;
        while (eof != (n = input.read(buffer, 0, DEFAULT_BUFFER_SIZE))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourcesHandler.class);

}
