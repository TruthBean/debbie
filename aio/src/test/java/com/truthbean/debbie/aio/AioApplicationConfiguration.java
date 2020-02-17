package com.truthbean.debbie.aio;

import com.truthbean.debbie.bean.BeanConfiguration;
import com.truthbean.debbie.bean.DebbieBean;

import java.util.concurrent.Callable;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.0.2
 * Created on 2019-12-17 21:00
 */
@BeanConfiguration
public class AioApplicationConfiguration {

    @DebbieBean(name = "test")
    public Callable<Void> test() {
        return () -> {
            System.out.println("call nothing");
            return null;
        };
    }
}
