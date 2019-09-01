package com.truthbean.debbie.httpclient;

import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.test.DebbieApplicationExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @author TruthBean
 * @since 0.0.2
 * Created on 2019/09/01 22:29.
 */
@ExtendWith(DebbieApplicationExtension.class)
public class BeanTest {

    @Test
    public void test(@BeanInject UserService userService) {
        System.out.println(userService.getUserHttpClient());
        userService.login();
    }
}
