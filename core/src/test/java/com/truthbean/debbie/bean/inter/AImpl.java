/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.bean.inter;

import com.truthbean.debbie.bean.BeanComponent;
import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.bean.BeanType;

import java.util.UUID;

@BeanComponent(name = "a", type = BeanType.SINGLETON)
public class AImpl implements A {

    private final String a;

    private B bBean;
    private C cBean;

    public String getA() {
        return a;
    }

    public AImpl(@BeanInject("b") B bBean, @BeanInject(name = "c") C cBean) {
        this.bBean = bBean;
        this.cBean = cBean;

        a = "A --- " + UUID.randomUUID().toString();
        System.out.println("a a a a a a a a a a a a a a a a a a a a a a a a a a a a a a ");
    }

    @Override
    public String toString() {
        return "ABean:{" + "a:\'" + a + '\'' + "," + "bBean:" + bBean + "," + "cBean:" + cBean + '}';
    }
}