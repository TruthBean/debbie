/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.event;

import java.lang.reflect.Method;

/**
 * @author truthbean
 * @since 0.0.2
 */
public class DefaultEventListenerFactory implements EventListenerFactory {

    private int order = Integer.MAX_VALUE;


    public void setOrder(int order) {
        this.order = order;
    }

    public int getOrder() {
        return this.order;
    }


    @Override
    public boolean supportsMethod(Method method) {
        return true;
    }

    @Override
    public DebbieEventListener<?> createEventListener(String beanName, Class<?> type, Method method) {
        return null;
    }
}
