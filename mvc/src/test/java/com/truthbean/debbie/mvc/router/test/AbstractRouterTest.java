/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mvc.router.test;

import com.truthbean.debbie.mvc.request.QueryParameter;
import com.truthbean.debbie.mvc.router.Router;

import java.util.List;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.0.2
 * Created on 2019-12-03 12:07
 */
public class AbstractRouterTest<C extends List> {

    @Router(urlPatterns = "/hehe")
    public CharSequence testPathAttribute0(
            @QueryParameter(name = "hehe", require = false, defaultValue = "0") C id) {
        return "<html><head><title>" + id + "</title></head><body><p>this is a path attribute test</p></body></html>";
    }
}
