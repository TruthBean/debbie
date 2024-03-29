package com.truthbean.debbie.check.boot;

import com.truthbean.debbie.boot.DebbieApplication;
import com.truthbean.debbie.boot.DebbieBootApplication;
import com.truthbean.debbie.core.ApplicationFactory;
import com.truthbean.debbie.core.DebbieCoreModuleStarter;
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
            e.printStackTrace();
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

    @Test
    void test() {
        int a, b, c, d;
        for (a = 1; a <= 9; a++) {
            for (b = 0; b <= 9; b++) {
                for (c = 0; c <= 9; c++) {
                    for (d = 1; d <= 9; d++) {
                        int r1 = a * 1000 + b * 100 + c * 10 + d;
                        int r2 = d * 1000 + c * 100 + b * 10 + a;
                        if (r1 * 9 == r2) {
                            System.out.println("a = " + a);
                            System.out.println("b = " + b);
                            System.out.println("c = " + c);
                            System.out.println("d = " + d);
                            break;
                        }
                    }
                }
            }
        }
    }

}