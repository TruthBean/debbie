package com.truthbean.code.debbie.httpclient;

import com.truthbean.code.debbie.core.io.MediaType;
import com.truthbean.code.debbie.core.watcher.Watcher;
import com.truthbean.code.debbie.core.watcher.WatcherType;
import com.truthbean.code.debbie.httpclient.annotation.HttpClientRouter;
import com.truthbean.code.debbie.mvc.request.HttpMethod;
import com.truthbean.code.debbie.mvc.request.RequestParam;
import com.truthbean.code.debbie.mvc.request.RequestParamType;
import com.truthbean.code.debbie.mvc.router.Router;

@HttpClientRouter(baseUrl = {"http://192.168.1.192:8080", "http://192.168.1.206:8080", "https://www.facebook.com"})
@Watcher(type = WatcherType.HTTP_CLIENT)
public interface UserHttpClient {

    @Router(value = "/login", method = HttpMethod.POST)
    <T> T login(
            @RequestParam(paramType = RequestParamType.BODY, bodyType = MediaType.APPLICATION_JSON_UTF8) String body
    );
}
