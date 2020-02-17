package com.truthbean.debbie.se;

import com.truthbean.debbie.boot.DebbieApplicationFactory;
import com.truthbean.debbie.boot.DebbieBootApplication;
import org.junit.jupiter.api.Test;

@DebbieBootApplication
class NoWebApplicationFactoryTest {

    @Test
    public void testConfig() {
        DebbieApplicationFactory beanFactoryHandler = new DebbieApplicationFactory(NoWebApplicationFactoryTest.class);
        beanFactoryHandler.config();
        beanFactoryHandler.callStarter();
    }

    @Test
    public void testApplication() {
        var application = DebbieApplicationFactory.create(NoWebApplicationFactoryTest.class);
        application.start();
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