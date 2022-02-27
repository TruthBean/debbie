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

import com.truthbean.Logger;
import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.debbie.check.jdbc.entity.Surname;
import com.truthbean.debbie.event.DebbieStartedEvent;
import com.truthbean.debbie.event.EventBeanListener;
import com.truthbean.debbie.event.GenericStartedEventListener;
import com.truthbean.debbie.jdbc.annotation.JdbcTransactional;
import com.truthbean.debbie.jdbc.service.SurnameService;
import com.truthbean.LoggerFactory;

import java.util.List;

/**
 * @author truthbean/Rogar·Q
 * @since 0.0.2
 * Created on 2020-01-19 18:43.
 */
@EventBeanListener
public class StartedListener implements GenericStartedEventListener<DebbieStartedEvent> {

    @BeanInject
    private SurnameService surnameService;

    @BeanInject
    private NotingService notingService;

    @Override
    public void onApplicationEvent(DebbieStartedEvent event) {
        this.printStartInfo();
    }

    private void printStartInfo() {
        LOGGER.debug(() -> "！！！！！！！！！！！！！！");
        List<Surname> surnames = surnameService.list();
        System.out.println(surnames);
        notingService.listAll();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(StartedListener.class);
}
