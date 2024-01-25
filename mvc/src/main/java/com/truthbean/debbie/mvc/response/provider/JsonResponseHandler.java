/**
 * Copyright (c) 2024 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mvc.response.provider;

import com.truthbean.debbie.data.serialize.jackson.JacksonJsonUtils;
import com.truthbean.debbie.io.MediaType;
import com.truthbean.debbie.io.MediaTypeInfo;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/23 14:01.
 */
public class JsonResponseHandler<S> extends AbstractRestResponseHandler<S> {
    @Override
    public String transform(S original) {
        return JacksonJsonUtils.toJson(original);
    }

    @Override
    public MediaTypeInfo getResponseType() {
        return MediaType.APPLICATION_JSON_UTF8.info();
    }
}
