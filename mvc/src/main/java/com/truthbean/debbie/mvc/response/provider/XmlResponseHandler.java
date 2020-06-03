/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mvc.response.provider;

import com.truthbean.debbie.io.MediaType;
import com.truthbean.debbie.io.MediaTypeInfo;
import com.truthbean.debbie.util.JacksonUtils;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-03-03 12:01
 */
public class XmlResponseHandler<S> extends AbstractRestResponseHandler<S> {

    @Override
    public String transform(S s) {
        return JacksonUtils.toXml(s);
    }

    @Override
    public MediaTypeInfo getResponseType() {
        return MediaType.APPLICATION_XML_UTF8.info();
    }
}
