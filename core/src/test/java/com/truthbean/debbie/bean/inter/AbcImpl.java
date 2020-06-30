/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.bean.inter;

import com.truthbean.debbie.bean.BeanComponent;
import com.truthbean.debbie.bean.BeanInit;
import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.bean.BeanType;

import java.util.UUID;

@BeanComponent(name = "abc", type = BeanType.SINGLETON)
public class AbcImpl implements Abc {

    private final String a;

    private final A aBean;
    private final B bBean;
    private final C cBean;

    @BeanInject
    private DemoBeanComponent demoBeanComponent;

    @BeanInit
    public static AbcImpl init(@BeanInject(name = "a") A aBean, @BeanInject(name = "b") B bBean,
                               @BeanInject(name = "c") C cBean) {
        return new AbcImpl(aBean, bBean, cBean);
    }

    public AbcImpl(@BeanInject(name = "a") A aBean, @BeanInject(name = "b") B bBean, @BeanInject(name = "c") C cBean) {
        this.aBean = aBean;
        this.bBean = bBean;
        this.cBean = cBean;

        a = "ABC --- " + UUID.randomUUID().toString();
        System.out.println("abc abc abc abc abc abc abc abc abc abc abc abc abc abc abc abc abc abc abc abc abc ");
    }

    public A getaBean() {
        return aBean;
    }

    public B getbBean() {
        return bBean;
    }

    public C getcBean() {
        return cBean;
    }

    @Override
    public String toString() {
        return "ABCBean:{" + "a:\'" + a + '\'' + "," + "aBean:" + aBean + "," + "bBean:" + bBean + ","
                + "cBean:" + cBean
                + ", demoBeanComponent:" + demoBeanComponent
                + '}';
    }
}

