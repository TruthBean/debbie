/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package test.bean;

import com.truthbean.Console;
import com.truthbean.debbie.bean.*;
import com.truthbean.debbie.boot.DebbieApplication;
import com.truthbean.debbie.boot.DebbieBootApplication;
import com.truthbean.debbie.event.DebbieEventPublisher;
import com.truthbean.debbie.event.EventMulticaster;
import com.truthbean.debbie.proxy.BeanProxyType;
import com.truthbean.debbie.task.DebbieTaskConfig;
import com.truthbean.debbie.task.TaskInfo;
import com.truthbean.debbie.task.TaskRegister;
import com.truthbean.debbie.task.TaskRunnable;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.2.0
 * Created on 2020-12-12 14:54
 */
@DebbieBootApplication(scan = @DebbieScan(basePackages = "demo.raw"))
public class ChainBeanTest {
    static {
        System.setProperty("logging.level.root", "TRACE");
        System.setProperty("logging.level.com.truthbean.debbie", "TRACE");
    }

    public static void main(String[] args) {
        DebbieApplication.create(ChainBeanTest.class, args)
                .then(applicationContext -> {
                    BeanInfoManager beanInitialization = applicationContext.getBeanInfoManager();
                    String bean001Name = "bean001";
                    Bean001 bean001 = new Bean001("bean001");
                    BeanFactory<Bean001> beanInfo = new SimpleBeanFactory<>(bean001, BeanType.SINGLETON, BeanProxyType.NO, bean001Name);
                    beanInitialization.registerBeanInfo(beanInfo);

                    EventMulticaster eventMulticaster = applicationContext.getGlobalBeanFactory().factory(EventMulticaster.class);
                    eventMulticaster.addEventListener(new TestBeanEventListener());

                    BeanInfoManager beanInfoManager = applicationContext.getBeanInfoManager();
                    BeanInfo bean001Info = beanInfoManager.getBeanInfo(bean001Name, Bean001.class, true);
                    GlobalBeanFactory globalBeanFactory = applicationContext.getGlobalBeanFactory();
                    Bean001 newBean001 = globalBeanFactory.factory(bean001Name);
                    Console.println("is origin beanInfo: " + (beanInfo == bean001Info));
                    // Console.println("is origin bean VALUE: " + (bean001 == bean001Info.getBean()));
                    Console.println("is origin bean: " + (bean001 == newBean001));

                    TaskRegister taskRegister = globalBeanFactory.factory(TaskRegister.class);
                    TaskInfo taskInfo = new TaskInfo();
                    TaskRunnable taskRunnable = (applicationContext1) -> {
                        Console.println(Thread.currentThread().getName() + " " + Thread.currentThread().getId() + " " + System.currentTimeMillis());
                    };
                    taskInfo.setTaskExecutor(taskRunnable);
                    DebbieTaskConfig taskConfig = new DebbieTaskConfig();
                    taskConfig.setFixedRate(1000);
                    taskInfo.setTaskConfig(taskConfig);

                    taskRegister.registerTask(taskInfo);

                    DebbieEventPublisher eventPublisher = applicationContext.getGlobalBeanFactory().factory(DebbieEventPublisher.class);
                    eventPublisher.publishEvent(new TestBeanEvent(new ChainBeanTest()));
                })
                .start()
                .afterStarted(applicationBootContext -> {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                })
                .exit();
    }
}
