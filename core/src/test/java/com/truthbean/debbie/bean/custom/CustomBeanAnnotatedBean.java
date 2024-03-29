/**
 * Copyright (c) 2024 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.bean.custom;

import com.truthbean.debbie.bean.BeanComponent;
import com.truthbean.debbie.bean.BeanType;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-07-03 17:10.
 */
// @BeanComponent(type = BeanType.SINGLETON, conditions = TestCondition.class)
@CustomBeanAnnotation(value = "customize-bean", conditions = TestCondition.class)
public class CustomBeanAnnotatedBean {

    private final int a;
    public CustomBeanAnnotatedBean() {
        System.out.println("customBeanAnnotatedBean constructor...");
        a = 666;
    }

    public int getA() {
        return a;
    }
}
