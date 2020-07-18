/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.aio;

import com.truthbean.debbie.io.FileNameUtils;
import com.truthbean.debbie.io.MediaType;
import com.truthbean.debbie.io.MediaTypeInfo;
import com.truthbean.debbie.io.MultipartFile;
import com.truthbean.debbie.mvc.RouterSession;
import com.truthbean.debbie.mvc.request.DefaultRouterRequest;
import com.truthbean.debbie.mvc.request.HttpHeader;
import com.truthbean.debbie.mvc.request.HttpMethod;
import com.truthbean.debbie.mvc.request.RouterRequest;
import com.truthbean.debbie.net.uri.QueryStringDecoder;
import com.truthbean.debbie.net.uri.UriUtils;
import com.truthbean.debbie.util.Constants;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.HttpCookie;
import java.net.SocketAddress;
import java.nio.charset.Charset;
import java.util.*;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.0.2
 * Created on 2019-12-17 19:09
 */
public class RawRequestWrapper implements RouterRequest {

    private final List<String> raw;

    private final DefaultRouterRequest routerRequestCache;

    RawRequestWrapper(List<String> rawRequest, SocketAddress remoteAddress) {
        this.raw = rawRequest;

        this.routerRequestCache = new DefaultRouterRequest();

        String id = UUID.randomUUID().toString();
        this.routerRequestCache.setId(id);

        var first = raw.get(0);
        String[] word = first.split(Constants.SPACE);
        var url = word[1];

        var decoder = new QueryStringDecoder(url);

        if (decoder.hasParams()) {
            this.routerRequestCache.addQueries(decoder.parameters());;
        }
        this.routerRequestCache.setUrl(decoder.path());
        Map<String, List<String>> matrix = UriUtils.resolveMatrixByPath(getUrl());
        this.routerRequestCache.setMatrix(matrix);

        var requestMethod = word[0];
        this.routerRequestCache.setMethod(HttpMethod.valueOf(requestMethod));

        setRequestResult();
        this.routerRequestCache.setRemoteAddress(remoteAddress.toString());
    }

    @Override
    public String getId() {
        return this.routerRequestCache.getId();
    }

    @Override
    public HttpMethod getMethod() {
        return this.routerRequestCache.getMethod();
    }

    @Override
    public String getUrl() {
        return this.routerRequestCache.getUrl();
    }

    @Override
    public void addAttribute(String name, Object value) {
        this.routerRequestCache.addAttribute(name, value);
    }

    @Override
    public void removeAttribute(String name) {
        this.routerRequestCache.removeAttribute(name);
    }

    @Override
    public Object getAttribute(String name) {
        return this.routerRequestCache.getAttribute(name);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.routerRequestCache.getAttributes();
    }

    @Override
    public Map<String, List<String>> getPathAttributes() {
        return this.routerRequestCache.getPathAttributes();
    }

    @Override
    public void setPathAttributes(Map<String, List<String>> map) {
        this.routerRequestCache.setPathAttributes(map);
    }

    @Override
    public Map<String, List<String>> getMatrix() {
        return this.routerRequestCache.getMatrix();
    }

    @Override
    public HttpHeader getHeader() {
        return this.routerRequestCache.getHeader();
    }

    private String boundary = "";
    private boolean isMultipartFormData = false;

    private List<String> headersAndGetContent() {
        List<String> content = new ArrayList<>();

        boolean headerOver = false;
        for (int i = 1; i < raw.size(); i++) {
            var line = raw.get(i);

            if (!headerOver) {
                if (line.contains(Constants.COLON) && !line.startsWith(Constants.FORM_DATA)) {
                    String[] list = line.split(Constants.COLON);
                    var key = list[0];
                    String value = "";
                    if (list.length == 2)
                        value = list[1];
                    this.routerRequestCache.addHeader(key.trim(), value.trim());

                    var prefix = "Content-Type: multipart/form-data; boundary=";
                    if (line.startsWith(prefix)) {
                        boundary = "--" + line.substring(prefix.length());
                    }
                    continue;
                } else {
                    headerOver = true;
                }
            } else {
                content.add(line);
            }
        }
        return content;
    }

