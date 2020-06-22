/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.spi;

import com.truthbean.debbie.io.StreamHelper;
import com.truthbean.debbie.properties.DebbieConfiguration;
import com.truthbean.debbie.properties.DebbieProperties;
import com.truthbean.debbie.proxy.MethodProxyHandler;
import com.truthbean.debbie.reflection.ClassLoaderUtils;
import com.truthbean.Logger;
import com.truthbean.logger.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.*;

/**
 * @author 璩诗斌
 * @since 0.0.1
 */
public class SpiLoader {

    private static final String META_INF = "META-INF";
    private static final String SPI = "com.truthbean.debbie.spi";

    public static <S> S loadProvider(Class<S> serviceClass) {
        var classLoader = ClassLoaderUtils.getClassLoader(serviceClass);
        return loadProvider(serviceClass, classLoader);
    }

    public static <S> S loadProvider(Class<S> serviceClass, ClassLoader classLoader) {
        S service = loadProvider(serviceClass, classLoader, null);
        if (service == null) {
            throw new NoServiceProviderException(serviceClass.getName());
        }
        return service;
    }

    public static <S> S loadProvider(Class<S> serviceClass, ClassLoader classLoader, S defaultService) {
        ServiceLoader<S> serviceLoader = ServiceLoader.load(serviceClass, classLoader);
        Iterator<S> search = serviceLoader.iterator();
        if (search.hasNext()) {
            return search.next();
        } else {
            return defaultService;
        }
    }

    public static <S> Set<S> loadProviders(Class<S> serviceClass) {
        var classLoader = ClassLoaderUtils.getClassLoader(serviceClass);
        return loadProviders(serviceClass, classLoader);
    }

    public static <S> Set<S> loadProviderSet(Class<S> serviceClass, ClassLoader classLoader) {
        Set<S> result = new HashSet<>();
        ServiceLoader<S> serviceLoader = ServiceLoader.load(serviceClass, classLoader);
        for (S s : serviceLoader) {
            result.add(s);
        }
        return result;
    }

    public static <S> Set<S> loadProviders(Class<S> serviceClass, ClassLoader classLoader) {
        Set<S> result = loadProviderSet(serviceClass, classLoader);
        if (result.isEmpty()) {
            throw new NoServiceProviderException(serviceClass.getName());
        }
        return result;
    }

    public static Set<File> loadServiceClassNames() {
        Set<File> result = new HashSet<>();

        var spi = META_INF + "/" + SPI;
        var classLoader = ClassLoaderUtils.getDefaultClassLoader();
        try {
            Enumeration<URL> resources = classLoader.getResources(spi);
            while (resources.hasMoreElements()) {
                var url = resources.nextElement();
                LOGGER.debug(url::toString);
                var file = new File(url.getFile());
                if (file.exists() && file.isDirectory()) {
                    result.add(file);
                    String[] list = file.list();
                    if (list != null) {
                        for (String s : list) {
                            LOGGER.debug(() -> s);
                        }
                    }

                }
            }
        } catch (IOException e) {
            LOGGER.error("", e);
        }

        return result;
    }

    public static <P extends DebbieProperties<C>, C extends DebbieConfiguration>
    Map<Class<P>, Class<C>> loadPropertiesClasses(ClassLoader classLoader) {
        Map<Class<P>, Class<C>> result = new HashMap<>();

        var spi = META_INF + "/" + SPI + "/debbie-properties";
        try {
            Enumeration<URL> resources = classLoader.getResources(spi);
            while (resources.hasMoreElements()) {
                var url = resources.nextElement();
                LOGGER.debug(() -> url.toString());
                var protocol = url.getProtocol();
                if ("file".equals(protocol)) {
                    var file = new File(url.getFile());
                    List<String> strings = StreamHelper.readFile(file);
                    resolvePropertiesClass(strings, result, classLoader);
                } else if ("jar".equals(protocol)) {
                    List<String> strings = StreamHelper.readFileInJar(url);
                    resolvePropertiesClass(strings, result, classLoader);
                }
            }
        } catch (IOException e) {
            LOGGER.error("", e);
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    private static <P extends DebbieProperties<C>, C extends DebbieConfiguration>
    void resolvePropertiesClass(List<String> strings, Map<Class<P>, Class<C>> map, ClassLoader classLoader) {
        for (String string : strings) {
            String[] split = string.split(" --> ");
            Class<P> propertiesClass = null;
            try {
                propertiesClass = (Class<P>) classLoader.loadClass(split[0]);
            } catch (Exception e) {
                LOGGER.error("", e);
            }

            Class<?> configClass = null;
            if (propertiesClass != null) {
                try {
                    configClass = classLoader.loadClass(split[1]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (propertiesClass != null && configClass != null) {
                map.put(propertiesClass, (Class<C>) configClass);
            }
        }
    }

    public static <A extends Annotation> Map<Class<A>, List<Class<? extends MethodProxyHandler>>> loadProxyHandler(ClassLoader classLoader) {
        Map<Class<A>, List<Class<? extends MethodProxyHandler>>> result = new HashMap<>();

        var spi = META_INF + "/" + SPI + "/debbie-proxy-handler";
        try {
            Enumeration<URL> resources = classLoader.getResources(spi);
            while (resources.hasMoreElements()) {
                var url = resources.nextElement();
                LOGGER.debug(url::toString);
                var protocol = url.getProtocol();
                if ("file".equals(protocol)) {
                    var file = new File(url.getFile());
                    List<String> strings = StreamHelper.readFile(file);
                    resolveProxyHandlerClass(strings, result, classLoader);
                } else if ("jar".equals(protocol)) {
                    List<String> strings = StreamHelper.readFileInJar(url);
                    resolveProxyHandlerClass(strings, result, classLoader);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    private static <A extends Annotation> void resolveProxyHandlerClass
            (List<String> strings, Map<Class<A>, List<Class<? extends MethodProxyHandler>>> map, ClassLoader classLoader) {
        for (String string : strings) {
            String[] split = string.split(" --> ");
            Class<A> annotationType = null;
            try {
                annotationType = (Class<A>) classLoader.loadClass(split[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Class<? extends MethodProxyHandler> handlerClass = null;
            if (annotationType != null) {
                try {
                    handlerClass = (Class<MethodProxyHandler>) classLoader.loadClass(split[1]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (handlerClass != null) {
                    var classes = map.get(annotationType);
                    if (classes == null) {
                        classes = new ArrayList<>();
                    }
                    classes.add(handlerClass);
                    map.put(annotationType, classes);
                }

            }
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(SpiLoader.class);
}
