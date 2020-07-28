package com.truthbean.debbie.rmi;

import com.truthbean.debbie.boot.DebbieApplicationFactory;
import org.junit.jupiter.api.Test;

public class RmiFactoryTest {

    @Test
    public void test() throws InterruptedException {
        var application = DebbieApplicationFactory.create(RmiFactoryTest.class);
        application.start();
        Thread.sleep(1000);
        application.exit();
    }

    public static void main(String[] args) {
        var application = DebbieApplicationFactory.create(RmiFactoryTest.class);
        application.start();
    }
}
