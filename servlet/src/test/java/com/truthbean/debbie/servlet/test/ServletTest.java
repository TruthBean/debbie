package com.truthbean.debbie.servlet.test;

import com.truthbean.debbie.test.annotation.DebbieApplicationTest;
import org.junit.jupiter.api.Test;

@DebbieApplicationTest
class ServletTest {

    @Test
    void content() {
        System.out.println("nothing");
    }
}
