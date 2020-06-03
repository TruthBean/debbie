/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.servlet;

import com.truthbean.debbie.mvc.MvcConfiguration;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/10 18:56.
 */
public class ServletConfiguration extends MvcConfiguration {
    public ServletConfiguration(ClassLoader classLoader) {
        super(classLoader);
    }

    public ServletConfiguration(MvcConfiguration configuration, ClassLoader classLoader) {
        super(configuration, classLoader);
    }
}
