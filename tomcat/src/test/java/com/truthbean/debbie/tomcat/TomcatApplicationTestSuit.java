package com.truthbean.debbie.tomcat;

import com.truthbean.debbie.boot.DebbieApplicationFactory;
import com.truthbean.debbie.boot.DebbieBootApplication;
import org.junit.jupiter.api.Test;

@DebbieBootApplication
public class TomcatApplicationTestSuit {

    @Test
    public void application() {
        DebbieApplicationFactory.create(TomcatApplicationTestSuit.class).start();
    }

    public static void main(String[] args) {
        DebbieApplicationFactory.create(TomcatApplicationTestSuit.class).start(args);
    }
}
