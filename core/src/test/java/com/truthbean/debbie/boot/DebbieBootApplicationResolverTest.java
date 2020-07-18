package com.truthbean.debbie.boot;

import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.bean.DebbieScan;
import com.truthbean.debbie.bean.GlobalBeanFactory;
import com.truthbean.debbie.reflection.ReflectionHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author truthbean/Rogar·Q
 * @since 0.0.2
 */
@DebbieBootApplication(scan = @DebbieScan(basePackages = "com.truthbean"))
class DebbieBootApplicationResolverTest {

    private DebbieApplicationFactory factory;

    @BeforeEach
    void setUp() {
        factory = DebbieApplicationFactory.configure(DebbieBootApplicationResolverTest.class);
    }

    @AfterEach
    void tearDown() {
        factory.release();
    }

    @BeanInject("hehe")
    private Object test;

    @Test
    void resolverApplicationClass() {
        GlobalBeanFactory globalBeanFactory = this.factory.getGlobalBeanFactory();
        DebbieBootApplicationResolverTest factory = globalBeanFactory.factory(DebbieBootApplicationResolverTest.class);
        ReflectionHelper.invokeMethod(factory.test, "hello");
    }
}