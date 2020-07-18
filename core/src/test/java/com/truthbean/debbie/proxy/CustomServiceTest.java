package com.truthbean.debbie.proxy;

import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.proxy.bean.TargetObject;
import com.truthbean.debbie.test.DebbieApplicationTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DebbieApplicationTest
class CustomServiceTest {

    @BeanInject
    private CustomService customService;

    @BeanInject
    private TargetObject targetObject;

    @Test
    void hello() {
        String hello = customService.hello();
        System.out.println(hello);
    }

    @Test
    void testProxy() {
        try {
            targetObject.code("java");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testAsmProxy() {
        try {
            targetObject.code("java", 1, 333.5F);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}