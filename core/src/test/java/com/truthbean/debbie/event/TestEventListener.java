package com.truthbean.debbie.event;

@EventBeanListener
public class TestEventListener implements GenericEventListener<TestEvent> {

    @Override
    public void onEvent(TestEvent event) {
        System.out.println(TestEventListener.class + " do event.....");
        System.out.println(event.getEvent());
    }

    @Override
    public boolean supportsSourceType(Class<?> sourceType) {
        return sourceType == TestEvent.class;
    }
}
