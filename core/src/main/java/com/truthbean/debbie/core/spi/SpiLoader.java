package com.truthbean.debbie.core.spi;

import com.truthbean.debbie.core.io.StreamHelper;
import com.truthbean.debbie.core.properties.BaseProperties;
import com.truthbean.debbie.core.reflection.ClassLoaderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
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
        var classLoader = ClassLoaderUtils.getClassLoader(SpiLoader.class);
        return loadProvider(serviceClass, classLoader);
    }

    public static <S> S loadProvider(Class<S> serviceClass, ClassLoader classLoader) {
        ServiceLoader<S> serviceLoader = ServiceLoader.load(serviceClass, classLoader);
        Iterator<S> search = serviceLoader.iterator();
        if (search.hasNext()) {
            return search.next();
        } else {
            throw new NoServiceProviderException(serviceClass.getName());
        }
    }

    public static <S> Set<S> loadProviders(Class<S> serviceClass) {
        Set<S> result = new HashSet<>();
        ServiceLoader<S> serviceLoader = ServiceLoader.load(serviceClass);
        Iterator<S> search = serviceLoader.iterator();
        if (search.hasNext()) {
            result.add(search.next());
        } else {
            throw new NoServiceProviderException(serviceClass.getName());
        }
        return result;
    }

    public static <S> Set<S> loadProviders(Class<S> serviceClass, ClassLoader classLoader) {
        Set<S> result = new HashSet<>();
        ServiceLoader<S> serviceLoader = ServiceLoader.load(serviceClass, classLoader);
        Iterator<S> search = serviceLoader.iterator();
        if (search.hasNext()) {
            result.add(search.next());
        } else {
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
                LOGGER.debug(url.toString());
                var file = new File(url.getFile());
                if (file.exists() && file.isDirectory()) {
                    result.add(file);
                    String[] list = file.list();
                    if (list != null) {
                        for (String s : list) {
                            LOGGER.debug(s);
                        }
                    }

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static <P extends BaseProperties> Map<Class<P>, Class> loadPropertiesClasses(ClassLoader classLoader) {
        Map<Class<P>, Class> result = new HashMap<>();

        var spi = META_INF + "/" + SPI + "/debbie-properties";
        try {
            Enumeration<URL> resources = classLoader.getResources(spi);
            while (resources.hasMoreElements()) {
                var url = resources.nextElement();
                LOGGER.debug(url.toString());
                var protocol = url.getProtocol();
                if ("file".equals(protocol)) {
                    var file = new File(url.getFile());
                    List<String> strings = StreamHelper.readFile(file);
                    resolveClass(strings, result, classLoader);
                } else if ("jar".equals(protocol)) {
                    List<String> strings = StreamHelper.readFileInJar(spi, url, classLoader);
                    resolveClass(strings, result, classLoader);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    private static <P extends BaseProperties> void resolveClass
            (List<String> strings, Map<Class<P>, Class> map, ClassLoader classLoader) {
        for (String string : strings) {
            String[] split = string.split(" --> ");
            Class<P> propertiesClass = null;
            try {
                propertiesClass = (Class<P>) classLoader.loadClass(split[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Class configClass = null;
            if (propertiesClass != null) {
                try {
                    configClass = classLoader.loadClass(split[1]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (propertiesClass != null && configClass != null) {
                map.put(propertiesClass, configClass);
            }
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(SpiLoader.class);
}
