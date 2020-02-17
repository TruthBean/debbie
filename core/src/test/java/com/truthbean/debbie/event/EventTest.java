package com.truthbean.debbie.event;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.boot.DebbieApplicationFactory;
import com.truthbean.debbie.reflection.ClassLoaderUtils;
import org.junit.jupiter.api.Test;

public class EventTest {

    @Test
    public void testEvent() {
        long start = System.currentTimeMillis();
        DebbieEventMulticaster eventMulticaster = new DefaultEventMulticaster();
        eventMulticaster.addEventListener(new TestEventListener());
        eventMulticaster.addEventListener(new Test2EventListener());

        long start1 = System.currentTimeMillis();
        TestEvent testEvent = new TestEvent(this, "test22222");
        eventMulticaster.multicastEvent(testEvent);
        long end = System.currentTimeMillis();
        System.out.println(end - start1);
        System.out.println(end - start);
    }

    @Test
    public void testEventListenerBeans() {
        long start = System.currentTimeMillis();
        ClassLoader classLoader = ClassLoaderUtils.getClassLoader(EventTest.class);
        DebbieApplicationFactory factory = new DebbieApplicationFactory(classLoader);
        factory.config(EventTest.class);
        factory.callStarter();

        BeanFactoryHandler beanFactoryHandler = factory.getBeanFactoryHandler();

        DebbieEventPublisher eventPublisher = beanFactoryHandler.factory(DebbieEventPublisher.class);

        long start1 = System.currentTimeMillis();
        Test2Event testEvent = new Test2Event(this, 2333);
        eventPublisher.publishEvent(testEvent);
        Test2Event testEvent1 = new Test2Event(this, 44444);
        eventPublisher.publishEvent(testEvent1);
        long end = System.currentTimeMillis();
        System.out.println(end - start1);
        System.out.println(end - start);
    }
}
