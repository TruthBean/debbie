package com.truthbean.debbie.jdbc.event;

import com.truthbean.debbie.boot.DebbieApplicationFactory;
import com.truthbean.debbie.boot.DebbieBootApplication;
import org.junit.jupiter.api.Test;

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
