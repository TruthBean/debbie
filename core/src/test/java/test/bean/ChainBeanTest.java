/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package test.bean;

import com.truthbean.debbie.bean.*;
import com.truthbean.debbie.boot.DebbieApplication;
import com.truthbean.debbie.boot.DebbieBootApplication;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.event.DebbieEventPublisher;
import com.truthbean.debbie.event.EventMulticaster;
import com.truthbean.debbie.task.*;

import java.security.Permission;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.2.0
 * Created on 2020-12-12 14:54
 */
@DebbieBootApplication(scan = @DebbieScan(basePackages = "demo.raw"))
public class ChainBeanTest {
    static {
        System.setProperty("logging.level.com.truthbean.debbie", "TRACE");
    }
    static {
        SecurityManager originalSecurityManager = System.getSecurityManager();
        if (originalSecurityManager == null) {
            // 创建自己的SecurityManager
            SecurityManager sm = new SecurityManager() {
                private void check(Permission perm) {
                    String name = perm.getName();
                    String actions = perm.getActions();
                    // 禁止exec
                    if (perm instanceof java.io.FilePermission) {
                        if (actions != null && actions.contains("execute")) {
                            throw new SecurityException("execute denied!");
                        }
                    }
                    // 禁止设置新的SecurityManager，保护自己
                    if (perm instanceof java.lang.RuntimePermission) {
                        if (name != null && name.contains("setSecurityManager")) {
                            throw new SecurityException("System.setSecurityManager denied!");
                        }
                    }
                    // 反射
                    if (perm instanceof java.lang.reflect.ReflectPermission) {
                        // if (actions != null && actions.contains())
                        System.out.println("reflection .....");
                        System.out.println("actions: " + actions);
                        System.out.println("name: " + name);
                        System.out.println(perm.toString());
                    }
                }

                @Override
                public void checkPermission(Permission perm) {
                    check(perm);
                }

                @Override
                public void checkPermission(Permission perm, Object context) {
                    check(perm);
                }
            };

            System.setSecurityManager(sm);
        }
    }

    public static void main(String[] args) {
        DebbieApplication application = DebbieApplication.create(ChainBeanTest.class, args);
        ApplicationContext applicationContext = application.getContext();
        BeanInitialization beanInitialization = applicationContext.getBeanInitialization();
        String bean001Name = "bean001";
        Bean001 bean001 = new Bean001("bean001");
        BeanInfo<Bean001> beanInfo = new SimpleBeanInfo<>(bean001, BeanType.SINGLETON, bean001Name);
        beanInitialization.initBean(beanInfo);
        applicationContext.refreshBeans();

        EventMulticaster eventMulticaster = applicationContext.factory(EventMulticaster.class);
        eventMulticaster.addEventListener(new TestBeanEventListener());

        BeanInfoFactory beanInfoFactory = applicationContext.getBeanInfoFactory();
        BeanInfo<Bean001> bean001Info = beanInfoFactory.getBeanInfo(bean001Name, Bean001.class, true);
        GlobalBeanFactory globalBeanFactory = applicationContext.getGlobalBeanFactory();
        Bean001 newBean001 = globalBeanFactory.factory(bean001Name);
        System.out.println("is origin beanInfo: " + (beanInfo == bean001Info));
        System.out.println("is origin bean value: " + (bean001 == bean001Info.getBean()));
        System.out.println("is origin bean: " + (bean001 == newBean001));


        TaskRegister taskRegister = globalBeanFactory.factory(TaskRegister.class);
        TaskInfo taskInfo = new TaskInfo();
        TaskRunnable taskRunnable = () -> {
            System.out.println(Thread.currentThread().getName() + " " + Thread.currentThread().getId() + " 44567890");
        };
        taskInfo.setTaskExecutor(taskRunnable);
        DebbieTaskConfig taskConfig = new DebbieTaskConfig();
        taskConfig.setFixedRate(100);
        taskInfo.setTaskConfig(taskConfig);

        taskRegister.registerTask(taskInfo);

        DebbieEventPublisher eventPublisher = applicationContext.factory(DebbieEventPublisher.class);
        eventPublisher.publishEvent(new TestBeanEvent(new ChainBeanTest()));

        application.start(args);
        application.exit(args);
    }
}
