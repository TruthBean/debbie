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
import com.truthbean.debbie.bean.BeanInit;
import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.bean.BeanType;

import java.util.UUID;

@BeanComponent(name = "c", type = BeanType.SINGLETON)
public class CImpl implements C {

    private final String c;

    /*@BeanInject(name = "a")
    private A aBean;

    @BeanInject(name = "b")
    private B bBean;*/

    /*@BeanInit
    public static void init(@BeanInject(name = "a") A aBean, @BeanInject(name = "b") B bBean) {
        CImpl c = new CImpl();
        c.aBean = aBean;
        c.bBean = bBean;
    }*/


    public String getC() {
        return c;
    }

    public CImpl() {
        c = "C --- " + UUID.randomUUID().toString();
        System.out.println("c c c c c c c c c c c c c c c c c c c c c c c c c c c c c c c c c c c ");
    }

    @Override
    public String toString() {
        /*String a = null, b = null;
        if (aBean != null) {
            a = aBean.getA();
        }
        if (bBean != null) {
            b = bBean.getB();
        }
        return "{" +
                "\"c\":\"" + c + '\"' +
                ",\"aBean\":" + a +
                ",\"bBean\":" + b +
                '}';*/
        return "{\"c\":\"" + c + "\"}";
    }
}