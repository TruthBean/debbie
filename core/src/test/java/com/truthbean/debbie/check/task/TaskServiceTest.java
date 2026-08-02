package com.truthbean.debbie.check.task;

import com.truthbean.Console;
import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.bean.DebbieScan;
import com.truthbean.debbie.check.event.TestBean;
import com.truthbean.debbie.concurrent.ThreadPooledExecutor;
import com.truthbean.debbie.test.annotation.DebbieApplicationTest;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author truthbean/Rogar·Q
 * @since 0.0.2
 */
@DebbieApplicationTest(scan = @DebbieScan(basePackages = {"demo.raw"}))
public class TaskServiceTest {

    @BeanInject
    private TestBean bean;

    @BeanInject
    private ThreadPooledExecutor threadPooledExecutor;

    @Test
    void context() {
        Console.println("context");
        Console.println(bean.toString());
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void task() {
        threadPooledExecutor.execute(() -> {
            Console.println("66666");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Console.println("66666 done");
        });
        Future<String> submit = threadPooledExecutor.submit((args) -> {
            Console.println("1 .....................");
            try {
                Thread.sleep(6000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "hello";
        });
        try {
            String s = submit.get(5000, TimeUnit.MILLISECONDS);
            Console.println("2. " + s);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }
        Console.println("3 ...................");
        while (!submit.isDone()) {
            try {
                String s = submit.get();
                Console.println("4. " + s);
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
