package com.truthbean.debbie.netty;

import com.truthbean.debbie.core.io.FileNameUtils;
import com.truthbean.debbie.core.io.MediaType;
import com.truthbean.debbie.core.net.uri.UriComposition;
import com.truthbean.debbie.core.net.uri.UriPathFragment;
import com.truthbean.debbie.core.net.uri.UriUtils;
import com.truthbean.debbie.mvc.RouterSession;
import com.truthbean.debbie.mvc.request.DefaultRouterRequest;
import com.truthbean.debbie.mvc.request.HttpHeader;
import com.truthbean.debbie.mvc.request.HttpMethod;
import com.truthbean.debbie.mvc.request.RouterRequest;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.net.HttpCookie;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/05/02 22:37.
 */
public class NettyRouterRequest implements RouterRequest {

    private String host;
    private int port;
    private String id;
    private io.netty.handler.codec.http.HttpRequest httpRequest;

    private final DefaultRouterRequest routerRequestCache;

    public NettyRouterRequest(io.netty.handler.codec.http.HttpRequest httpRequest, String host, int port) {
        this(UUID.randomUUID().toString(), httpRequest, host, port);
    }

    public NettyRouterRequest(String id, io.netty.handler.codec.http.HttpRequest httpRequest, String host, int port) {
        this.httpRequest = httpRequest;
        this.routerRequestCache = new DefaultRouterRequest();
        this.id = id;
        this.routerRequestCache.setId(id);

        this.host = host;
        this.port = port;

        io.netty.handler.codec.http.HttpMethod httpMethod = httpRequest.method();
        this.routerRequestCache.setMethod(com.truthbean.debbie.mvc.request.HttpMethod.valueOf(httpMethod.name()));

        String uri = httpRequest.uri();
        LOGGER.debug("url ..... " + uri);

        String rawUrl = "http://" + host + ":" + port + uri;
        URL url;
        try {
            url = new URL(rawUrl);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        UriComposition composition = UriUtils.resolveUrl(url);
        assert composition != null;

        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(uri);
        Map<String, List<String>> queries = queryStringDecoder.parameters();
        this.routerRequestCache.setQueries(queries);

        uri = UriUtils.getPaths(uri);
        this.routerRequestCache.setUrl(uri);
        this.routerRequestCache.setPathAttributes(new HashMap<>());

        this.routerRequestCache.setMatrix(composition.getMatrix());

        HttpHeaders httpHeaders = httpRequest.headers();
        Map<String, List<String>> headers = new HashMap<>();
        httpHeaders.names().forEach(name -> headers.put(name, httpHeaders.getAll(name)));
        this.routerRequestCache.setHeaders(headers);

        var contentType = getMediaTypeFromHeaders(httpHeaders, "Content-Type");
        routerRequestCache.setContentType(contentType);
        var responseType = getMediaTypeFromHeaders(httpHeaders, "Response-Type");
        routerRequestCache.setResponseType(responseType);

        setCookies(httpHeaders);
    }

    public void setParameters(HttpHeaders trailingHeaders) {
        if (trailingHeaders.isEmpty()) {
            for (CharSequence name : trailingHeaders.names()) {
                this.routerRequestCache.addParameters((String) name, trailingHeaders.getAll(name));
            }
        }
    }

    public void setInputStreamBody(ByteBuf content) {
        ByteBufInputStream byteBufInputStream = new ByteBufInputStream(content);
        this.routerRequestCache.setInputStreamBody(byteBufInputStream);
    }

    private void setCookies(HttpHeaders httpHeaders) {
        String cookieString = httpHeaders.get(HttpHeaderNames.COOKIE);
        if (cookieString != null) {
            Set<Cookie> cookies = ServerCookieDecoder.STRICT.decode(cookieString);
            if (!cookies.isEmpty()) {
                for (Cookie cookie : cookies) {
                    HttpCookie httpCookie = new HttpCookie(cookie.name(), cookie.value());
                    httpCookie.setDomain(cookie.domain());
                    httpCookie.setMaxAge(cookie.maxAge());
                    httpCookie.setPath(cookie.path());
                    httpCookie.setSecure(cookie.isSecure());
                    httpCookie.setHttpOnly(cookie.isHttpOnly());
                    this.routerRequestCache.addCookie(httpCookie);
                }
            }
        }
        if (this.routerRequestCache.getCookies() == null) {
            this.routerRequestCache.setCookies(new ArrayList<>());
        }
    }

    private MediaType getMediaTypeFromHeaders(HttpHeaders httpHeaders, String name) {
        String headerValue = httpHeaders.get(name);
        MediaType type;
        if (headerValue != null && !headerValue.isBlank()) {
            type = MediaType.of(headerValue);
        } else {
            String ext = FileNameUtils.getExtension(getUrl());
            if (ext == null || ext.isBlank()) {
                type = MediaType.ANY;
            } else {
                type = MediaType.getTypeByUriExt(ext);
            }
        }
        return type;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public HttpMethod getMethod() {
        return routerRequestCache.getMethod();
    }

    @Override
    public String getUrl() {
        return routerRequestCache.getUrl();
    }

    @Override
    public void addAttribute(String name, Object value) {
        routerRequestCache.addAttribute(name, value);
    }

    @Override
    public void removeAttribute(String name) {
        routerRequestCache.removeAttribute(name);
    }

    @Override
    public Object getAttribute(String name) {
        return routerRequestCache.getAttribute(name);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return routerRequestCache.getAttributes();
    }

    @Override
    public Map<String, List<String>> getPathAttributes() {
        return routerRequestCache.getPathAttributes();
    }

    @Override
    public Map<String, List<String>> getMatrix() {
        return routerRequestCache.getMatrix();
    }

    @Override
    public HttpHeader getHeader() {
        return routerRequestCache.getHeader();
    }

    @Override
    public List<HttpCookie> getCookies() {
        return routerRequestCache.getCookies();
    }

    @Override
    public RouterSession getSession() {
        var session = routerRequestCache.getSession();
        if (session == null) {
            // todo
            /*try {
                session = new UndertowRouterSession(exchange);
            } catch (Throwable throwable) {
                LOGGER.warn("this request has no session");
            }
            routerRequestCache.setSession(session);*/
        }
        return session;
    }

    @Override
    public Map<String, List> getParameters() {
        return routerRequestCache.getParameters();
    }

    @Override
    public Object getParameter(String name) {
        return routerRequestCache.getParameter(name);
    }

    @Override
    public Map<String, List<String>> getQueries() {
        return routerRequestCache.getQueries();
    }

    @Override
    public InputStream getInputStreamBody() {
        return routerRequestCache.getInputStreamBody();
    }

    @Override
    public MediaType getContentType() {
        return routerRequestCache.getContentType();
    }

    @Override
    public MediaType getResponseType() {
        return routerRequestCache.getResponseType();
    }

    @Override
    public String getRealPath(String path) {
        return null;
    }

    @Override
    public String getContextPath() {
        return null;
    }

    @Override
    public String getTextBody() {
        return null;
    }

    @Override
    public File getFileBody() {
        return null;
    }

    @Override
    public RouterRequest copy() {
        return new NettyRouterRequest(id, httpRequest, host, port);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyRouterRequest.class);
}
