package com.truthbean.debbie.boot;

import com.truthbean.debbie.reflection.ReflectionHelper;

/**
 * @author truthbean/Rogar·Q
 * @since 0.0.2
 */
@DebbieBootApplication
class SeTestApplication {

    private SeTestApplication() {
        System.out.println(".......");
    }

    public static void main(String[] args) {
        /*DebbieApplication application = DebbieApplicationFactory.create(SimpleApplicationFactoryTest.class);

        new Thread(() -> application.start(args)).start();
        new Thread(() -> application.start(args)).start();
        application.start(args);
        new Thread(() -> application.start(args)).start();
        new Thread(() -> application.start(args)).start();
        new Thread(() -> application.start(args)).start();

        new Thread(() -> application.exit(args)).start();
        application.exit(args);
        new Thread(() -> application.exit(args)).start();*/

        SeTestApplication instance = ReflectionHelper.newInstance(SeTestApplication.class);
        System.out.println(instance);
    }
}
