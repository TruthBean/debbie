package com.truthbean.debbie.netty.test;

import com.truthbean.debbie.boot.DebbieApplication;
import com.truthbean.debbie.io.MediaType;
import com.truthbean.debbie.mvc.request.BodyParameter;
import com.truthbean.debbie.mvc.router.GetRouter;
import com.truthbean.debbie.mvc.router.RestRouter;
import com.truthbean.debbie.test.annotation.DebbieApplicationTest;
import org.junit.jupiter.api.Test;

@DebbieApplicationTest
@RestRouter
public class NettyApplicationTest {

    @Test
    void content() {
        System.out.println("nothing");
    }

    @GetRouter(value = "/hello", responseType = MediaType.TEXT_ANY_UTF8)
    public String hello(@BodyParameter(type = MediaType.TEXT_ANY_UTF8) String body) {
        return body;
    }

    public static void main(String[] args) {
        DebbieApplication application = DebbieApplication.create(NettyApplicationTest.class, args);
        application.start();
        application.exit();
    }
}
