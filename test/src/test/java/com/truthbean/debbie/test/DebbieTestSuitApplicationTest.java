package com.truthbean.debbie.test;

import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.boot.DebbieBootApplication;
import com.truthbean.debbie.properties.PropertyInject;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;

// @ExtendWith({DebbieApplicationExtension.class})
// @DebbieBootApplication(customInjectType = Autowired.class)
@DebbieApplicationTest(customInjectType = Autowired.class)
public class DebbieTestSuitApplicationTest {

    @Autowired
    private TestSuitService testSuitService;

    // @BeforeAll
    static void beforeAll() {
        System.out.println("..................");
    }

    @BeforeEach
    void beforeEach() {
        System.out.println("before each ...");
    }

    @Test
    public void content() {
        System.out.println("hello junit5");
        System.out.println(testSuitService.getId());
    }

    @Test
    void testSuitService(@BeanInject TestSuitService testSuitService, @PropertyInject("hello.test") String hello,
                         @Autowired TestSuitService service) {
        System.out.println(testSuitService.getId());
        System.out.println(hello);
        System.out.println(service);
    }

    @AfterEach
    void afterEach() {
        System.out.println("after each ...");
    }

    // @AfterAll
    static void afterAll() {
        System.out.println("-------------------");
    }
}
