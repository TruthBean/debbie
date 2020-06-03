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
import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.bean.BeanType;
import com.truthbean.debbie.proxy.MethodProxy;

import java.util.Random;
import java.util.UUID;

@BeanComponent(type = BeanType.NO_LIMIT)
public class DemoBeanComponent {

    private final String uuid;

    private final Demo2 demo1;
    private final Demo2 demo2;

    public DemoBeanComponent(Demo2 demo1, Demo2 demo2) {
        uuid = UUID.randomUUID().toString();
        this.demo2 = demo2;
        this.demo1 = demo1;
    }

    @MethodProxy(order = 31)
    public String getUuid() {
        return uuid;
    }

    public Demo2 getDemo2() {
        return demo2;
    }

    public Demo2 getDemo1() {
        return demo1;
    }

    @BeanComponent(name = "demo", type = BeanType.NO_LIMIT)
    public static class Demo2 {
        private byte id;

        public Demo2() {
        }

        @MethodProxy(order = 32)
        public byte getUuid() {
            return id;
        }

        @MethodProxy(order = 32)
        public void setId(byte id) {
            this.id = id;
        }
    }
}