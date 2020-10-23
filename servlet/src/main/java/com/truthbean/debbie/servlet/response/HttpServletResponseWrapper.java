/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.servlet.response;

import com.truthbean.debbie.mvc.response.RouterResponse;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class HttpServletResponseWrapper implements HttpServletResponse {
    private HttpServletResponse response;
    private final RouterResponse routerResponse;

    public HttpServletResponseWrapper(HttpServletResponse response) {
        this.response = response;
        this.routerResponse = new RouterResponse();
    }

    public HttpServletResponseWrapper(RouterResponse copy) {
        this.routerResponse = new RouterResponse();
        this.routerResponse.copyFrom(copy);
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public RouterResponse getRouterResponse() {
        return routerResponse;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }

    @Override
    public void addCookie(Cookie cookie) {
        response.addCookie(cookie);
    }

    @Override
    public boolean containsHeader(String name) {
        return response.containsHeader(name);
    }

    @Override
    public String encodeURL(String url) {
        return response.encodeURL(url);
    }

    @Override
    public String encodeRedirectURL(String url) {
        return response.encodeRedirectURL(url);
    }

    @Override
    public String encodeUrl(String url) {
        return response.encodeURL(url);
    }

    @Override
    public String encodeRedirectUrl(String url) {
        return response.encodeRedirectURL(url);
    }

    @Override
    public void sendError(int sc, String msg) throws IOException {
        response.sendError(sc, msg);
    }

    @Override
    public void sendError(int sc) throws IOException {
        response.sendError(sc);
    }

    @Override
    public void sendRedirect(String location) throws IOException {
        response.sendRedirect(location);
    }

    @Override
    public void setDateHeader(String name, long date) {
        response.setDateHeader(name, date);
    }

    @Override
    public void addDateHeader(String name, long date) {
        response.addDateHeader(name, date);
    }

    @Override
    public void setHeader(String name, String value) {
        response.setHeader(name, value);
    }

    @Override
    public void addHeader(String name, String value) {
        response.addHeader(name, value);
    }

    @Override
    public void setIntHeader(String name, int value) {
        response.setIntHeader(name, value);
    }

    @Override
    public void addIntHeader(String name, int value) {
        response.addIntHeader(name, value);
    }

    @Override
    public void setStatus(int sc) {
        response.setStatus(sc);
    }

    @Override
    public void setStatus(int sc, String sm) {
        response.setStatus(sc);
    }

    @Override
    public int getStatus() {
        return response.getStatus();
    }

    @Override
    public String getHeader(String name) {
        return response.getHeader(name);
    }

    @Override
    public Collection<String> getHeaders(String name) {
        return response.getHeaders(name);
    }

    @Override
    public Collection<String> getHeaderNames() {
        return response.getHeaderNames();
    }

    @Override
    public void setTrailerFields(Supplier<Map<String, String>> supplier) {
        response.setTrailerFields(supplier);
    }

    @Override
    public Supplier<Map<String, String>> getTrailerFields() {
        return response.getTrailerFields();
    }

    @Override
    public String getCharacterEncoding() {
        return response.getCharacterEncoding();
    }

    @Override
    public String getContentType() {
        return response.getContentType();
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return response.getOutputStream();
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return response.getWriter();
    }

    @Override
    public void setCharacterEncoding(String charset) {
        response.setCharacterEncoding(charset);
    }

    @Override
    public void setContentLength(int len) {
        response.setContentLength(len);
    }

    @Override
    public void setContentLengthLong(long len) {
        response.setContentLengthLong(len);
    }

    @Override
    public void setContentType(String type) {
        response.setContentType(type);
    }

    @Override
    public void setBufferSize(int size) {
        response.setBufferSize(size);
    }

    @Override
    public int getBufferSize() {
        return response.getBufferSize();
    }

    @Override
    public void flushBuffer() throws IOException {
        response.flushBuffer();
    }

    @Override
    public void resetBuffer() {
        response.resetBuffer();
    }

    @Override
    public boolean isCommitted() {
        return response.isCommitted();
    }

    @Override
    public void reset() {
        response.reset();
    }

    @Override
    public void setLocale(Locale loc) {
        response.setLocale(loc);
    }

    @Override
    public Locale getLocale() {
        return response.getLocale();
    }
}
