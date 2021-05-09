/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.aio.test;

import com.truthbean.debbie.bean.BeanConfiguration;
import com.truthbean.debbie.bean.DebbieBean;

import java.util.concurrent.Callable;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.0.2
 * Created on 2019-12-17 21:00
 */
@BeanConfiguration
public class AioApplicationConfiguration {

    @DebbieBean(name = "test")
    public Callable<Void> test() {
        return () -> {
            System.out.println("call nothing");
            return null;
        };
    }
}
