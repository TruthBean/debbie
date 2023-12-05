/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.check.properties;

import com.truthbean.debbie.bean.*;
import com.truthbean.debbie.boot.DebbieApplication;
import com.truthbean.debbie.environment.Environment;
import com.truthbean.debbie.environment.EnvironmentDepository;
import com.truthbean.debbie.environment.EnvironmentDepositoryHolder;
import com.truthbean.debbie.proxy.BeanProxyType;
import com.truthbean.debbie.test.annotation.DebbieApplicationTest;
import com.truthbean.debbie.core.ApplicationContext;
import org.junit.jupiter.api.Test;

import java.util.Set;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-09-24 18:26
 */
@DebbieApplicationTest
public class PropertiesTest {

    static {
        System.setProperty("application.profiles", "test");
        // -Dtest.hehe.name=不知道
        //-Ddebbie.application.properties=application.properties;application-test.properties
    }

    @Test
    public void test(@BeanInject(profile = "test") PropertiesConfigurationTest test) {
        System.out.println(test.getHehe().getName());
    }

    @Test
    public void testEnv(@BeanInject ApplicationContext applicationContext) {
        EnvironmentDepositoryHolder environmentDepositoryHolder = applicationContext.getEnvironmentHolder();
        BeanInfoManager beanInfoManager = applicationContext.getBeanInfoManager();
        String profile = environmentDepositoryHolder.getProfile();
        System.out.println("profile: " + profile);
        Set<String> profiles = environmentDepositoryHolder.getProfiles();
        System.out.println();
        for (String s : profiles) {
            System.out.println("profile: " + s);
            Environment environment = environmentDepositoryHolder.getEnvironmentIfPresent(s);
            System.out.println("env: " + environment);
            // String p = environment.getProfile();
            // System.out.println("env profile: " + p);
        }
        System.out.println("----------------------------------------------------");
        // 自定义读取properties/yml/json等文件
        System.out.println("profile: " + EnvironmentDepository.PROPERTIES);
        Environment propesEnvironment = environmentDepositoryHolder.getEnvironmentIfPresent(EnvironmentDepository.PROPERTIES);
        System.out.println("environment: " + propesEnvironment);
        // system
        System.out.println("profile: " + EnvironmentDepository.SYSTEM);
        Environment systemEnvironment = environmentDepositoryHolder.getEnvironmentIfPresent(EnvironmentDepository.SYSTEM);
        System.out.println("environment: " + systemEnvironment);
        // jvm
        System.out.println("profile: " + EnvironmentDepository.JVM);
        Environment jvmEnvironment = environmentDepositoryHolder.getEnvironmentIfPresent(EnvironmentDepository.JVM);
        System.out.println("environment: " + jvmEnvironment);
        // env
        System.out.println("profile: " + EnvironmentDepository.ENV);
        Environment envEnvironment = environmentDepositoryHolder.getEnvironmentIfPresent(EnvironmentDepository.ENV);
        System.out.println("environment: " + envEnvironment);
        // EnvironmentSpi
        System.out.println("=========================================================");
        Environment defaultEnvironment = environmentDepositoryHolder.getEnvironment();
        System.out.println(defaultEnvironment.getValue("test.hehe.name"));
        Environment testPropsEnvironment = environmentDepositoryHolder.getEnvironmentIfPresent("application");
        System.out.println(testPropsEnvironment.getValue("test.hehe.name"));
        Environment propsEnvironment = environmentDepositoryHolder.getEnvironmentIfPresent("application");
        System.out.println(propsEnvironment.getValue("test.hehe.name"));
    }

    public static void main(String[] args) {
        /*DebbieApplication application = DebbieApplication.create(PropertiesTest.class, args)
                .then(context -> {
                    BeanInfoManager beanInfoManager = context.getBeanInfoManager();
                    BeanInfo<PropertiesConfigurationTest> beanInfo = beanInfoManager.getBeanInfo(null, PropertiesConfigurationTest.class, true);
                    if (beanInfo instanceof BeanFactory<PropertiesConfigurationTest> beanFactory) {
                        String profile = "test";
                        PropertiesConfigurationTest test = beanFactory.factory(profile, null, null, null, BeanType.SINGLETON, BeanProxyType.JDK, context);
                        System.out.println(test.getHehe().getName());
                        beanFactory.close();
                        profile = EnvironmentDepositoryHolder.ORIGIN_PROFILE;
                        test = beanFactory.factory(profile, null, null, null, BeanType.SINGLETON, BeanProxyType.JDK, context);
                        System.out.println(test.getHehe().getName());
                        beanFactory.close();
                        test = beanFactory.factory(profile, "a", null, null, BeanType.SINGLETON, BeanProxyType.JDK, context);
                        System.out.println(test.getHehe().getName());
                    }
                });*/
        // application.start();
    }
}
