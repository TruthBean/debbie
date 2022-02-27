/**
 * Copyright (c) 2022 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.jdbc.event;

import com.truthbean.debbie.bean.BeanComponent;
import com.truthbean.debbie.bean.BeanType;
import com.truthbean.debbie.proxy.MethodProxy;

import java.util.List;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.0.2
 * Created on 2020-01-23 16:45
 */
@BeanComponent(value = "notingService", lazy = true, type = BeanType.SINGLETON)
public class NotingServiceImpl implements NotingService<Void, Void> {
    @Override
    @MethodProxy
    public List<Void> listAll() {
        System.out.println("list noting");
        return null;
    }
}
