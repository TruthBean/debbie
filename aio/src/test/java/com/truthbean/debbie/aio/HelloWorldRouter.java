package com.truthbean.debbie.aio;

import com.truthbean.debbie.io.MediaType;
import com.truthbean.debbie.mvc.router.GetRouter;
import com.truthbean.debbie.mvc.router.Router;
import com.truthbean.debbie.watcher.Watcher;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.0.2
 * Created on 2020-02-05 11:46
 */
@Watcher
@Router
public class HelloWorldRouter {

    @GetRouter(value = "/hello-world", responseType = MediaType.TEXT_PLAIN_UTF8)
    public String helloWorld() {
        return "hello world";
    }
}
