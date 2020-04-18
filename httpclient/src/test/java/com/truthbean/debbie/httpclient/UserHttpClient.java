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
