/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mvc.filter;

import com.truthbean.debbie.mvc.MvcConfiguration;
import com.truthbean.debbie.mvc.request.RouterRequest;
import com.truthbean.debbie.mvc.response.RouterResponse;
import com.truthbean.Logger;
import com.truthbean.LoggerFactory;

import java.nio.charset.Charset;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/15 21:21.
 */
public class CharacterEncodingFilter implements RouterFilter {

    private Charset charset;

    @Override
    public CharacterEncodingFilter setMvcConfiguration(MvcConfiguration configuration) {
        charset = configuration.getCharset();
        return this;
    }

    @Override
    public boolean preRouter(RouterRequest request, RouterResponse response) {
        LOGGER.trace("set character encoding by filter");
        request.setCharacterEncoding(this.charset);
        return false;
    }

    @Override
    public Boolean postRouter(RouterRequest request, RouterResponse response) {
        response.setCharacterEncoding(this.charset);
        return false;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(CharacterEncodingFilter.class);
}