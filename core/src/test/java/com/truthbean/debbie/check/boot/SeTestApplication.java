package com.truthbean.debbie.check.boot;


import com.truthbean.debbie.boot.DebbieApplication;
import com.truthbean.debbie.boot.DebbieBootApplication;

/**
 * @author truthbean/Rogar·Q
 * @since 0.0.2
 */
@DebbieBootApplication
public class SeTestApplication {

    public SeTestApplication() {
        System.out.println(".......");
    }

    public static void main(String[] args) {
        DebbieApplication application = DebbieApplication.create(SeTestApplication.class, args);

        new Thread(application::start).start();

        Package pkg = SeTestApplication.class.getPackage();
        String result = pkg != null ? pkg.getImplementationVersion() : null;
        System.out.println(result);

        new Thread(application::start).start();
        application.start();
        new Thread(application::start).start();
        new Thread(application::start).start();
        new Thread(application::start).start();

        new Thread(application::exit).start();
        application.exit();
        new Thread(application::exit).start();

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        application.exit();
        new Thread(application::exit).start();
    }
}