    private void setRequestResult() {
        Map<String, List> params = new HashMap<>();

        MediaTypeInfo responseType = MediaType.ANY.info();

        //=========================================================

        String paramName = null;
        var isSpace = false;
        var hasValue = false;
        // var isText = true;
        // byte[] content = null
        MultipartFile multipartFile = null;

        var textContent = new StringBuilder();
        var rawContent = new StringBuilder();

        List<String> content = headersAndGetContent();

        HttpHeader header = this.routerRequestCache.getHeader();
        if (header == null) {
            header = new HttpHeader();
        }

        responseType = header.getMediaTypeFromHeaders(MediaType.RESPONSE_TYPE, getUrl());
        this.routerRequestCache.setResponseType(responseType);

        MediaTypeInfo contentType = header.getMediaTypeFromHeaders(MediaType.CONTENT_TYPE, getUrl());
        this.routerRequestCache.setContentType(contentType);

        if (!content.isEmpty()) {
            for (String line : content) {
                if (line.equals(boundary) || "$boundary--".equals(line)) {
                    isMultipartFormData = true;
                    continue;
                }

                //when httpRequest headers is Content-Type: application/x-www-form-urlencoded
                //params will be key=key&value=%E5%91%B5%E5%91%B5%E5%91%B5%E5%91%B5%E5%91%B5
                if (MediaType.APPLICATION_FORM_URLENCODED.isSame(contentType)) {
                    var decoder = new QueryStringDecoder("/?" + line);
                    if (decoder.hasParams()) {
                        decoder.parameters().forEach(params::put);
                        continue;
                    }
                }

            /*if (contentType != null && contentType.isText()) {
                this.routerRequestCache.setTextBody(line);
            }*/

                final var split = Constants.SEMICOLON + Constants.SPACE;
                if (line.startsWith(Constants.FORM_DATA) && line.contains(split)) {
                    var names = line.split(split);
                    paramName = names[1].split("\"")[1];
                    if (names.length == 3) {
                        //isText = false
                        multipartFile = new MultipartFile();
                        String filename = names[2].split(Constants.EQUAL_MARK)[1].replace("\"", "");
                        multipartFile.setFileName(filename);
                        multipartFile.setFileExt(FileNameUtils.getExtension(multipartFile.getFileName()));
                    }
                    continue;
                }

                if (multipartFile != null) {
                    /*if (line.startsWith(ResponseType.Constants.RESPONSE_TYPE)) {
                        println(ResponseType.Constants.make(line))
                    } else {
                        println(ResponseType.Constants.guess(line))
                        if (multipartFile.contentType == null
                                || multipartFile.contentType == ResponseType.APPLICATION_OCTET_STREAM) {
                            multipartFile.contentType = ResponseType.Constants.guess(line)
                        }
                    }
                    continue;
                    */
                }

                if (multipartFile != null || (contentType != null && contentType.isText())) {
                    textContent.append(line);
                    continue;
                } else {
                    rawContent.append(line);
                }

                if (Constants.EMPTY_STRING.equals(line)) {
                    isSpace = true;
                    hasValue = true;
                    continue;
                }
//            if (isSpace && !hasValue) {
//                hasValue = true
//                isSpace = false
//                continue
//            }

                if (hasValue && paramName != null) {
                    hasValue = false;
                    List list;
                    if (params.containsKey(paramName)) {
                        list = params.get(paramName);
                        list.add(line);
                    } else {
                        list = new ArrayList();
                        list.add(line);
                    }
                    params.put(paramName, list);
                    paramName = null;
                    continue;
                }
            }
        }

//                if (multipartFile != null && content != null) {
//                    multipartFile.content = content
//                }

        if (multipartFile != null) {
            multipartFile.setContent(textContent.toString().getBytes());
            textContent = new StringBuilder();
            multipartFile.setContentType(contentType.toMediaType());

            if (paramName != null) {
                List list;
                if (params.containsKey(paramName)) {
                    list = params.get(paramName);
                    list.add(multipartFile);
                } else {
                    list = new ArrayList();
                    list.add(multipartFile);
                }
                params.put(paramName, list);
            }

        }

        if (!"".equals(textContent.toString().trim())) {
            this.routerRequestCache.setTextBody(textContent.toString());
        }

        if (!"".equals(rawContent.toString().trim())) {
            this.routerRequestCache.setInputStreamBody(new ByteArrayInputStream(rawContent.toString().getBytes()));
        }

        this.routerRequestCache.setParameters(params);
    }

    @Override
    public List<HttpCookie> getCookies() {
        return this.routerRequestCache.getCookies();
    }

    @Override
    public RouterSession getSession() {
        return this.routerRequestCache.getSession();
    }

    @Override
    public Map<String, List> getParameters() {
        return this.routerRequestCache.getParameters();
    }

    @Override
    public Object getParameter(String name) {
        return this.routerRequestCache.getParameter(name);
    }

    @Override
    public Map<String, List<String>> getQueries() {
        return this.routerRequestCache.getQueries();
    }

    @Override
    public InputStream getInputStreamBody() {
        return this.routerRequestCache.getInputStreamBody();
    }

    @Override
    public MediaTypeInfo getContentType() {
        return this.routerRequestCache.getContentType();
    }

    @Override
    public MediaTypeInfo getResponseType() {
        return this.routerRequestCache.getResponseType();
    }

    @Override
    public String getRealPath(String path) {
        return this.routerRequestCache.getRealPath(path);
    }

    @Override
    public String getContextPath() {
        return this.routerRequestCache.getContextPath();
    }

    @Override
    public String getTextBody() {
        return this.routerRequestCache.getTextBody();
    }

    @Override
    public File getFileBody() {
        return this.routerRequestCache.getFileBody();
    }

    @Override
    public RouterRequest copy() {
        return this.routerRequestCache.copy();
    }

    @Override
    public void setCharacterEncoding(Charset charset) {
        this.routerRequestCache.setCharacterEncoding(charset);
    }

    @Override
    public String getRemoteAddress() {
        return this.routerRequestCache.getRemoteAddress();
    }
}
