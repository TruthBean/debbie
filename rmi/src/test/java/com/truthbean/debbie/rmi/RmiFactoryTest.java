package com.truthbean.debbie.rmi;

import com.truthbean.debbie.boot.DebbieApplication;
import org.junit.jupiter.api.Test;

public class RmiFactoryTest {

    @Test
    public void test() throws InterruptedException {
        var application = DebbieApplication.create(RmiFactoryTest.class);
        application.start();
        Thread.sleep(1000);
        application.exit();
    }

    public static void main(String[] args) {
        var application = DebbieApplication.create(RmiFactoryTest.class, args);
        application.start();
    }
}
