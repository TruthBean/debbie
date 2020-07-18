package com.truthbean.debbie.event;

import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.bean.BeanType;
import com.truthbean.debbie.bean.DebbieBeanInfo;
import com.truthbean.debbie.bean.GlobalBeanFactory;
import com.truthbean.debbie.boot.DebbieApplication;
import com.truthbean.debbie.boot.DebbieApplicationFactory;
import com.truthbean.debbie.test.DebbieApplicationTest;
import org.junit.jupiter.api.Test;

@DebbieApplicationTest
class EventBeanTest {

    @Test
    void testEventListenerBeans(@BeanInject DebbieEventPublisher eventPublisher) {
        long start1 = System.currentTimeMillis();
        Test2Event testEvent = new Test2Event(this, 2333);
        eventPublisher.publishEvent(testEvent);
        Test2Event testEvent1 = new Test2Event(this, 44444);
        eventPublisher.publishEvent(testEvent1);
        long end = System.currentTimeMillis();
        System.out.println(end - start1);
    }

    @Test
    void testEventListener(@BeanInject DebbieEventPublisher debbieEventPublisher,
                                  @BeanInject("test") TestEvent testEvent) {
        debbieEventPublisher.publishEvent(testEvent);
        debbieEventPublisher.publishEvent(new TestStartedEvent(this, null));
    }

    public static void main(String[] args) {
        EventBeanTest test = new EventBeanTest();
        DebbieBeanInfo<TestStartedEvent> beanInfo = new DebbieBeanInfo<>(TestStartedEvent.class);
        beanInfo.setBean(new TestStartedEvent(test, null));
        beanInfo.setBeanType(BeanType.SINGLETON);
        beanInfo.addBeanName("testStartedEvent");
        DebbieApplicationFactory factory = DebbieApplicationFactory.configure(EventBeanTest.class);
        factory.getBeanInitialization().initSingletonBean(beanInfo);
        factory.refreshBeans();

        // todo
        DebbieApplication application = factory.postCreateApplication();
        GlobalBeanFactory globalBeanFactory = factory.getGlobalBeanFactory();
        DebbieEventPublisher eventPublisher = globalBeanFactory.factory("eventPublisher");
        eventPublisher.publishEvent(new TestStartedEvent(test, null));
        application.start(args);

        application.exit(args);
    }
}
