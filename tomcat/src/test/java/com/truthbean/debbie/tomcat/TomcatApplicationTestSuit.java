package com.truthbean.debbie.tomcat;

import com.truthbean.debbie.boot.DebbieApplicationFactory;
import org.junit.jupiter.api.Test;

public class TomcatApplicationTestSuit {

    @Test
    public void application() {
        DebbieApplicationFactory.factory().start();
    }
}
