package com.truthbean.debbie.graalvm.listener;

import com.truthbean.debbie.bean.GlobalBeanFactory;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.core.ApplicationContextAware;
import com.truthbean.debbie.event.DebbieEventListener;
import com.truthbean.debbie.event.DebbieReadyEvent;
import com.truthbean.debbie.graalvm.ApplicationEntrypoint;
import com.truthbean.debbie.graalvm.service.LogService;

/**
 * @author TruthBean
 * @since 0.5.3
 * Created on 2021/11/25 21:42.
 */
public class DebbieReadyEventListener implements DebbieEventListener<DebbieReadyEvent>, ApplicationContextAware {
    @Override
    public String getName() {
        return "debbieReadyEventListener";
    }

    @Override
    public boolean async() {
        return true;
    }

    @Override
    public boolean allowConcurrent() {
        return false;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        GlobalBeanFactory globalBeanFactory = applicationContext.getGlobalBeanFactory();
        globalBeanFactory.factory(ApplicationEntrypoint.class).printId();
        Object myBean = globalBeanFactory.factory("myBean");
        System.out.println(myBean);
        MyEvent myEvent = globalBeanFactory.factory(MyEvent.class);
        System.out.println(myEvent);
    }

    @Override
    public void onEvent(DebbieReadyEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        applicationContext.getGlobalBeanFactory().factory(LogService.class).log("hahahaha");
    }

    @Override
    public boolean supportsSourceType(Class<?> sourceType) {
        return sourceType == DebbieReadyEvent.class;
    }

    @Override
    public Class<DebbieReadyEvent> getEventType() {
        return DebbieReadyEvent.class;
    }
}
