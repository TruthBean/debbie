/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * @author TruthBean/Rogar·Q
 * @since 1.0.0
 * Created on 2020-12-21 09:28
 */
public class ThreadTest {
    public static class ThreadOne {
        private final Boolean value = Boolean.FALSE;

        public void runOne() {
            synchronized (value) {
                System.out.println("1");
                System.out.println("11");
                System.out.println("111");
                System.out.println("1111");
                System.out.println("11111");
            }
        }
    }

    public static class ThreadTwo {
        private final Boolean value = Boolean.FALSE;

        public void runTwo() {
            synchronized (value) {
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
}
