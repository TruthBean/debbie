package com.truthbean.debbie.boot;

import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.bean.DebbieScan;
import com.truthbean.debbie.reflection.ReflectionHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author truthbean/Rogar·Q
 * @since 0.0.2
 */
@DebbieBootApplication(scan = @DebbieScan(basePackages = "com.truthbean"))
public class DebbieBootApplicationResolverTest {

    private DebbieApplicationFactory factory;

    @BeforeEach
    void setUp() {
        ClassLoader classLoader = DebbieBootApplicationResolverTest.class.getClassLoader();
        factory = new DebbieApplicationFactory(classLoader);
        factory.config(DebbieBootApplicationResolverTest.class);
        factory.callStarter();
    }

    @AfterEach
    void tearDown() {
        factory.release();
    }

    @BeanInject("hehe")
    private Object test;

    @Test
    void resolverApplicationClass() {
        DebbieBootApplicationResolverTest factory = this.factory.factory(DebbieBootApplicationResolverTest.class);
        ReflectionHelper.invokeMethod(factory.test, "hello");
    }
}