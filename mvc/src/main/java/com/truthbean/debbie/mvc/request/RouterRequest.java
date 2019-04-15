package com.truthbean.debbie.mvc.request;

import com.truthbean.debbie.core.io.MediaType;
import com.truthbean.debbie.mvc.RouterSession;
import com.truthbean.debbie.mvc.url.RouterPathAttribute;

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
public interface RouterRequest extends Cloneable {

    HttpMethod getMethod();

    String getUrl();

    List<RouterPathAttribute> getPathAttributes();

    Map<String, List<String>> getMatrix();

    Map<String, List<String>> getHeaders();

    List<HttpCookie> getCookies();

    RouterSession getSession();

    Map<String, List> getParameters();

    Map<String, List<String>> getQueries();

    InputStream getInputStreamBody();

    MediaType getContentType();

    MediaType getResponseType();

    String getRealPath(String path);

    String getContextPath();

    String getTextBody();

    File getFileBody();

    RouterRequest clone();
}
