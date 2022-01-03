/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mvc.request;

import com.truthbean.debbie.io.MediaTypeInfo;
import com.truthbean.debbie.mvc.RouterSession;
import com.truthbean.transformer.DataTransformerCenter;
import com.truthbean.transformer.TransformerFactory;

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
     * @param value VALUE
     */
    void addAttribute(String name, Object value);

    /**
     * remove request attribute
     * @param name name
     */
    void removeAttribute(String name);

    /**
     * get request attribute VALUE by name
     * @param name name
     * @return request attribute VALUE
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

    default List<String> getPathAttributes(String name) {
        var map = getPathAttributes();
        return map.get(name);
    }

    default String getPathAttribute(String name) {
        var map = getPathAttributes();
        List<String> list = map.get(name);
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    default <T> T getPathAttributeValue(String name, Class<T> clazz) {
        var map = getPathAttributes();
        List<String> list = map.get(name);
        if (list != null && !list.isEmpty()) {
            String value = list.get(0);
            if (value != null) {
                return DataTransformerCenter.transform(value, clazz);
            }
        }
        return null;
    }

    void setPathAttributes(Map<String, List<String>> map);

    Map<String, List<String>> getMatrix();

    default List<String> getMatrixValues(String name) {
        var map = getMatrix();
        return map.get(name);
    }

    default String getMatrixValue(String name) {
        var map = getMatrix();
        List<String> list = map.get(name);
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    default <T> T getMatrixValue(String name, Class<T> clazz) {
        var map = getMatrix();
        List<String> list = map.get(name);
        if (list != null && !list.isEmpty()) {
            String value = list.get(0);
            if (value != null) {
                return DataTransformerCenter.transform(value, clazz);
            }
        }
        return null;
    }

    HttpHeader getHeader();

    List<HttpCookie> getCookies();

    default HttpCookie getCookie(String name) {
        var cookies = getCookies();
        if (cookies != null) {
            for (HttpCookie cookie : cookies) {
                if (name.equals(cookie.getName())) {
                    return cookie;
                }
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
