package com.truthbean.debbie.se;

import com.truthbean.debbie.boot.DebbieApplicationFactory;
import com.truthbean.debbie.boot.DebbieBootApplication;

/**
 * @author truthbean/Rogar·Q
 * @since 0.0.2
 */
@DebbieBootApplication
public class SeTestApplication {

    public static void main(String[] args) {
        var application = DebbieApplicationFactory.create(NoWebApplicationFactoryTest.class);
        application.start(args);
    }
}
