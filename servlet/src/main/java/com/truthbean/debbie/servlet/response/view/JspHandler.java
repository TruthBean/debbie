/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.servlet.response.view;

import com.truthbean.debbie.io.MediaType;
import com.truthbean.debbie.io.MediaTypeInfo;
import com.truthbean.debbie.mvc.response.view.AbstractTemplateViewHandler;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/10 21:13.
 */
public class JspHandler extends AbstractTemplateViewHandler {

    @Override
    public Object transform(Object o) {
        // do nothing
        return o;
    }

    @Override
    public MediaTypeInfo getResponseType() {
        return MediaType.TEXT_HTML_UTF8.info();
    }
}