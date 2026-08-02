package com.truthbean.debbie.check.test;

import com.truthbean.Console;
import com.truthbean.debbie.bean.BeanComponent;
import com.truthbean.debbie.bean.BeanType;
import com.truthbean.debbie.task.DebbieTask;

/**
 * @author truthbean/Rogar·Q
 * @since 0.0.2
 */
@BeanComponent(lazy = false, type = BeanType.SINGLETON)
public class TaskRunner {

    @DebbieTask(async = true)
    public void task() {
        var thread = new Thread(() -> {
            try {
                Thread.sleep(10000L);
                Console.println("000000000000000000000");
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
