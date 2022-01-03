package com.truthbean.debbie.check.event;

import com.truthbean.debbie.bean.GlobalBeanFactory;
import com.truthbean.debbie.concurrent.ThreadPooledExecutor;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.core.ApplicationFactory;
import com.truthbean.debbie.event.DefaultEventListenerBeanManager;
import com.truthbean.debbie.event.EventMulticaster;
import com.truthbean.debbie.event.DebbieEventPublisher;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

class EventTest {

    @Test
    void testEvent() {
        long start = System.currentTimeMillis();
        ThreadPooledExecutor factory = new ThreadPooledExecutor();
        EventMulticaster eventMulticaster = new DefaultEventListenerBeanManager(null, factory, null);
        // eventMulticaster.addEventListener(new TestEventListener());
        eventMulticaster.addEventListener(new Test2EventListener());

        long start1 = System.currentTimeMillis();
        Test2Event testEvent = new Test2Event(this, 2333);
        eventMulticaster.multicastEvent(testEvent);
        long end = System.currentTimeMillis();
        System.out.println(end - start1);
        System.out.println(end - start);
    }

    @Test
    void testEventListenerBeans() {
        long start = System.currentTimeMillis();
        ApplicationFactory factory = ApplicationFactory.configure(EventTest.class);

        ApplicationContext applicationContext = factory.getApplicationContext();
        GlobalBeanFactory globalBeanFactory = applicationContext.getGlobalBeanFactory();

        DebbieEventPublisher eventPublisher = globalBeanFactory.factory(DebbieEventPublisher.class);

        for (int i = 0; i < 100; i++) {
            long start1 = System.currentTimeMillis();
            Test2Event testEvent = new Test2Event(this, 2333 + i);
            eventPublisher.publishEvent(testEvent);
            Test2Event testEvent1 = new Test2Event(this, 44444 + i);
            eventPublisher.publishEvent(testEvent1);
            long end = System.currentTimeMillis();
            System.out.println(end - start1);
            System.out.println(end - start);
        }
    }

    @Test
    void test() {
        LocalDateTime dateTime = LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0);
        long milli = dateTime.toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
        System.out.println(milli);
    }
}
