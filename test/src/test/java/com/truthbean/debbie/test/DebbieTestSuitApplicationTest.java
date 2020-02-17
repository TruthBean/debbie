package com.truthbean.debbie.test;

import com.truthbean.debbie.bean.BeanInject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({DebbieApplicationExtension.class})
public class DebbieTestSuitApplicationTest {

    @BeanInject
    private TestSuitService testSuitService;

    @BeforeAll
    static void beforeAll() {
        System.out.println("..................");
    }

    @Test
    public void content() {
        System.out.println("hello junit5");
        System.out.println(testSuitService.getId());
    }

    @Test
    void testSuitService(@BeanInject TestSuitService testSuitService) {
        System.out.println(testSuitService.getId());
    }

    @AfterAll
    static void afterAll() {
        System.out.println("-------------------");
    }
}
