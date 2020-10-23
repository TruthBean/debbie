/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.servlet.request;

import com.truthbean.debbie.io.FileNameUtils;
import com.truthbean.debbie.io.MediaType;
import com.truthbean.debbie.io.MediaTypeInfo;
import com.truthbean.debbie.io.MultipartFile;
import com.truthbean.debbie.mvc.request.DefaultRouterRequest;
import com.truthbean.debbie.mvc.request.HttpMethod;
import com.truthbean.debbie.mvc.request.RequestBody;
import com.truthbean.debbie.mvc.request.RouterRequest;
import com.truthbean.debbie.net.uri.QueryStringDecoder;
import com.truthbean.debbie.net.uri.UriUtils;
import com.truthbean.debbie.servlet.ServletRouterCookie;
import com.truthbean.debbie.servlet.ServletRouterSession;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpCookie;
import java.nio.charset.Charset;
import java.security.Principal;
import java.util.*;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-02-19 11:11
 */
public class HttpServletRequestWrapper implements HttpServletRequest {

    private final HttpServletRequest request;
    private final DefaultRouterRequest routerRequest;

    public HttpServletRequestWrapper(HttpServletRequest httpServletRequest) {
        this(UUID.randomUUID().toString(), httpServletRequest);
    }

    private HttpServletRequestWrapper(RouterRequest routerRequest, HttpServletRequest request) {
        this.routerRequest = new DefaultRouterRequest();
        this.request = request;
        this.routerRequest.copy(routerRequest);
    }

    private HttpServletRequestWrapper(String id, HttpServletRequest httpServletRequest) {
        this.routerRequest = new DefaultRouterRequest() {
            @Override
            public String getRealPath(String path) {
                return httpServletRequest.getServletContext().getRealPath(path);
            }

            @Override
            public String getContextPath() {
                return httpServletRequest.getContextPath();
            }

            @Override
            public void setCharacterEncoding(Charset charset) {
                try {
                    request.setCharacterEncoding(charset.name());
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        };
        this.request = httpServletRequest;
        this.routerRequest.setMethod(HttpMethod.valueOf(request.getMethod()));
        this.routerRequest.setUrl(request.getRequestURI());
        this.routerRequest.setMatrix(UriUtils.resolveMatrixByPath(this.routerRequest.getUrl()));

        this.routerRequest.setPathAttributes(new HashMap<>());
        setHeaders();
        setCookies();

        this.routerRequest.setMethod(HttpMethod.resolve(request.getMethod()));

        this.routerRequest.setSession(new ServletRouterSession(request));

        this.routerRequest.setQueries(queries(request.getQueryString()));
        setParams();
        setBody();

        setContentType();
        setResponseType();

        setRequestAttribute();

        this.routerRequest.setId(id);
    }

    public HttpServletRequest getHttpServletRequest() {
        return request;
    }

    private void setHeaders() {
        Map<String, List<String>> map = new HashMap<>();
        var headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            var headerName = headerNames.nextElement();
            List<String> value = new ArrayList<>();
            var headers = request.getHeaders(headerName);
            while (headers.hasMoreElements()) {
                value.add(headers.nextElement());
            }
            map.put(headerName, value);
        }
        this.routerRequest.setHeaders(map);
    }

    private void setCookies() {
        var cookies = request.getCookies();
        List<HttpCookie> result = new ArrayList<>();

        if (cookies != null) {
            for (var cookie : cookies) {
                result.add(new ServletRouterCookie(cookie).getHttpCookie());
            }
        }
        this.routerRequest.setCookies(result);
    }

    private void setParams() {
        Map<String, List<Object>> map = new HashMap<>();
        var parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            var name = parameterNames.nextElement();
            var values = request.getParameterValues(name);
            List<Object> objects = new ArrayList<>(Arrays.asList(values));
            map.put(name, objects);
        }
        var paramsInBody = getParamsInBody();
        if (!paramsInBody.isEmpty()) {
            map.putAll(paramsInBody);
        }
        this.routerRequest.setParameters(map);
    }

