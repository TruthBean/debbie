package com.truthbean.debbie.netty;

import com.truthbean.debbie.boot.DebbieApplication;
import com.truthbean.debbie.boot.DebbieApplicationFactory;
import com.truthbean.debbie.test.DebbieApplicationExtension;
import com.truthbean.debbie.test.DebbieApplicationTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

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
