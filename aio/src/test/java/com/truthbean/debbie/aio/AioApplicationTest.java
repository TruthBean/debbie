package com.truthbean.debbie.aio;

import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.boot.DebbieApplicationFactory;
import com.truthbean.debbie.test.DebbieApplicationExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.concurrent.Callable;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.0.2
 * Created on 2019-12-17 20:32
 */
@ExtendWith({DebbieApplicationExtension.class})
public class AioApplicationTest {

    public static void main(String[] args) {
        var application = DebbieApplicationFactory.create(AioApplicationTest.class);
        application.start(args);
        application.exit(args);
    }

    @Test
    public void content() {
        System.out.println("hello aio");
    }

    @Test
    public void test(@BeanInject(name = "test") Callable<Void> test) throws Exception {
        test.call();
    }
}
