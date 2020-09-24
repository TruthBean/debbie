/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.check.bean;

import com.truthbean.debbie.asm.AsmClassCreator;
import com.truthbean.debbie.bean.BeanInitialization;
import com.truthbean.debbie.bean.BeanType;
import com.truthbean.debbie.bean.DebbieBeanInfo;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.core.ApplicationFactory;
import com.truthbean.debbie.reflection.ReflectionHelper;
import org.junit.jupiter.api.Test;

import java.util.UUID;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-08-26 16:48
 */
public class BeanInitializationTest {

    @Test
    void init() {
        var applicationFactory = ApplicationFactory.configure(BeanInitializationTest.class);
        var context = applicationFactory.getApplicationContext();
        var initialization = context.getBeanInitialization();

        AsmClassCreator classCreator = new AsmClassCreator();

        DebbieBeanInfo beanInfo;
        for (int i = 0; i < 100_000; i++) {
            String className = classCreator.randomClassName();
            Class<?> cls = classCreator.createClass(className, "com.truthbean.debbie.check.bean",
                    AsmClassCreator.class.getClassLoader(), "com.truthbean.debbie.check.bean.EmptyBean", false);
            Object o = ReflectionHelper.newInstance(cls);
            beanInfo = new DebbieBeanInfo<>(o.getClass());
            beanInfo.setBean(o);
            beanInfo.addBeanName(UUID.randomUUID().toString().replaceAll("-", ""));
            beanInfo.setBeanType(BeanType.SINGLETON);
            initialization.initBean(beanInfo);
        }
        applicationFactory.release();
    }

    @Test
    void createClass() {
        AsmClassCreator classCreator = new AsmClassCreator();
        String className = classCreator.randomClassName();
        System.out.println(className);
        Class<?> cls = classCreator.createClass(className, "com.truthbean.debbie.check.bean",
                BeanInitializationTest.class.getClassLoader(), "com.truthbean.debbie.check.bean.EmptyBean", false);
        System.out.println(cls);
    }
}