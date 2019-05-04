package com.truthbean.debbie.mvc.request;

import com.truthbean.debbie.core.io.MediaType;
import com.truthbean.debbie.mvc.RouterSession;
import com.truthbean.debbie.mvc.url.RouterPathAttribute;

import java.io.File;
import java.io.InputStream;
import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/04/13 12:28.
 */
public class DefaultRouterRequest implements RouterRequest {
    private String id;

    private HttpMethod method;

    private String url;

    // TODO
    private List<RouterPathAttribute> pathAttributes;
    private Map<String, List<String>> matrix;

    private Map<String, List<String>> headers;

    private List<HttpCookie> cookies;

    private RouterSession session;

    private Map<String, List> parameters;

    private Map<String, List<String>> queries;

    private InputStream inputStreamBody;
    private String textBody;
    private File fileBody;

    private MediaType contentType;

    private MediaType responseType;

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    @Override
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public List<RouterPathAttribute> getPathAttributes() {
        return pathAttributes;
    }

    @Override
    public Map<String, List<String>> getMatrix() {
        return matrix;
    }

    public void setMatrix(Map<String, List<String>> matrix) {
        this.matrix = matrix;
    }

    public void setPathAttributes(List<RouterPathAttribute> pathAttributes) {
        this.pathAttributes = pathAttributes;
    }

    @Override
    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
    }

    public void addHeaders(Map<String, List<String>> headers) {
        if (this.headers == null) {
            this.headers = new HashMap<>();
        }
        this.headers.putAll(headers);
    }

    public void addHeader(String name, List<String> value) {
        if (this.headers == null) {
            this.headers = new HashMap<>();
        }
        this.headers.put(name, value);
    }

    @Override
    public List<HttpCookie> getCookies() {
        return cookies;
    }

    public void setCookies(List<HttpCookie> cookies) {
        this.cookies = cookies;
    }

    public void addCookies(List<HttpCookie> cookies) {
        if (this.cookies == null) {
            this.cookies = new ArrayList<>();
        }
        this.cookies.addAll(cookies);
    }

    public void addCookie(HttpCookie cookie) {
        if (this.cookies == null) {
            this.cookies = new ArrayList<>();
        }
        this.cookies.add(cookie);
    }

    @Override
    public RouterSession getSession() {
        return session;
    }

    public void setSession(RouterSession session) {
        this.session = session;
    }

    @Override
    public Map<String, List> getParameters() {
        return parameters;
    }

    @Override
    public Object getParameter(String name) {
        if (parameters != null && !parameters.isEmpty()) {
            var values = parameters.get(name);
            if (values != null && !values.isEmpty()) {
                return values.get(0);
            }
        }
        return null;
    }

    public List getParameterValues(String name) {
        if (parameters != null && !parameters.isEmpty()) {
            var values = parameters.get(name);
            if (values != null && !values.isEmpty()) {
                return values;
            }
        }
        return null;
    }

    public void setParameters(Map<String, List> parameters) {
        this.parameters = parameters;
    }

    public void addParameters(Map<String, List> parameters) {
        if (this.parameters == null) {
            this.parameters = new HashMap<>();
        }
        this.parameters.putAll(parameters);
    }

    public void addParameters(String name, List value) {
        if (this.parameters == null) {
            this.parameters = new HashMap<>();
        }
        this.parameters.put(name, value);
    }

    @Override
    public Map<String, List<String>> getQueries() {
        return queries;
    }

    public void setQueries(Map<String, List<String>> queries) {
        this.queries = queries;
    }

    public void addQueries(Map<String, List<String>> queries) {
        if (this.queries == null) {
            this.queries = new HashMap<>();
        }
        this.queries.putAll(queries);
    }

    public void addQueries(String name, List<String> value) {
        if (this.queries == null) {
            this.queries = new HashMap<>();
        }
        this.queries.put(name, value);
    }

    @Override
    public InputStream getInputStreamBody() {
        return inputStreamBody;
    }

    public void setInputStreamBody(InputStream body) {
        this.inputStreamBody = body;
    }

    @Override
    public MediaType getContentType() {
        return contentType;
    }

    public void setContentType(MediaType contentType) {
        this.contentType = contentType;
    }

    @Override
    public MediaType getResponseType() {
        return responseType;
    }

    public void setResponseType(MediaType responseType) {
        this.responseType = responseType;
    }

    @Override
    public String getRealPath(String path) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getContextPath() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getTextBody() {
        return textBody;
    }

    public void setTextBody(String textBody) {
        this.textBody = textBody;
    }

    @Override
    public File getFileBody() {
        return fileBody;
    }

    public void setFileBody(File fileBody) {
        this.fileBody = fileBody;
    }

    @Override
    public RouterRequest copy() {
        DefaultRouterRequest clone = new DefaultRouterRequest();
        clone.id = id;
        clone.method = method;
        clone.url = url;
        clone.inputStreamBody = inputStreamBody;
        clone.contentType = contentType;
        clone.responseType = responseType;
        clone.textBody = textBody;
        clone.fileBody = fileBody;

        if (pathAttributes == null) {
            clone.pathAttributes = new ArrayList<>();
        } else {
            clone.pathAttributes = new ArrayList<>(pathAttributes);
        }

        if (headers == null) {
            clone.headers = new HashMap<>();
        } else {
            clone.headers = new HashMap<>(headers);
        }

        if (cookies == null) {
            clone.cookies = new ArrayList<>();
        } else {
            clone.cookies = new ArrayList<>(cookies);
        }

        clone.session = session;

        if (parameters == null) {
            clone.parameters = new HashMap<>();
        } else {
            clone.parameters = new HashMap<>(parameters);
        }

        if (queries == null) {
            clone.queries = new HashMap<>();
        } else {
            clone.queries = new HashMap<>(queries);
        }
        return clone;
    }

    @Override
    public String toString() {
        return "{" +
                "\"method\":" + method +
                ",\"uri\":\"" + url + '\"' +
                ",\"pathAttributes\":" + pathAttributes +
                ",\"headers\":" + headers +
                ",\"cookies\":" + cookies +
                ",\"session\":" + session +
                ",\"parameters\":" + parameters +
                ",\"queries\":" + queries +
                ",\"textBody\":" + textBody +
                ",\"fileBody\":" + fileBody +
                ",\"inputStreamBody\":" + inputStreamBody +
                ",\"contentType\":" + contentType +
                ",\"responseType\":" + responseType +
                '}';
    }
}
