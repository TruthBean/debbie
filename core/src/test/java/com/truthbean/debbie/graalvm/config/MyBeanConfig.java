package com.truthbean.debbie.graalvm.config;

import com.truthbean.debbie.bean.BeanConfiguration;
import com.truthbean.debbie.bean.DebbieBean;

/**
 * @author TruthBean
 * @since 0.5.3
 * Created on 2021/11/26 22:03.
 */
@BeanConfiguration
public class MyBeanConfig {

    @DebbieBean
    public Object myBean() {
        return "myBean";
    }
}
