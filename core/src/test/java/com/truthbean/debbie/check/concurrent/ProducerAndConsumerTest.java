/**
 * Copyright (c) 2022 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.check.concurrent;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.0
 * Created on 2021-01-31 08:51
 */
public class ProducerAndConsumerTest {

    private static final Deque<String> DEQUE = new LinkedBlockingDeque<>();

    public static void main(String[] args) {
        List<Producer> producers = new ArrayList<>();
        List<Consumer> consumers = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Producer producer = new Producer();
            new Thread(producer).start();
            producers.add(producer);
            Consumer consumer = new Consumer(String.valueOf(i));
            new Thread(consumer).start();
            consumers.add(consumer);
        }

        long current = System.currentTimeMillis();
        for (long i = 0L; i < current + 10000L; i = System.currentTimeMillis());

        for (int i = 0; i < 10; i++) {
            Producer producer = producers.get(i);
            Consumer consumer = consumers.get(i);
            producer.stop();
            consumer.stop();
        }
    }

    public static class Producer implements Runnable {

        private final AtomicBoolean stop = new AtomicBoolean(false);

        public void stop() {
            stop.set(true);
        }

        @Override
        public void run() {
            while (!stop.get()) {
                LocalDateTime localDateTime = LocalDateTime.now();
                int year = localDateTime.getYear();
                Month month = localDateTime.getMonth();
                int dayOfMonth = localDateTime.getDayOfMonth();
                int hour = localDateTime.getHour();
                int minute = localDateTime.getMinute();
                int second = localDateTime.getSecond();
                int nano = localDateTime.getNano();
                String time = year + "年" + month.getValue() + "月" + dayOfMonth + "日" + hour + "点" + minute + "分" + second + "秒" + nano + "纳秒";
                DEQUE.addFirst(time);
            }
        }
    }

    public static class Consumer implements Runnable {
        private final AtomicBoolean stop = new AtomicBoolean(false);
        private final String sequence;

        public Consumer(String sequence) {
            this.sequence = sequence;
        }

        public void stop() {
            stop.set(true);
        }

        @Override
        public void run() {
            while (!stop.get()) {
                System.out.println("consumer " + sequence + ": " + DEQUE.pollFirst());
            }
        }
    }
}
