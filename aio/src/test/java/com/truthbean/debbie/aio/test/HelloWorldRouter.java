/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.aio.test;

import com.truthbean.debbie.io.MediaType;
import com.truthbean.debbie.mvc.request.RequestParameter;
import com.truthbean.debbie.mvc.response.ResponseEntity;
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

    @Router(value = "/hello-world", responseType = MediaType.TEXT_PLAIN_UTF8)
    public String helloWorld() {
        return "hello world";
    }

    @Router(value = "/custom/api")
    public SimpleApiResponse<String> getCustomApi(@RequestParameter("id") Long id) {
        return new SimpleApiResponse<>(200, "获取成功", "rtsp://192.168.1.200:8554/channel2");
    }
}
