package com.truthbean.debbie.rmi;

import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.Logger;
import com.truthbean.logger.LoggerFactory;

@DebbieRmiService("test")
public class TestRmiServiceImpl implements TestRmiService {

    private final TestBeanD testBeanD;

    public TestRmiServiceImpl(@BeanInject TestBeanD testBeanD) {
        super();
        this.testBeanD = testBeanD;
    }

    @Override
    public String queryName(String id) {
        logger.debug("debuggggggggggggggggggggggggggggggggggggggger");
        return "pong " + id + "---->" + testBeanD.getId();
    }

    private static final Logger logger = LoggerFactory.getLogger(TestRmiServiceImpl.class);
}
