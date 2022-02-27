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

import com.truthbean.debbie.mvc.request.RouterRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class DefaultCsrfTokenFactory implements CsrfTokenFactory {
    private final Map<String, CsrfToken> tokens = new HashMap<>();

    @Override
    public CsrfToken loadToken(RouterRequest request) {
        var id = request.getId();
        if (tokens.containsKey(id)) {
            return tokens.get(id);
        } else {
            var token = CsrfToken.create();
            tokens.put(id, token);
            return token;
        }
    }

    public void reset() {
        tokens.clear();
    }
}
