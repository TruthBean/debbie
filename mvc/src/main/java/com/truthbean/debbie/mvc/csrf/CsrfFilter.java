/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mvc.csrf;

import com.truthbean.debbie.mvc.MvcConfiguration;
import com.truthbean.debbie.mvc.filter.RouterFilter;
import com.truthbean.debbie.mvc.request.RouterRequest;
import com.truthbean.debbie.mvc.response.RouterResponse;
import com.truthbean.Logger;
import com.truthbean.LoggerFactory;

/**
 * @author TruthBean
 * @since 0.0.1
 * @since 2019-03-30 14:15
 */
public class CsrfFilter implements RouterFilter {

    private MvcConfiguration configuration;

    @Override
    public CsrfFilter setMvcConfiguration(MvcConfiguration configuration) {
        this.configuration = configuration;
        return this;
    }

    @Override
    public boolean preRouter(RouterRequest request, RouterResponse response) {
        String csrfTokenInHeader = request.getHeader().getHeader("CSRF-TOKEN");
        logger.debug("crsfToken in header: " + csrfTokenInHeader);
        String csrfTokenInParams = (String) request.getParameter("_CSRF_TOKEN");
        logger.debug("crsfToken in hidden form: " + csrfTokenInParams);
        // return configuration.isEnableCrsf();
        return false;
    }

    @Override
    public Boolean postRouter(RouterRequest request, RouterResponse response) {
        return false;
    }

    private final Logger logger = LoggerFactory.getLogger(CsrfFilter.class);
}
