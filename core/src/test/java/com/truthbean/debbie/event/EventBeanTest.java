package com.truthbean.debbie.event;

import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.test.DebbieApplicationTest;
import org.junit.jupiter.api.Test;

@DebbieApplicationTest
public class EventBeanTest {

    @Test
    public void testEventListenerBeans(@BeanInject DebbieEventPublisher eventPublisher) {
        long start1 = System.currentTimeMillis();
        Test2Event testEvent = new Test2Event(this, 2333);
        eventPublisher.publishEvent(testEvent);
        Test2Event testEvent1 = new Test2Event(this, 44444);
        eventPublisher.publishEvent(testEvent1);
        long end = System.currentTimeMillis();
        System.out.println(end - start1);
    }

    @Test
    public void testEventListener(@BeanInject DebbieEventPublisher debbieEventPublisher, @BeanInject("test") TestEvent testEvent) {
        debbieEventPublisher.publishEvent(testEvent);
    }
}
