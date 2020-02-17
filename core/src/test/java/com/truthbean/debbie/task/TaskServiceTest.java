package com.truthbean.debbie.task;

import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.event.TestBean;
import com.truthbean.debbie.test.DebbieApplicationExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @author truthbean/Rogar·Q
 * @since 0.0.2
 */
@ExtendWith({DebbieApplicationExtension.class})
public class TaskServiceTest {

    @BeanInject
    private TestBean bean;

    @Test
    void context() {
        System.out.println("context");
        System.out.println(bean.toString());
    }
}
