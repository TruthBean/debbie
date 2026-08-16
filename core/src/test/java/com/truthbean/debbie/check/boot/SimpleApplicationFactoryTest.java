package com.truthbean.debbie.check.boot;

import com.truthbean.Console;
import com.truthbean.debbie.boot.DebbieApplication;
import com.truthbean.debbie.boot.DebbieBootApplication;
import com.truthbean.debbie.core.ApplicationFactory;
import org.junit.jupiter.api.Test;

@DebbieBootApplication
class SimpleApplicationFactoryTest {

    @Test
    void testConfig() {
        ApplicationFactory.configure(SimpleApplicationFactoryTest.class);
    }

    @Test
    void testApplication() {
        var application = DebbieApplication.create(SimpleApplicationFactoryTest.class);
        application.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Console.error("Thread.sleep error", e);
        }
        application.exit();
    }

    @Test
    void customize() {
        var mainClass = SimpleApplicationFactoryTest.class;
        ApplicationFactory.newEmpty()
                .preInit("test")
                .init(mainClass)
                .config()
                .create()
                .build()
                .factory()
                .start();
    }

    @Test
    void custom2() {
        ApplicationFactory.newEmpty()
                .preInit("test")
                .init()
                .config()
                .create()
                .build()
                .factory()
                .start();
    }

    public static void main(String[] args) {
        ApplicationFactory.newEmpty()
                .preInit(args)
                .init()
                .config()
                .create()
                .build()
                .factory()
                .start();
    }

}