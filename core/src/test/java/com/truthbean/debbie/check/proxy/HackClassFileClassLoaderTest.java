package com.truthbean.debbie.check.proxy;

import com.truthbean.debbie.proxy.HackClassFileClassLoader;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;

/**
 * @author TruthBean/Rogar·Q
 * @since Created on 2020-08-15 11:36
 */
class HackClassFileClassLoaderTest {

    @Test
    void loadClass() throws InterruptedException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        while (true) {
            HackClassFileClassLoader classLoader = new HackClassFileClassLoader("D:\\develop\\java\\debbie\\core\\build\\classes\\java\\tmp");
            Thread.sleep(1000);
            var className = "com.truthbean.debbie.check.proxy.CustomServiceImpl";
            @SuppressWarnings("unchecked")
            Class<? extends CustomService> serviceClass = (Class<? extends CustomService>) classLoader.loadClass(className, true);
            System.out.println(serviceClass);
            CustomService customService = serviceClass.getConstructor().newInstance();
            String hello = customService.hello();
            System.out.println(hello);
            classLoader.unloadClass(className);
            serviceClass = null;
            customService = null;
            System.gc();
        }
    }
}