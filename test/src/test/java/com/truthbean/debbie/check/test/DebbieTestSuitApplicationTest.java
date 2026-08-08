package com.truthbean.debbie.check.test;

import com.truthbean.Console;
import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.properties.PropertyInject;
import com.truthbean.debbie.test.annotation.DebbieApplicationTest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

// @ExtendWith({DebbieApplicationExtension.class})
// @DebbieBootApplication(customInjectType = Autowired.class)
@DebbieApplicationTest(customInjectType = Autowired.class,
        properties = {"test.test.test=hahahahaha", "debbie.task.enable=false"}
)
class DebbieTestSuitApplicationTest {

    @BeanInject
    private ApplicationContext applicationContext;

    @PropertyInject("test.test.test")
    private String test;

    private TestSuitService testSuitService;

    @Autowired
    public void setTestSuitService(TestSuitService testSuitService) {
        this.testSuitService = testSuitService;
    }

    // @BeforeAll
    static void beforeAll() {
        Console.println("..................");
    }

    @BeforeEach
    void beforeEach() {
        Console.println("before each ...");
        Console.println(System.getProperty("test.test.test"));
        Console.println("${test.test.test}: " + test);
        Console.println("${test.test.test} from test: " + applicationContext.getEnvironmentHolder().getAllProperties("test").getProperty("test.test.test"));
    }

    @Test
    void content() {
        Console.println("hello junit5");
        Console.println(testSuitService.getId());
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            Console.error("", e);
        }
        Console.info("wait task");
    }

    @Test
    void testSuitService(@BeanInject TestSuitService testSuitService, @PropertyInject("hello.test") String hello,
                         @Autowired TestSuitService service) {
        Console.println(testSuitService.getId());
        Console.println(hello);
        Console.println(service);
    }

    @AfterEach
    void afterEach() {
        Console.println("after each ...");
    }

    // @AfterAll
    static void afterAll() {
        Console.println("-------------------");
    }
}
