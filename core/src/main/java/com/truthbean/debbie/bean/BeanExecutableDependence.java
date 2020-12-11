/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.bean;

/**
 * @author TruthBean
 * @since 0.1.0
 */
public class BeanExecutableDependence {
    private Integer index;
    private BeanInfo<?> beanInfo;
    private Class<?> type;
    private Object value;

    public BeanExecutableDependence() {
    }

    public BeanExecutableDependence(Integer index, BeanInfo<?> beanInfo, Class<?> type) {
        this.index = index;
        this.beanInfo = beanInfo;
        this.type = type;
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

    public boolean isPresent() {
        if (value != null) {
            return true;
        }
        if (this.beanInfo != null)
            return this.beanInfo.isPresent();
        return false;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "\"BeanExecutableDependence\":{" +
                "\"index\":" + index + "," +
                "\"beanInfo\":" + beanInfo + "," +
                "\"type\":" + type + "," +
                "\"value\":" + value + "}";
    }
}
