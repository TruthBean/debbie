/**
 * Copyright (c) 2022 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mvc.csrf;

import com.truthbean.debbie.mvc.RouterSession;
import com.truthbean.debbie.mvc.request.RouterRequest;

import java.util.Set;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class SessionCsrfTokenFactory implements CsrfTokenFactory {

    private final RouterSession session;

    public SessionCsrfTokenFactory(RouterSession session) {
        this.session = session;
    }

    @Override
    public CsrfToken loadToken(RouterRequest request) {
        var id = request.getId();
        Set<String> keys = session.getAttributeNames();
        if (keys.contains(id)) {
            return (CsrfToken) session.getAttribute(id);
        } else {
            var token = CsrfToken.create();
            session.setAttribute(id, token);
            return token;
        }
    }
}
