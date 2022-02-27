/**
 * Copyright (c) 2022 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mvc.response.provider;

import com.truthbean.debbie.io.MediaType;
import com.truthbean.debbie.io.MediaTypeInfo;

import java.util.Map;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.0
 * Created on 2021-02-19 18:28
 */
public class PropertiesResponseHandler extends AbstractRestResponseHandler<Map<String, String>> {
    @Override
    public String transform(Map<String, String> s) {
        StringBuilder sb = new StringBuilder();
        if (s != null) {
            s.forEach((key, value) -> sb.append(key).append("=").append(value).append("\n"));
        }
        return sb.toString();
    }

    @Override
    public MediaTypeInfo getResponseType() {
        return MediaType.TEXT_PLAIN_UTF8.info();
    }
}
