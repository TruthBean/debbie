package com.truthbean.debbie.rmi;

import com.truthbean.debbie.bean.BeanInject;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestClient {

    @Test
    public void test() {
        //注册管理器
        RmiClientFactory factory = new RmiClientFactory("192.168.1.198", 8088);

        //列出所有注册的服务
        String[] list = factory.listName();
        for (String s : list) {
            LOGGER.debug(s);
        }

        //根据命名获取服务
        TestRmiService service = factory.lookup("test");
        //调用远程方法
        String result = service.queryName("hello");
        //输出调用结果
        LOGGER.debug("result from remote : " + result);
    }

    @Test
    public void queryName(@BeanInject TestRmiService service) {
        LOGGER.debug(service.queryName("hello"));
    }

    public static final Logger LOGGER = LoggerFactory.getLogger(TestClient.class);
}
