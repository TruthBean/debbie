/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.bean;

import java.lang.annotation.Annotation;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-09-18 00:16
 */
public class DefaultBeanComponentParser implements BeanComponentParser {
    @Override
    public BeanComponentInfo parse(Annotation annotation, Class<?> beanType) {
        if (annotation == null)
            return null;
        if (annotation.annotationType() == BeanComponent.class) {
            return parse((BeanComponent) annotation);
        }
        // todo
        return null;
    }

    public BeanComponentInfo parse(BeanComponent value) {
        var info = new BeanComponentInfo();
        String beanName = value.name();
        if (beanName.isBlank()) {
            beanName = value.value();
        }
        if (!beanName.isBlank()) {
            info.setName(beanName);
        }
        info.setType(value.type());
        info.setProxy(value.proxy());
        info.setLazy(value.lazy());
        info.setFactory(value.factory());

        return info;
    }
}
