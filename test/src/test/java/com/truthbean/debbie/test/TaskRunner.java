package com.truthbean.debbie.test;

import com.truthbean.debbie.bean.BeanComponent;
import com.truthbean.debbie.bean.BeanType;
import com.truthbean.debbie.task.DebbieTask;

/**
 * @author truthbean/Rogar·Q
 * @since 0.0.2
 */
@BeanComponent(lazy = false, type = BeanType.SINGLETON)
public class TaskRunner {

    @DebbieTask
    public void task() {
        var thread = new Thread(() -> {
            try {
                Thread.sleep(10000L);
                System.out.println("000000000000000000000");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
