/**
 * Copyright (c) 2024 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package test;

import com.truthbean.debbie.concurrent.PooledExecutor;
import com.truthbean.debbie.concurrent.ThreadPooledExecutor;
import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author TruthBean/Rogar·Q
 * @since 1.0.0
 * Created on 2020-12-21 09:28
 */
public class ThreadTest {
    public static class ThreadOne {

        public void runOne() {
            synchronized (this) {
                System.out.println("1");
                System.out.println("11");
                System.out.println("111");
                System.out.println("1111");
                System.out.println("11111");
            }
        }
    }

    public static class ThreadTwo {

        public void runTwo() {
            synchronized (this) {
                System.out.println("2");
                System.out.println("22");
                System.out.println("222");
                System.out.println("2222");
                System.out.println("22222");
            }
        }
    }

    public static void main(String[] args) {
        final ThreadOne one = new ThreadOne();
        final ThreadTwo two = new ThreadTwo();
        PooledExecutor executor = new ThreadPooledExecutor();
        List<Callable<Void>> callables = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            callables.add(() -> {
                new ThreadOne().runOne();
                return null;
            });
            callables.add(() -> {
                new ThreadTwo().runTwo();
                return null;
            });
        }
        executor.invokeAll(callables, 1000L);
        executor.destroy();
    }

    @Test
    public void testFork() {
        int num = 100 ;
        int processors = Runtime.getRuntime().availableProcessors();
        long begin = System.currentTimeMillis();
        ForkJoinPool pool = new ForkJoinPool();
        IntSum task = new IntSum(num);
        long sum = pool.invoke(task);
        long end = System.currentTimeMillis();
        System.out.println("Sum is " + sum);
        System.out.println(end - begin);

        System.out.println("------------------");

        /*begin = System.currentTimeMillis();
        long result = 0;
        for (int i = 0; i < num; i++) {
            result += task.getRandomInteger();
        }
        end = System.currentTimeMillis();
        System.out.println(result);
        System.out.println(end - begin);*/
    }

    class IntSum extends RecursiveTask<Long> {
        private final int count;

        public IntSum(int count) {
            this.count = count;
        }

        @Override
        protected Long compute() {
            long result = 0;

            if (this.count <= 0) {
                return 0L;
            } else if (this.count == 1) {
                return (long) this.getRandomInteger();
            }
            List<RecursiveTask<Long>> forks = new ArrayList<>();
            for (int i = 0; i < this.count; i++) {
                IntSum subTask = new IntSum(1);
                subTask.fork(); // Launch the subtask
                forks.add(subTask);
            }
            // all subtasks finish and combine the result
            for (RecursiveTask<Long> subTask : forks) {
                result = result + subTask.join();
            }
            return result;
        }

        public int getRandomInteger() {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // int ran = ThreadLocalRandom.current().nextInt();
            /*int ran = 0;
            try {
                ran = SecureRandom.getInstanceStrong().nextInt();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }*/
            int ran = new SecureRandom().nextInt();
            System.out.println(Thread.currentThread().getId() + " : " + ran);
            return ran;
        }
    }


}
