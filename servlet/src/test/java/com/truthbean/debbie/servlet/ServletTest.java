package com.truthbean.debbie.servlet;

import com.truthbean.debbie.test.DebbieApplicationExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({DebbieApplicationExtension.class})
public class ServletTest {

    @Test
    public void content() {
        System.out.println("nothing");
    }
}
