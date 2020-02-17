package com.truthbean.debbie.jdbc.event;

import com.truthbean.debbie.bean.BeanInit;
import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.boot.DebbieApplicationFactory;
import com.truthbean.debbie.boot.DebbieBootApplication;
import com.truthbean.debbie.test.DebbieApplicationExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @author truthbean/Rogar·Q
 * @since 0.0.2
 * Created on 2020-01-19 18:45.
 */
// @ExtendWith({DebbieApplicationExtension.class})
@DebbieBootApplication
public class TransactionTest {
    public static void main(String[] args) {
        var application = DebbieApplicationFactory.create(TransactionTest.class);
        application.start(args);
    }

    @Test
    void test() {

    }
}
