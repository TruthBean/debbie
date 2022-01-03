package com.truthbean.debbie.check.core;

import com.truthbean.debbie.boot.DebbieApplication;
import com.truthbean.debbie.boot.DebbieBootApplication;
import com.truthbean.debbie.reflection.ReflectionConfigurer;

/**
 * @author TruthBean
 * @since 0.5.3
 */
@DebbieBootApplication
public class DebbieApplicationTest {

    static {
        System.setProperty(DebbieApplication.DISABLE_DEBBIE, "false");
        System.setProperty(ReflectionConfigurer.ENABLE_KEY, "false");
    }

    public static void main(String[] args) {
        DebbieApplication.run(new DebbieApplicationTest(), args);
    }
}
