/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.proxy;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.SecureClassLoader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-08-15 10:29
 */
public class HackClassFileClassLoader extends SecureClassLoader {
    private final String classPath;
    private final Map<String, Class<?>> cache;

    /**
     * WARN:
     *  The parent delegation of the classloader has been destroyed here,
     *  so the classPath must be a path that contains the target class, not the path of the original class
     * @param classPath the path to be loaded class
     */
    public HackClassFileClassLoader(String classPath) {
        super("debbieHotSwapClassLoader", ClassLoader.getSystemClassLoader());
        this.classPath = classPath;
        this.cache = new ConcurrentHashMap<>();
    }

    @Override
    public Class<?> findClass(String className) throws ClassNotFoundException {
        try {
            StringBuilder sb = new StringBuilder(classPath);
            String classname = className.replace('.', File.separatorChar) + ".class";
            sb.append(File.separator).append(classname);
            File classFile = new File(sb.toString());
            if (classFile.exists()) {
                LOGGER.trace("load class file: " + classFile);
                return instantiateClass(className, new FileInputStream(classFile), classFile.length());
            }
        } catch (Throwable e) {
            LOGGER.error("load class from file error.", e);
        }
        return null;
    }

    private Class<?> instantiateClass(String name, InputStream fin, long len) {
        try {
            byte[] raw = new byte[(int) len];
            fin.read(raw);
            fin.close();
            Class<?> cls = super.defineClass(name, raw, 0, raw.length);
            this.cache.put(name, cls);
            return cls;
        } catch (Throwable e) {
            LOGGER.error("define class by bytes error.", e);
        }
        return null;
    }

    @Override
    public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> cls = this.findClass(name);
        if (cls == null) {
            if (this.cache.containsKey(name)) {
                cls = this.cache.get(name);
            }
            if (cls == null)
                cls = super.findLoadedClass(name);
        }
        if (cls == null)
            cls = ClassLoader.getSystemClassLoader().loadClass(name);
        if (cls == null)
            throw new ClassNotFoundException(name);
        if (resolve)
            super.resolveClass(cls);
        return cls;
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return this.loadClass(name, false);
    }

    public void unloadClass(String className) {
        this.cache.remove(className);
        StringBuilder sb = new StringBuilder(classPath);
        String classname = className.replace('.', File.separatorChar) + ".class";
        sb.append(File.separator).append(classname);
        File classFile = new File(sb.toString());
        if (classFile.exists() && classFile.delete());
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(HackClassFileClassLoader.class);
}
