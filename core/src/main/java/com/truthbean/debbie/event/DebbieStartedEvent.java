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

import com.truthbean.debbie.bean.BeanFactoryContext;

/**
 * @author TruthBean
 * @since 0.0.2
 */
public class DebbieStartedEvent extends AbstractDebbieEvent {

    private final BeanFactoryContext applicationContext;

    /**
     * @see AbstractDebbieEvent#AbstractDebbieEvent(Object)
     *
     * @param source event source
     * @param applicationContext bean factory handler
     */
    public DebbieStartedEvent(Object source, BeanFactoryContext applicationContext) {
        super(source);
        this.applicationContext = applicationContext;
    }

    public BeanFactoryContext getApplicationContext() {
        return applicationContext;
    }
}
