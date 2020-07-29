package com.truthbean.debbie.check.boot;

import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.bean.DebbieScan;
import com.truthbean.debbie.bean.GlobalBeanFactory;
import com.truthbean.debbie.boot.DebbieBootApplication;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.core.ApplicationFactory;
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

    private ApplicationFactory factory;

    @BeforeEach
    void setUp() {
        factory = ApplicationFactory.configure(DebbieBootApplicationResolverTest.class);
    }

    @AfterEach
    void tearDown() {
        factory.release();
    }

    @BeanInject("hehe")
    private Object test;

    @Test
    void resolverApplicationClass() {
        ApplicationContext context = this.factory.getApplicationContext();
        GlobalBeanFactory globalBeanFactory = context.getGlobalBeanFactory();
        DebbieBootApplicationResolverTest factory = globalBeanFactory.factory(DebbieBootApplicationResolverTest.class);
        ReflectionHelper.invokeMethod(factory.test, "hello");
    }
}