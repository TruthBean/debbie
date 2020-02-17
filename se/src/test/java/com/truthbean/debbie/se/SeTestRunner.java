package com.truthbean.debbie.se;

import com.truthbean.debbie.bean.BeanComponent;
import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.task.DebbieTask;

@BeanComponent(lazy = false)
public class SeTestRunner {

    @BeanInject
    private SeTestService seTestService;

    @DebbieTask
    public void printId() {
        for (int i = 0; i < 10; i++) {
            System.out.println(seTestService.getUuid());
        }
    }
}
