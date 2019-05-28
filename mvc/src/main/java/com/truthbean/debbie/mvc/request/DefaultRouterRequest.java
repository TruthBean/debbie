package com.truthbean.debbie.mvc.request;

import com.truthbean.debbie.core.io.MediaTypeInfo;
import com.truthbean.debbie.mvc.RouterSession;

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

    private Map<String, List<String>> pathAttributes;
    private Map<String, List<String>> matrix;

    private HttpHeader header = new HttpHeader();

    private List<HttpCookie> cookies;

    private RouterSession session;

    private Map<String, List> parameters;

    private Map<String, List<String>> queries;

    private InputStream inputStreamBody;
    private String textBody;
    private File fileBody;

    private MediaTypeInfo contentType;

    private MediaTypeInfo responseType;

    private final Map<String, Object> requestAttribute = new HashMap<>();

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

    @Override
    public void addAttribute(String name, Object value) {
        requestAttribute.put(name, value);
    }

    @Override
    public void removeAttribute(String name) {
        requestAttribute.remove(name);
    }

    @Override
    public Object getAttribute(String name) {
        return requestAttribute.get(name);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return requestAttribute;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public Map<String, List<String>> getPathAttributes() {
        return pathAttributes;
    }

    @Override
    public Map<String, List<String>> getMatrix() {
        return matrix;
    }

    public void setMatrix(Map<String, List<String>> matrix) {
        this.matrix = matrix;
    }

    public void setPathAttributes(Map<String, List<String>> pathAttributes) {
        this.pathAttributes = pathAttributes;
    }

    @Override
    public HttpHeader getHeader() {
        return header;
    }

    public void setHeaders(Map<String, List<String>> headers) {
        this.header.addHeaders(headers);
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

    public void addParameter(String name, Object value) {
        if (this.parameters == null) {
            this.parameters = new HashMap<>();
        }
        List list;
        if (this.parameters.containsKey(name)) {
            list = this.parameters.get(name);
        } else {
            list = new ArrayList();
        }
        list.add(value);
        this.parameters.put(name, list);
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

    public void addQuery(String name, String value) {
        if (this.queries == null) {
            this.queries = new HashMap<>();
        }
        List<String> list;
        if (this.queries.containsKey(name)) {
            list = this.queries.get(name);
        } else {
            list = new ArrayList<>();
        }
        list.add(value);
        this.queries.put(name, list);
    }

    @Override
    public InputStream getInputStreamBody() {
        return inputStreamBody;
    }

    public void setInputStreamBody(InputStream body) {
        this.inputStreamBody = body;
    }

    @Override
    public MediaTypeInfo getContentType() {
        return contentType;
    }

    public void setContentType(MediaTypeInfo contentType) {
        this.contentType = contentType;
    }

    @Override
    public MediaTypeInfo getResponseType() {
        return responseType;
    }

    public void setResponseType(MediaTypeInfo responseType) {
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
            clone.pathAttributes = new HashMap<>();
        } else {
            clone.pathAttributes = new HashMap<>(pathAttributes);
        }

        if (matrix == null) {
            clone.matrix = new HashMap<>();
        } else {
            clone.matrix = new HashMap<>(matrix);
        }

        clone.header = header.copy();

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
                ",\"header\":" + header +
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
