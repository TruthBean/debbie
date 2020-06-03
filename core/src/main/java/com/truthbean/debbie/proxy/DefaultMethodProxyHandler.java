/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * @author truthbean
 * @since 0.0.1
 */
public class DefaultMethodProxyHandler implements MethodProxyHandler<MethodProxy> {

    private String methodName;

    private int order;

    public DefaultMethodProxyHandler() {
    }

    @Override
    public void setMethod(Method method) {
        methodName = method.getName();
    }

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public void before() {
        LOGGER.debug("runing before method(" + methodName + ") invoke ..");
    }

    @Override
    public void after() {
        LOGGER.debug("runing after method(" + methodName + ") invoke ..");
    }

    @Override
    public void catchException(Throwable e) throws Throwable {
        LOGGER.debug("runing when method(" + methodName + ") invoke throw exception and cached ..", e);
        if (e != null) {
            throw e;
        }
    }

    @Override
    public void finallyRun() {
        LOGGER.debug("runing when method(" + methodName + ") invoke throw exception and run to finally ..");
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultMethodProxyHandler.class);
}