    private Map<String, List<Object>> getParamsInBody() {
        var headers = this.routerRequest.getHeader();
        String type = MediaType.ANY.getValue();
        if (headers.getHeader(MediaType.CONTENT_TYPE) != null) {
            type = headers.getHeader(MediaType.CONTENT_TYPE);
        }
        if (MediaType.APPLICATION_FORM_URLENCODED.getValue().equals(type)) {
            try {
                RequestBody requestBody = new RequestBody(request.getInputStream());
                var content = requestBody.getContent();
                if (content != null && !content.isEmpty()) {
                    var queries = queries(content.get(0));
                    Map<String, List<Object>> map = new HashMap<>();
                    queries.forEach((key, value) -> map.put(key, new ArrayList<>(value)));
                    return map;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return getMultipartParams();
    }

    private Map<String, List<Object>> getMultipartParams() {
        Map<String, List<Object>> map = new HashMap<>();
        if (ServletFileUpload.isMultipartContent(request)) {
            var maxMemorySize = 1024 * 1024 * 1024;
            var tempDirectory = new File(System.getProperty("java.io.tmpdir"));
            // Create a factory for disk-based file items
            var factory = new DiskFileItemFactory(maxMemorySize, tempDirectory);
            // Configure a repository (to ensure a secure temp location is used)
            var repository = (File) request.getServletContext().getAttribute("javax.servlet.context.tempdir");
            factory.setRepository(repository);

            // Create a new file upload handler
            var upload = new ServletFileUpload(factory);

            var maxRequestSize = 1024 * 1024 * 1024;
            // Set overall httpRequest size constraint
            upload.setSizeMax(maxRequestSize);

            try {
                // Parse the httpRequest
                var items = upload.parseParameterMap(new ServletRequestContext(request));

                // Process the uploaded items
                processFormField(items, map);
            } catch (FileUploadException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    private void processFormField(Map<String, List<FileItem>> items, Map<String, List<Object>> map) {
        items.forEach((key, value) -> {
            List<Object> values = new ArrayList<>();
            value.forEach(fileItem -> {
                if (fileItem.isFormField()) {
                    try {
                        values.add(fileItem.getString("UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                } else {
                    var multipartFile = new MultipartFile();
                    multipartFile.setFileName(fileItem.getName());
                    var type = fileItem.getContentType();
                    var contentType = MediaType.APPLICATION_OCTET_STREAM;
                    if (type != null && !"null".equalsIgnoreCase(type)) {
                        contentType = MediaType.ofWithOctetDefault(type);
                    }
                    multipartFile.setContentType(contentType);
                    multipartFile.setContent(fileItem.get());
                    values.add(multipartFile);
                }
            });
            map.put(key, values);
        });
    }

    private Map<String, List<String>> queries(String url) {
        return queries(url, false);
    }

    private Map<String, List<String>> queries(String url, boolean encode) {
        Map<String, List<String>> map = new HashMap<>();
        if (encode) {
            if (url != null) {
                var decoder = new QueryStringDecoder(url);
                map.putAll(decoder.parameters());
            }
        } else {
            if (url != null) {
                var decoder = new QueryStringDecoder("/?" + url);
                map.putAll(decoder.parameters());
            }
        }

        return map;
    }

    private void setBody() {
        try {
            this.routerRequest.setInputStreamBody(request.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setContentType() {
        var respType = request.getContentType();
        if (respType != null) {
            this.routerRequest.setContentType(MediaTypeInfo.parse(respType));
        } else {
            this.routerRequest.setContentType(MediaType.ANY.info());
        }
    }

    private void setResponseType() {
        var respType = request.getHeader("Response-Type");
        MediaTypeInfo mediaType = MediaType.ANY.info();
        if (respType != null) {
            mediaType = MediaTypeInfo.parse(respType);
        } else {
            var ext = FileNameUtils.getExtension(this.routerRequest.getUrl());
            if (ext != null && !ext.isBlank()) {
                mediaType = MediaType.getTypeByUriExt(ext).info();
            }
        }
        this.routerRequest.setResponseType(mediaType);
    }

    private void setRequestAttribute() {
        Enumeration<String> attributeNames = request.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            var name = attributeNames.nextElement();
            this.routerRequest.addAttribute(name, request.getAttribute(name));
        }
    }

    @Override
    public Object getAttribute(String name) {
        return request.getAttribute(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return request.getAttributeNames();
    }

    @Override
    public String getCharacterEncoding() {
        return request.getCharacterEncoding();
    }

    @Override
    public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
        request.setCharacterEncoding(env);
    }

    @Override
    public int getContentLength() {
        return request.getContentLength();
    }

    @Override
    public long getContentLengthLong() {
        return request.getContentLengthLong();
    }

    @Override
    public String getContentType() {
        return request.getContentType();
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return request.getInputStream();
    }

    @Override
    public String getParameter(String name) {
        return request.getParameter(name);
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return request.getParameterNames();
    }

    @Override
    public String[] getParameterValues(String name) {
        return request.getParameterValues(name);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return request.getParameterMap();
    }

    @Override
    public String getProtocol() {
        return request.getProtocol();
    }

    @Override
    public String getScheme() {
        return request.getScheme();
    }

    @Override
    public String getServerName() {
        return request.getServerName();
    }

    @Override
    public int getServerPort() {
        return request.getServerPort();
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return request.getReader();
    }

    @Override
    public String getRemoteAddr() {
        return request.getRemoteAddr();
    }

    @Override
    public String getRemoteHost() {
        return request.getRemoteHost();
    }

    @Override
    public void setAttribute(String name, Object o) {
        request.setAttribute(name, o);
    }

    @Override
    public void removeAttribute(String name) {
        request.removeAttribute(name);
    }

    @Override
    public Locale getLocale() {
        return request.getLocale();
    }

    @Override
    public Enumeration<Locale> getLocales() {
        return request.getLocales();
    }

    @Override
    public boolean isSecure() {
        return request.isSecure();
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        return request.getRequestDispatcher(path);
    }

    @Override
    public String getRealPath(String path) {
        return request.getServletContext().getRealPath(path);
    }

    @Override
    public int getRemotePort() {
        return request.getRemotePort();
    }

    @Override
    public String getLocalName() {
        return request.getLocalName();
    }

    @Override
    public String getLocalAddr() {
        return request.getLocalAddr();
    }

    @Override
    public int getLocalPort() {
        return request.getLocalPort();
    }

    @Override
    public ServletContext getServletContext() {
        return request.getServletContext();
    }

    @Override
    public AsyncContext startAsync() throws IllegalStateException {
        return request.startAsync();
    }

    @Override
    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
        return request.startAsync(servletRequest, servletResponse);
    }

    @Override
    public boolean isAsyncStarted() {
        return request.isAsyncStarted();
    }

    @Override
    public boolean isAsyncSupported() {
        return request.isAsyncSupported();
    }

    @Override
    public AsyncContext getAsyncContext() {
        return request.getAsyncContext();
    }

    @Override
    public DispatcherType getDispatcherType() {
        return request.getDispatcherType();
    }

    @Override
    public String getAuthType() {
        return request.getAuthType();
    }

    @Override
    public Cookie[] getCookies() {
        return request.getCookies();
    }

    @Override
    public long getDateHeader(String name) {
        return request.getDateHeader(name);
    }

    @Override
    public String getHeader(String name) {
        return request.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        return request.getHeaders(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return request.getHeaderNames();
    }

    @Override
    public int getIntHeader(String name) {
        return request.getIntHeader(name);
    }

    @Override
    public HttpServletMapping getHttpServletMapping() {
        return request.getHttpServletMapping();
    }

    @Override
    public String getMethod() {
        return request.getMethod();
    }

    @Override
    public String getPathInfo() {
        return request.getPathInfo();
    }

    @Override
    public String getPathTranslated() {
        return request.getPathTranslated();
    }

    @Override
    public PushBuilder newPushBuilder() {
        return request.newPushBuilder();
    }

    @Override
    public String getContextPath() {
        return request.getContextPath();
    }

    @Override
    public String getQueryString() {
        return request.getQueryString();
    }

    @Override
    public String getRemoteUser() {
        return request.getRemoteUser();
    }

    @Override
    public boolean isUserInRole(String role) {
        return request.isUserInRole(role);
    }

    @Override
    public Principal getUserPrincipal() {
        return request.getUserPrincipal();
    }

    @Override
    public String getRequestedSessionId() {
        return request.getRequestedSessionId();
    }

    @Override
    public String getRequestURI() {
        return request.getRequestURI();
    }

    @Override
    public StringBuffer getRequestURL() {
        return request.getRequestURL();
    }

    @Override
    public String getServletPath() {
        return request.getServletPath();
    }

    @Override
    public HttpSession getSession(boolean create) {
        return request.getSession(create);
    }

    @Override
    public HttpSession getSession() {
        return request.getSession();
    }

    @Override
    public String changeSessionId() {
        return request.changeSessionId();
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        return request.isRequestedSessionIdValid();
    }

    @Override
    public boolean isRequestedSessionIdFromCookie() {
        return request.isRequestedSessionIdFromCookie();
    }

    @Override
    public boolean isRequestedSessionIdFromURL() {
        return request.isRequestedSessionIdFromURL();
    }

    @Override
    public boolean isRequestedSessionIdFromUrl() {
        return request.isRequestedSessionIdFromURL();
    }

    @Override
    public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
        return request.authenticate(response);
    }

    @Override
    public void login(String username, String password) throws ServletException {
        request.login(username, password);
    }

    @Override
    public void logout() throws ServletException {
        request.logout();
    }

    @Override
    public Collection<Part> getParts() throws IOException, ServletException {
        return request.getParts();
    }

    @Override
    public Part getPart(String name) throws IOException, ServletException {
        return request.getPart(name);
    }

    @Override
    public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) throws IOException, ServletException {
        return request.upgrade(handlerClass);
    }

    @Override
    public Map<String, String> getTrailerFields() {
        return request.getTrailerFields();
    }

    @Override
    public boolean isTrailerFieldsReady() {
        return request.isTrailerFieldsReady();
    }

    public RouterRequest getRouterRequest() {
        return this.routerRequest;
    }
}
