/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
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

@BeanComponent(name = "b", type = BeanType.SINGLETON)
public class BImpl implements B {

    private final String b;

    private final C cBean;

    public String getB() {
        return b;
    }

    public BImpl(@BeanInject(name = "c") C cBean) {
        this.cBean = cBean;

        b = "B --- " + UUID.randomUUID().toString();
        System.out.println("b b b b b b b b b b b b b b b b b b b b b b b b b b b b b b b b b b b b b b b ");
    }

    @Override
    public String toString() {
        return "ABean:{" + "b:\'" + b + '\'' + "," + "cBean:" + cBean + '}';
    }
}