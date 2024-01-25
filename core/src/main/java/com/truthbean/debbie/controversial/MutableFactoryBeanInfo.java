/**
 * Copyright (c) 2024 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 *//*

package com.truthbean.debbie.controversial;

import com.truthbean.debbie.bean.BeanFactory;
import com.truthbean.debbie.bean.MutableBeanInfo;
import com.truthbean.debbie.core.ApplicationContext;

*/
/**
 * @author TruthBean/Rogar·Q
 * @since 0.0.2
 * Created on 2020-06-07 20:31
 *//*

interface MutableFactoryBeanInfo<Bean> extends FactoryBeanInfo<Bean>, MutableBeanInfo<Bean> {

    void setBeanFactory(BeanFactory<Bean> beanFactory);

    @Override
    default void destruct(ApplicationContext applicationContext) {
        BeanFactory<Bean> beanFactory = getBeanFactory();
        if (beanFactory != null) {
            beanFactory.destruct(applicationContext);
            setBeanFactory(null);
        } else {
            close(applicationContext);
        }
        setBean((Bean) null);
    }
}
*/
