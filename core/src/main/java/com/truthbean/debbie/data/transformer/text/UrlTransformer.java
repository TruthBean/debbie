/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.data.transformer.text;

import com.truthbean.debbie.data.transformer.DataTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class UrlTransformer implements DataTransformer<URL, String> {
    @Override
    public String transform(URL url) {
        return url.toString();
    }

    @Override
    public URL reverse(String s) {
        try {
            return new URL(s);
        } catch (MalformedURLException e) {
            LOGGER.error("", e);
        }
        return null;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(UrlTransformer.class);
}
