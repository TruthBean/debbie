package com.truthbean.debbie.httpclient.test;

import com.truthbean.debbie.httpclient.annotation.HttpClientRouter;
import com.truthbean.debbie.mvc.router.GetRouter;
import com.truthbean.debbie.watcher.Watcher;
import com.truthbean.debbie.watcher.WatcherType;

/**
 * @author OceanAi/武汉魅瞳科技有限公司
 * @since 1.0.0
 * Created on 2020-10-13 16:55
 */
@Watcher(type = WatcherType.HTTP_CLIENT)
@HttpClientRouter(baseUrl = {"{ping.url}"}, failureAction = ErrorPingService.class)
public interface PingService {

    @GetRouter("/ping")
    String ping();
}
