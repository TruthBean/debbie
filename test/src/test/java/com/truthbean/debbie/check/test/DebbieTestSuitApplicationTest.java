package com.truthbean.debbie.check.test;

import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.properties.PropertyInject;
import com.truthbean.debbie.test.annotation.DebbieApplicationTest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

// @ExtendWith({DebbieApplicationExtension.class})
// @DebbieBootApplication(customInjectType = Autowired.class)
@DebbieApplicationTest(customInjectType = Autowired.class)
class DebbieTestSuitApplicationTest {

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
    void content() {
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
