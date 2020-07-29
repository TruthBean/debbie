package com.truthbean.debbie.netty;

import com.truthbean.debbie.boot.DebbieApplication;
import com.truthbean.debbie.internal.DebbieApplicationFactory;
import com.truthbean.debbie.test.annotation.DebbieApplicationTest;
import org.junit.jupiter.api.Test;

@DebbieApplicationTest
public class NettyApplicationTest {

    @Test
    void content() {
        System.out.println("nothing");
    }

    public static void main(String[] args) {
        DebbieApplication application = DebbieApplicationFactory.create(NettyApplicationTest.class);
        application.start(args);
        application.exit(args);
    }
}
