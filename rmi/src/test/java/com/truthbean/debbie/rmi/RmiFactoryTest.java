package com.truthbean.debbie.rmi;

import com.truthbean.debbie.boot.DebbieApplicationFactory;
import org.junit.jupiter.api.Test;

public class RmiFactoryTest {

    @Test
    public void test() {
        var application = DebbieApplicationFactory.factory();
        application.start();
    }

    public static void main(String[] args) {
        var application = DebbieApplicationFactory.factory();
        application.start();
    }
}
