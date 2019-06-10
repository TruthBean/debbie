package com.truthbean.debbie.bean;

import com.truthbean.debbie.server.DebbieApplicationFactory;
import com.truthbean.debbie.data.transformer.DataTransformer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BeanConfigurationRegisterTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void register() {
        DebbieApplicationFactory factory = new DebbieApplicationFactory();
        factory.config();
        factory.callStarter();

        BeanFactoryHandler beanFactoryHandler = factory.getBeanFactoryHandler();
        DataTransformer<Integer, Character> bean = beanFactoryHandler.factory("dataTransformer");
        System.out.println(bean.reverse('a'));


    }
}