package com.truthbean.debbie.httpclient;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.boot.DebbieApplicationFactory;
import org.junit.jupiter.api.Test;

class UserServiceTest {

    private static UserService userService;

    static {
        DebbieApplicationFactory factory = new DebbieApplicationFactory(UserServiceTest.class);
        factory.config();
        factory.callStarter();

        BeanFactoryHandler beanFactoryHandler = factory.getBeanFactoryHandler();
        userService = beanFactoryHandler.factory(UserService.class);
    }

    @Test
    void getUserHttpClient() {
        System.out.println(userService.getUserHttpClient());
    }

    @Test
    void login() {
        userService.login();
    }
}