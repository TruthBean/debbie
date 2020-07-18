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

import com.truthbean.debbie.bean.DebbieApplicationContext;
import com.truthbean.debbie.bean.DebbieApplicationContextAware;

/**
 *  DebbieStartedEvent's subclass must annotated by @EventComponent if under debbie managed
 *
 * @author TruthBean
 * @since 0.0.2
 */
public class DebbieStartedEvent extends AbstractDebbieEvent implements DebbieApplicationContextAware {

    private DebbieApplicationContext debbieApplicationContext;

    /**
     * @see AbstractDebbieEvent#AbstractDebbieEvent(Object)
     *
     * @param source event source
     */
    public DebbieStartedEvent(Object source) {
        super(source);
    }

    public DebbieApplicationContext getDebbieApplicationContext() {
        return debbieApplicationContext;
    }

    @Override
    public void setDebbieApplicationContext(DebbieApplicationContext applicationContext) {
        this.debbieApplicationContext = applicationContext;
    }
}
