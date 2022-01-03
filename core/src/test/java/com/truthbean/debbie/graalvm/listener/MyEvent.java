package com.truthbean.debbie.graalvm.listener;


import com.truthbean.debbie.bean.BeanComponent;

/**
 * @author TruthBean
 * @since 0.5.3
 * Created on 2021/11/26 22:19.
 */
@BeanComponent
public class MyEvent {

    @Override
    public String toString() {
        return "MyEvent: " + super.toString();
    }
}
