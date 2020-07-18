package com.truthbean.debbie.undertow;

import com.truthbean.debbie.bean.DebbieScan;
import com.truthbean.debbie.boot.DebbieApplication;
import com.truthbean.debbie.boot.DebbieApplicationFactory;
import com.truthbean.debbie.test.DebbieApplicationTest;
import org.junit.jupiter.api.Test;

@DebbieApplicationTest(scan = @DebbieScan(basePackages = "com.truthbean"))
public class UndertowApplicationTest {

    @Test
    void content() {
        System.out.println("nothing");
    }

    public static void main(String[] args) {
        DebbieApplication application = DebbieApplicationFactory.create(UndertowApplicationTest.class);
        application.start(args);
        application.exit(args);
    }
}
