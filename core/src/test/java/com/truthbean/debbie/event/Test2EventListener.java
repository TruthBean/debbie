package com.truthbean.debbie.event;

import com.truthbean.debbie.bean.BeanInject;

@EventBeanListener
public class Test2EventListener implements GenericEventListener<Test2Event> {

    @BeanInject
    private TestBean testBean;

    @Override
    public void onEvent(Test2Event event) {
        System.out.println(Test2EventListener.class + " do event.....");
        System.out.println(event.getEvent() + 1);
        System.out.println(testBean);
    }

    @Override
    public boolean supportsSourceType(Class<?> sourceType) {
        return sourceType == Test2Event.class;
    }
}
