package com.truthbean.debbie.mvc.request;

import com.truthbean.debbie.io.MediaTypeInfo;
import com.truthbean.debbie.mvc.RouterSession;
import com.truthbean.transformer.DataTransformerCenter;

import java.io.File;
import java.io.InputStream;
import java.net.HttpCookie;
import java.util.List;
import java.util.Map;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public interface BaseRouterRequest {
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

    String getRemoteAddress();
}
