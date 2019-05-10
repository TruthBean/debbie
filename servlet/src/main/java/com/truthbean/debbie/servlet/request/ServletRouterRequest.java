package com.truthbean.debbie.servlet.request;

import com.truthbean.debbie.core.io.FileNameUtils;
import com.truthbean.debbie.core.io.MediaType;
import com.truthbean.debbie.core.io.MultipartFile;
import com.truthbean.debbie.core.net.uri.QueryStringDecoder;
import com.truthbean.debbie.core.net.uri.UriUtils;
import com.truthbean.debbie.mvc.request.DefaultRouterRequest;
import com.truthbean.debbie.mvc.request.HttpMethod;
import com.truthbean.debbie.mvc.request.RequestBody;
import com.truthbean.debbie.servlet.ServletRouterCookie;
import com.truthbean.debbie.servlet.ServletRouterSession;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpCookie;
import java.util.*;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-02-19 11:11
 */
public class ServletRouterRequest extends DefaultRouterRequest {

    private HttpServletRequest request;

    public ServletRouterRequest(HttpServletRequest httpServletRequest) {
        this(UUID.randomUUID().toString(), httpServletRequest);
    }

    private ServletRouterRequest(String id, HttpServletRequest httpServletRequest) {
        this.request = httpServletRequest;
        setMethod(HttpMethod.valueOf(request.getMethod()));
        setUrl(request.getRequestURI());
        setMatrix(UriUtils.resolveMatrixByPath(getUrl()));

        setPathAttributes(new HashMap<>());
        setHeaders();
        setCookies();

        setSession(new ServletRouterSession(request));

        setQueries(queries(request.getQueryString()));
        setParams();
        setBody();

        setContentType();
        setResponseType();

        setRequestAttribute();

        super.setId(id);
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
        setHeaders(map);
    }

    private void setCookies() {
        var cookies = request.getCookies();
        List<HttpCookie> result = new ArrayList<>();

        if (cookies != null) {
            for (var cookie : cookies) {
                result.add(new ServletRouterCookie(cookie).getHttpCookie());
            }
        }
        setCookies(result);
    }

    private void setParams() {
        Map<String, List> map = new HashMap<>();
        var parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            var name = parameterNames.nextElement();
            var values = request.getParameterValues(name);
            map.put(name, Arrays.asList(values));
        }
        var paramsInBody = getParamsInBody();
        if (!paramsInBody.isEmpty()) {
            map.putAll(paramsInBody);
        }
        setParameters(map);
    }

    private Map<String, List> getParamsInBody() {
        var headers = getHeader();
        String type = MediaType.ANY.getValue();
        if (headers.getHeader(MediaType.CONTENT_TYPE) != null) {
            type = headers.getHeader(MediaType.CONTENT_TYPE);
        }
        if (MediaType.APPLICATION_FORM_URLENCODED.getValue().equals(type)) {
            try {
                RequestBody requestBody = new RequestBody(request.getReader());
                var queries = queries(requestBody.getContent().get(0));
                Map<String, List> map = new HashMap<>();
                queries.forEach(map::put);
                return map;
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return getMultipartParams();
    }

    private Map<String, List> getMultipartParams() {
        Map<String, List> map = new HashMap<>();
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

    private void processFormField(Map<String, List<FileItem>> items, Map<String, List> map) {
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
                    if (type != null) {
                        contentType = MediaType.valueOf(type);
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
            setInputStreamBody(request.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setContentType() {
        var respType = request.getContentType();
        if (respType != null) {
            setContentType(MediaType.of(respType));
        } else {
            setContentType(MediaType.ANY);
        }
    }

    private void setResponseType() {
        var respType = request.getHeader("Response-Type");
        MediaType mediaType = MediaType.ANY;
        if (respType != null) {
            mediaType = MediaType.of(respType);
        } else {
            var ext = FileNameUtils.getExtension(getUrl());
            if (ext != null && !ext.isBlank()) {
                mediaType = MediaType.getTypeByUriExt(ext);
            }
        }
        setResponseType(mediaType);
    }

    private void setRequestAttribute() {
        Enumeration<String> attributeNames = request.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            var name = attributeNames.nextElement();
            super.addAttribute(name, request.getAttribute(name));
        }
    }

    @Override
    public String getRealPath(String path) {
        return request.getServletContext().getRealPath(path);
    }

    @Override
    public String getContextPath() {
        return request.getContextPath();
    }

    @Override
    public ServletRouterRequest copy() {
        return new ServletRouterRequest(super.getId(), request);
    }
}
