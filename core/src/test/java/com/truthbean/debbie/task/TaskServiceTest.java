package com.truthbean.debbie.task;

import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.event.TestBean;
import com.truthbean.debbie.test.DebbieApplicationExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author truthbean/Rogar·Q
 * @since 0.0.2
 */
@ExtendWith({DebbieApplicationExtension.class})
public class TaskServiceTest {

    @BeanInject
    private TestBean bean;

    @BeanInject
    private ThreadPooledExecutor threadPooledExecutor;

    @Test
    void context() {
        System.out.println("context");
        System.out.println(bean.toString());
    }

    @Test
    void task() {
        threadPooledExecutor.execute(() -> {
            System.out.println("66666");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("66666 done");
        });
        Future<String> submit = threadPooledExecutor.submit((args) -> {
            System.out.println("1 .....................");
            try {
                Thread.sleep(6000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "hello";
        });
        try {
            String s = submit.get(5000, TimeUnit.MILLISECONDS);
            System.out.println("2. " + s);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        System.out.println("3 ...................");
        while (!submit.isDone()) {
            try {
                String s = submit.get();
                System.out.println("4. " + s);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } finally {
                break;
            }
        }
        threadPooledExecutor.destroy();
    }
}
