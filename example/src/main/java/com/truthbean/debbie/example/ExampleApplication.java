package com.truthbean.debbie.example;

import com.truthbean.debbie.boot.DebbieApplicationFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author TruthBean
 * @since 0.0.1
 * @since 2019-03-31 18:02
 */
public class ExampleApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExampleApplication.class);

    public static void main(String[] args) {
        var application = DebbieApplicationFactory.factory();
        application.start(args);
    }

    /*public static void main(String[] args) {
        Map<Class<AbstractProperties>, Class> classClassMap = SpiLoader.loadPropertiesClasses();
        System.out.println(classClassMap);
    }*/
}