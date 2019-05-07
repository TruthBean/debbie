package com.truthbean.debbie.mvc.request;

import com.truthbean.debbie.core.io.MediaType;
import com.truthbean.debbie.core.net.uri.UriPathFragment;
import com.truthbean.debbie.mvc.RouterSession;

import java.io.File;
import java.io.InputStream;
import java.net.HttpCookie;
import java.util.List;
import java.util.Map;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-12-15 12:56.
 */
public interface RouterRequest {

    String getId();

    HttpMethod getMethod();

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

    Map<String, List<String>> getPathAttributes();

    Map<String, List<String>> getMatrix();

    Map<String, List<String>> getHeaders();

    List<HttpCookie> getCookies();

    RouterSession getSession();

    Map<String, List> getParameters();

    Object getParameter(String name);

    Map<String, List<String>> getQueries();

    InputStream getInputStreamBody();

    MediaType getContentType();

    MediaType getResponseType();

    String getRealPath(String path);

    String getContextPath();

    String getTextBody();

    File getFileBody();

    RouterRequest copy();
}
