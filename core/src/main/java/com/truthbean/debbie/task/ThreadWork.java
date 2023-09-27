/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.task;

/**
 * @author truthbean/Rogar·Q
 * @since 0.0.2
 * Created on 2020-04-30 16:19.
 */
public class ThreadWork implements Runnable {
    private final Object[] args;

    public ThreadWork(Object... args) {
        this.args = args;
    }

    /**
     * do work in thread
     * @param args args
     */
    public void doWork(Object...args) {
        // do nothing
    }

    @Override
    public void run() {
        doWork(args);
    }
}
