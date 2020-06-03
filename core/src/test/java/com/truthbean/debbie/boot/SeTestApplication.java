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
        /*DebbieApplication application = DebbieApplicationFactory.create(SeTestApplication.class);

        new Thread(() -> application.start(args)).start();*/

        Package pkg = SeTestApplication.class.getPackage();
        String result = pkg != null ? pkg.getImplementationVersion() : null;
        System.out.println(result);

        /* new Thread(() -> application.start(args)).start();
        application.start(args);*/
        /*new Thread(() -> application.start(args)).start();
        new Thread(() -> application.start(args)).start();
        new Thread(() -> application.start(args)).start();

        new Thread(() -> application.exit(args)).start();
        application.exit(args);
        new Thread(() -> application.exit(args)).start();*/

        /*try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        application.exit(args);*/
        /*new Thread(() -> application.exit(args)).start();*/
    }
}
