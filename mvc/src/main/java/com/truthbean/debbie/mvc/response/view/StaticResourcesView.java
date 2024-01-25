/**
 * Copyright (c) 2024 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mvc.response.view;

import com.truthbean.debbie.io.ResourcesHandler;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-02-18 19:43
 */
public class StaticResourcesView extends AbstractView {

    public StaticResourcesView() {
        setText(true);
        setSuffix(".html");
        setPrefix("classpath*:statics/");
    }

    @Override
    public Object render() {
        if (isText()) {
            return ResourcesHandler.handleStaticResource(getLocation());
        } else {
            return ResourcesHandler.handleStaticBytesResource(getLocation());
        }
    }
}
