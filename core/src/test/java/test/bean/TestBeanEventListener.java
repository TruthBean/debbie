/**
 * Copyright (c) 2022 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package test.bean;

import com.truthbean.debbie.event.DebbieEventListener;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.2.0
 * Created on 2020-12-21 17:36
 */
public class TestBeanEventListener implements DebbieEventListener<TestBeanEvent> {
    @Override
    public String getName() {
        return "testBeanEventListener";
    }

    @Override
    public boolean async() {
        return true;
    }

    @Override
    public boolean allowConcurrent() {
        return true;
    }

    @Override
    public void onEvent(TestBeanEvent event) {
        System.out.println(event.toString());
    }

    @Override
    public boolean supportsSourceType(Class<?> sourceType) {
        return sourceType == TestBeanEvent.class;
    }

    @Override
    public Class<TestBeanEvent> getEventType() {
        return TestBeanEvent.class;
    }
}
