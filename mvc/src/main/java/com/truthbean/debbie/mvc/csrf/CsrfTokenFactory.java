/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mvc.csrf;

import com.truthbean.debbie.mvc.request.RouterRequest;

/**
 * Factory for loading or creating {@link CsrfToken}s associated with
 * incoming requests.
 *
 * @author TruthBean
 * @since 0.0.1
 */
public interface CsrfTokenFactory {

    /**
     * Loads (or creates) the CSRF token for the given request.
     *
     * @param request the router request
     * @return the CSRF token
     */
    CsrfToken loadToken(RouterRequest request);

}
