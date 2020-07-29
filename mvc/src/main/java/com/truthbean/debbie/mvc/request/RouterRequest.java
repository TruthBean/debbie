/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mvc.request;

import com.truthbean.debbie.io.MediaTypeInfo;
import com.truthbean.debbie.io.MultipartFile;
import com.truthbean.debbie.mvc.RouterSession;

import java.io.File;
import java.io.InputStream;
import java.net.HttpCookie;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-12-15 12:56.
 */
public interface RouterRequest {

    /**
     * request id, like uuid
     * @return request id
     */
    String getId();

    /**
     * request http method, eg: POST, GET, etc.
     * @see HttpMethod
     * @return httpMethod
     */
    HttpMethod getMethod();

    /**
     * request url, eg: /echo
     * @return string
     */
    String getUrl();

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

    /**
     * get request attribute value by name
     * @param name name
     * @return request attribute value
     */
    Object getAttribute(String name);

    /**
     * get request attributes
     * @return map
     */
    Map<String, Object> getAttributes();

    /**
     * get request url content;
     * eg:
     *  url: /hello/1
     *  pattern: /hello/{id}
     *  id = 1
     * @return map
     */
    Map<String, List<String>> getPathAttributes();

    void setPathAttributes(Map<String, List<String>> map);

    Map<String, List<String>> getMatrix();

    HttpHeader getHeader();

    List<HttpCookie> getCookies();

    default HttpCookie getCookie(String name) {
        var cookies = getCookies();
        for (HttpCookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                return cookie;
            }
        }
        return null;
    }

    RouterSession getSession();

    Map<String, List<Object>> getParameters();

    Object getParameter(String name);

    Map<String, List<String>> getQueries();

    default String getQuery(String name) {
        var queries = getQueries();
        var values = queries.get(name);
        if (values != null && !values.isEmpty()) {
            return values.get(0);
        }
        return null;
    }

    InputStream getInputStreamBody();

    MediaTypeInfo getContentType();

    MediaTypeInfo getResponseType();

    String getRealPath(String path);

    String getContextPath();

    String getTextBody();

    File getFileBody();

    RouterRequest copy();

    void setCharacterEncoding(Charset charset);

    String getRemoteAddress();
}
