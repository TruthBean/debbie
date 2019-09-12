package com.truthbean.debbie.netty;

import com.truthbean.debbie.boot.DebbieApplicationFactory;
import com.truthbean.debbie.test.DebbieApplicationExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({DebbieApplicationExtension.class})
public class NettyApplicationTest {

    @Test
    public void content() {
        System.out.println("nothing");
    }

    public static void main(String[] args) {
        DebbieApplicationFactory.factory().start();
    }
}
