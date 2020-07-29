package com.truthbean.debbie.tomcat;

import com.truthbean.debbie.boot.DebbieApplication;
import com.truthbean.debbie.internal.DebbieApplicationFactory;
import com.truthbean.debbie.boot.DebbieBootApplication;
import org.junit.jupiter.api.Test;

@DebbieBootApplication
public class TomcatApplicationTestSuit {

    @Test
    public void application() throws InterruptedException {
        DebbieApplication debbieApplication = DebbieApplicationFactory.create(TomcatApplicationTestSuit.class);
        debbieApplication.start();
        Thread.sleep(3000);
        debbieApplication.exit();
    }

    public static void main(String[] args) {
        DebbieApplicationFactory.create(TomcatApplicationTestSuit.class).start(args);
    }
}
