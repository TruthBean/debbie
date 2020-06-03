/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.httpclient;

import com.truthbean.debbie.httpclient.annotation.HttpClientRouter;
import com.truthbean.debbie.io.MediaType;
import com.truthbean.debbie.mvc.request.HttpMethod;
import com.truthbean.debbie.mvc.request.RequestParameter;
import com.truthbean.debbie.mvc.request.RequestParameterType;
import com.truthbean.debbie.mvc.router.Router;
import com.truthbean.debbie.watcher.Watcher;
import com.truthbean.debbie.watcher.WatcherType;

@HttpClientRouter(baseUrl = {"http://192.168.1.192:8080", "http://192.168.1.206:8080", "https://www.facebook.com"})
@Watcher(type = WatcherType.HTTP_CLIENT)
public interface UserHttpClient {

    @Router(value = "/login", method = HttpMethod.POST)
    <T> T login(
            @RequestParameter(paramType = RequestParameterType.BODY, bodyType = MediaType.APPLICATION_JSON_UTF8) String body
    );
}
