/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.bean;

import com.truthbean.debbie.core.ApplicationContext;

/**
 * @author TruthBean
 * @since 0.1.0
 */
public class BeanExecutableDependence {
    private Integer index;
    private BeanInfo<?> beanInfo;
    private Class<?> type;
    private String name;
    private Object value;

    public BeanExecutableDependence() {
    }

    public BeanExecutableDependence(Integer index, BeanInfo<?> beanInfo, Class<?> type, String name) {
        this.index = index;
        this.beanInfo = beanInfo;
        this.type = type;
        this.name = name;
    }

    public BeanExecutableDependence(Integer index, BeanInfo<?> beanInfo, Class<?> type, String name, Object value) {
        this.index = index;
        this.beanInfo = beanInfo;
        this.type = type;
        this.name = name;
        this.value = value;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public BeanInfo<?> getBeanInfo() {
        return beanInfo;
    }

    public void setBeanInfo(BeanInfo<?> beanInfo) {
        this.beanInfo = beanInfo;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPresent() {
        if (value != null) {
            return true;
        }
        if (this.beanInfo != null && this.beanInfo instanceof DebbieReflectionBeanFactory<?> beanFactory) {
            return beanFactory.isPreparationCreated() || beanFactory.isCreated();
        } else if (this.beanInfo != null && this.beanInfo instanceof BeanFactory<?> beanFactory) {
            return beanFactory.isCreated();
        }
        return false;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public void clear(ApplicationContext applicationContext) {
        this.value = null;
        if (beanInfo != null && this.beanInfo instanceof BeanFactory beanFactory) {
            beanFactory.destruct(applicationContext);
        }
    }

    @Override
    public String toString() {
        return "\"BeanExecutableDependence\":{" +
                "\"index\":" + index + "," +
                "\"beanInfo\":" + beanInfo + "," +
                "\"type\":" + type + "," +
                "\"VALUE\":" + value + "}";
    }
}
