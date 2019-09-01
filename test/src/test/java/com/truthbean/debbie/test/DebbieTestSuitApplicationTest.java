package com.truthbean.debbie.test;

import com.truthbean.debbie.bean.BeanInject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({DebbieApplicationExtension.class})
public class DebbieTestSuitApplicationTest {

    @Test
    public void content(@BeanInject TestSuitService testSuitService) {
        System.out.println(testSuitService.getId());
    }
}
