package com.truthbean.debbie.example.httpclient;

import com.truthbean.debbie.core.io.MediaType;
import com.truthbean.debbie.core.watcher.Watcher;
import com.truthbean.debbie.core.watcher.WatcherType;
import com.truthbean.debbie.httpclient.annotation.HttpClientRouter;
import com.truthbean.debbie.mvc.request.HttpMethod;
import com.truthbean.debbie.mvc.request.RequestParameter;
import com.truthbean.debbie.mvc.request.RequestParameterType;
import com.truthbean.debbie.mvc.router.Router;

/**
 * @author truth
 * @since 0.0.1
 */
@HttpClientRouter(baseUrl = {"http://192.168.1.192:8080", "http://192.168.1.206:8080", "https://www.example.com"})
@Watcher(type = WatcherType.HTTP_CLIENT)
public interface UserHttpClient {

    @Router(value = "/login", method = HttpMethod.POST)
    <T> T login(@RequestParameter(paramType = RequestParameterType.BODY, bodyType = MediaType.APPLICATION_JSON_UTF8) String body);
}
