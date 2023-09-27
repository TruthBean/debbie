/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mvc.router;

import com.truthbean.debbie.mvc.request.RouterRequest;
import com.truthbean.debbie.mvc.response.RouterResponse;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.2.0
 * Created on 2020-11-27 15:28
 */
public class SimpleRouterExecutor implements RouterExecutor {
    private RouterRequest request;
    private RouterResponse response;

    private final MvcRouter router;
    public SimpleRouterExecutor(MvcRouter router) {
        this.router = router;
    }

    public void setRequest(RouterRequest request) {
        this.request = request;
    }

    public void setResponse(RouterResponse response) {
        this.response = response;
    }

    @Override
    public boolean returnVoid() {
        return true;
    }

    @Override
    public Object execute(Object... params) throws Throwable {
        router.route(request, response);
        return null;
    }
}
