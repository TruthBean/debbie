/**
 * Copyright (c) 2022 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.event;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-10-11 00:27
 */
public class ApplicationExitEventListener implements DebbieEventListener<ApplicationExitEvent> {
    @Override
    public void onEvent(ApplicationExitEvent event) {
        logger.info(() -> "application closed by ApplicationExitEvent");
        event.getDebbieApplication().exit();
    }

    @Override
    public boolean async() {
        return true;
    }

    @Override
    public Class<ApplicationExitEvent> getEventType() {
        return ApplicationExitEvent.class;
    }

    private static final Logger logger = LoggerFactory.getLogger(ApplicationExitEventListener.class);
}
