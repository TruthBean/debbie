package com.truthbean.debbie.undertow;

import com.truthbean.debbie.test.DebbieApplicationExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({DebbieApplicationExtension.class})
public class UndertowApplicationTest {

    @Test
    public void content() {
        System.out.println("nothing");
    }
}
