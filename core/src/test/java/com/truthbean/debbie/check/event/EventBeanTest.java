package com.truthbean.debbie.check.event;

import com.truthbean.debbie.bean.*;
import com.truthbean.debbie.boot.DebbieApplication;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.core.ApplicationFactory;
import com.truthbean.debbie.event.DebbieEventPublisher;
// import com.truthbean.debbie.test.annotation.DebbieApplicationTest;
import com.truthbean.debbie.test.annotation.DebbieApplicationTest;
import org.junit.jupiter.api.Test;

@DebbieApplicationTest(scan = @DebbieScan(basePackages = "com.truthbean.debbie.check.event"))
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
                                  @BeanInject(value = "test", require = false) TestEvent testEvent) {
        debbieEventPublisher.publishEvent(testEvent);
        debbieEventPublisher.publishEvent(new TestStartedEvent(this, null));
    }

    public static void main(String[] args) {
        EventBeanTest test = new EventBeanTest();
        var beanInfo = new SimpleMutableBeanFactory<>(TestStartedEvent.class);
        beanInfo.setBean(new TestStartedEvent(test, null));
        beanInfo.setBeanType(BeanType.SINGLETON);
        beanInfo.addBeanName("testStartedEvent");
        ApplicationFactory factory = ApplicationFactory.configure(EventBeanTest.class, args);
        ApplicationContext context = factory.getApplicationContext();
        context.getBeanInfoManager().register(beanInfo);

        // todo
        GlobalBeanFactory globalBeanFactory = context.getGlobalBeanFactory();
        DebbieEventPublisher eventPublisher = globalBeanFactory.factory("eventPublisher");
        DebbieApplication application = factory.create().postCreate().factory();
        application.start();
        eventPublisher.publishEvent(new TestStartedEvent(test, null));

        application.exit();
    }
}
