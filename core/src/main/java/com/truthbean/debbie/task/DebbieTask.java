/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.task;

import java.lang.annotation.*;

/**
 * 标记一个类或方法为定时任务，支持固定频率和 cron 表达式两种调度方式。
 * <p>
 * <b>使用方式:</b>
 * <pre>
 * {@code
 * @DebbieTask(fixedRate = 5000)
 * public void runEvery5Seconds() { ... }
 *
 * @DebbieTask(cron = "0 0 12 * * ?")
 * public void runAtNoon() { ... }
 * }
 * </pre>
 * <p>
 * 调度优先级（互斥）：cron > fixedRate > 一次性执行（仅 initialDelay）。
 *
 * @author TruthBean
 * @since 0.0.2
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DebbieTask {

    /**
     * 表示未设置固定频率的常量值。
     * 用于区分 {@link #fixedRate()} 未设置（-1）和显式设为无限大（-2）。
     */
    long NO_RATE = -2;

    /**
     * 多个任务同时触发时的执行优先级。
     * <p>
     * 数值越小优先级越高，默认 {@link Integer#MAX_VALUE}（最低优先级）。
     *
     * @return 优先级顺序
     */
    int order() default Integer.MAX_VALUE;

    /**
     * 是否异步执行。
     * <p>
     * 为 {@code true} 时，任务在线程池中异步执行，不会阻塞其他任务；
     * 为 {@code false} 时，任务在调度线程中同步执行。
     *
     * @return 是否异步执行，默认 {@code true}
     */
    boolean async() default true;

    /**
     * 固定执行频率（毫秒）。
     * <p>
     * 设定后，任务将以固定的时间间隔重复执行。
     * 如果 {@link #initialDelay()} 未设置，则首次执行立即触发。
     * 与 {@link #cron()} 互斥，cron 优先级更高。
     *
     * @return 执行间隔（毫秒），默认 -1 表示未设置
     */
    long fixedRate() default -1;

    /**
     * 首次执行前的延迟时间（毫秒）。
     * <p>
     * 与 {@link #fixedRate()} 配合使用时，表示首次执行前的延迟，之后按 fixedRate 间隔执行。
     * 与 {@link #cron()} 配合使用时，表示首次 cron 匹配前的额外延迟。
     * 单独使用时，表示一次性延迟执行。
     *
     * @return 延迟时间（毫秒），默认 -1 表示无延迟
     */
    long initialDelay() default -1;

    /**
     * cron 表达式，用于更灵活的任务调度。
     * <p>
     * 支持 6 字段和 7 字段格式：
     * <ul>
     *   <li>6 字段: {@code second minute hour dayOfMonth month dayOfWeek}</li>
     *   <li>7 字段: {@code second minute hour dayOfMonth month dayOfWeek year}</li>
     * </ul>
     * 与 {@link #fixedRate()} 互斥，cron 优先级更高。
     * 详情参考 {@link CronExpression}。
     *
     * @return cron 表达式，默认空字符串表示未设置
     * @see CronExpression
     */
    String cron() default "";
}
