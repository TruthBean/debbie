package com.truthbean.debbie.check.event;

import com.truthbean.Console;
import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.event.DebbieEventListener;
import com.truthbean.debbie.event.EventBeanListener;

@EventBeanListener
public class Test2EventListener implements DebbieEventListener<Test2Event> {

    @BeanInject
    private TestBean testBean;

    @Override
    public void onEvent(Test2Event event) {
        Console.println(Test2EventListener.class + " do event.....");
        Console.println(event.getEvent() + 1);
        Console.println(testBean);
        throw new RuntimeException("test error....");
    }
}
