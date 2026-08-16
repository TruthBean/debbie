/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mvc.request;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

/**
 * Mutable extension of {@link BaseRouterRequest} adding write access
 * to request attributes, path attributes, and character encoding.
 *
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-12-15 12:56.
 */
public interface RouterRequest extends BaseRouterRequest {
    /**
     * add request attribute
     * @param name name
     * @param value value
     */
    void addAttribute(String name, Object value);

    /**
     * remove request attribute
     * @param name name
     */
    void removeAttribute(String name);

    /** Sets the path attributes extracted from the URL pattern. */
    void setPathAttributes(Map<String, List<String>> map);


    /** Sets the character encoding for the request. */
    void setCharacterEncoding(Charset charset);
}
