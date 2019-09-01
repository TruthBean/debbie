package com.truthbean.debbie.se;

import com.truthbean.debbie.boot.DebbieApplicationFactory;
import org.junit.jupiter.api.Test;

class NoWebApplicationFactoryTest {

    @Test
    public void testConfig() {
        DebbieApplicationFactory beanFactoryHandler = new DebbieApplicationFactory();
        beanFactoryHandler.config();
        beanFactoryHandler.callStarter();
    }

    @Test
    public void testApplication() {
        var application = DebbieApplicationFactory.factory();
        application.start();
    }
}