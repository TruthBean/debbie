package com.truthbean.debbie.check.core;

import com.truthbean.debbie.boot.DebbieApplication;
import com.truthbean.debbie.boot.DebbieBootApplication;

/**
 * @author TruthBean
 * @since 0.5.3
 */
@DebbieBootApplication
public class DebbieApplicationTest {

    static {
        System.setProperty(DebbieApplication.DISABLE_DEBBIE, "true");
    }

    public static void main(String[] args) {
        DebbieApplication.run(DebbieApplicationTest.class, args);
    }
}
