package com.truthbean.debbie.mvc.request;

import java.util.*;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class HttpHeader {
    private final Map<String, List<String>> headers = new HashMap<>();

    public Map<String, List<String>> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }

    public List<String> getHeaders(String name) {
        return headers.get(name);
    }

    public String getHeader(String name) {
        var values = headers.get(name);
        if (values != null && !values.isEmpty()) {
            return values.get(0);
        }
        return null;
    }

    public void addHeaders(Map<String, List<String>> headers) {
        this.headers.putAll(headers);
    }

    public void addHeader(String name, List<String> value) {
        this.headers.put(name, value);
    }

    public HttpHeader copy() {
        HttpHeader httpHeader = new HttpHeader();
        httpHeader.headers.putAll(this.headers);
        return httpHeader;
    }

    public boolean isEmpty() {
        return headers.isEmpty();
    }

    @Override
    public String toString() {
        return headers.toString();
    }

    public interface HttpHeaderName {

        String getName();
    }

    private static class CustomHttpHeaderName implements HttpHeaderName {

        private String name;

        @Override
        public String getName() {
            return name;
        }

        static HttpHeaderName name(String name) {
            CustomHttpHeaderName httpHeaderName = new CustomHttpHeaderName();
            httpHeaderName.name = name;
            return httpHeaderName;
        }

        @Override
        public String toString() {
            return "{\"name\":\"custom\",\"value\":\"" + name + "\"}";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof CustomHttpHeaderName)) return false;
            CustomHttpHeaderName that = (CustomHttpHeaderName) o;
            return Objects.equals(getName(), that.getName());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getName());
        }
    }

    /**
     * https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Headers
     * <p>
     * http header
     */
    public enum HttpHeaderNames implements HttpHeaderName {

        /**
         * https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Content_negotiation
         */
        ACCEPT("accept"),

        ACCEPT_CHARSET("accept-charset"),

        ACCEPT_FEATURES("accept-features"),

        ACCEPT_ENCODING("accept-encoding"),

        ACCEPT_LANGUAGE("accept-language"),

        ACCEPT_RANGES("accept-ranges"),

        /**
         * https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Access_control_CORS
         */

        ACCESS_CONTROL_ALLOW_CREDENTIALS("access-control-allow-credentials"),

        ACCESS_CONTROL_ALLOW_ORIGIN("access-control-allow-origin"),

        ACCESS_CONTROL_ALLOW_METHODS("access-control-allow-methods"),

        ACCESS_CONTROL_ALLOW_HEADERS("access-control-allow-headers"),

        ACCESS_CONTROL_MAX_AGE("access-control-max-age"),

        ACCESS_CONTROL_EXPOSE_HEADERS("access-control-expose-headers"),

        ACCESS_CONTROL_REQUEST_METHOD("access-control-request-method"),

        ACCESS_CONTROL_REQUEST_HEADERS("access-control-request-headers"),


        AGE("age"),

        ALLOW("allow"),

        ALTERNATES("alternates"),

        AUTHORIZATION("authorization"),

        /**
         * https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Caching_FAQ
         */
        CACHE_CONTROL("cache-control"),

        CONNECTION("connection"),

        CONTENT_ENCODING("content-encoding"),

        CONTENT_LANGUAGE("content-language"),

        CONTENT_LENGTH("content-length"),

        CONTENT_LOCATION("content-location"),

        CONTENT_RANGE("content-range"),

        /**
         * https://developer.mozilla.org/zh-CN/docs/Web/HTTP/CSP
         */
        CONTENT_SECURITY_POLICY("content-security-policy"),

        CONTENT_TYPE("content-type"),

        COOKIE("cookie"),

        /**
         * 设置该值为1， 表明用户明确退出任何形式的网上跟踪
         */
        DNT("dnt"),

        DATE("date"),

        ETAG("etag"),

        EXPECT("expect"),

        EXPIRES("expires"),

        FROM("from"),

        HOST("host"),

        IF_MATCH("if-match"),

        IF_MODIFIED_SINCE("if-modified-since"),

        IF_NONE_MATCH("if-none-match"),

        IF_RANGE("if-range"),

        IF_UNMODIFIED_SINCE("if-unmodified-since"),

        /**
         * 给出服务器在先前HTTP连接上接收的最后事件的ID.
         * 用于同步文本/事件流
         */
        LAST_EVENT_ID("last-event-id"),

        LAST_MODIFIED("last-modified"),

        /**
         * 等同于HTML标签中的"link"，但它是在HTTP层上，给出一个与获取的资源相关的URL以及关系的种类
         */
        LINK("link"),

        LOCATION("location"),

        MAX_FORWARDS("max-forwards"),

        NEGOTIATE("negotiate"),

        ORIGIN("origin"),

        PRAGMA("pragma"),

        PROXY_AUTHENTICATE("proxy-authenticate"),

        PROXY_AUTHORIZATION("proxy-authorization"),

        RANGE("range"),

        /**
         * （请注意，在HTTP / 0.9规范中引入的正交错误必须在协议的后续版本中保留）
         */
        REFERER("referer"),

        RETRY_AFTER("retry-after"),

        SEC_WEBSOCKET_EXTENSIONS("sec-websocket-extensions"),

        SEC_WEBSOCKET_KEY("sec-websocket-key"),

        SEC_WEBSOCKET_ORIGIN("sec-websocket-origin"),

        SEC_WEBSOCKET_PROTOCOL("sec-websocket-protocol"),

        SEC_WEBSOCKET_VERSION("sec-websocket-version"),

        SERVER("server"),

        SET_COOKIE("set-cookie"),

        SET_COOKIE2("set-cookie2"),

        /**
         * https://developer.mozilla.org/zh-CN/docs/Security/HTTP_Strict_Transport_Security
         */
        STRICT_TRANSPORT_SECURITY("strict-transport-security"),

        /**
         * https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Content_negotiation
         */
        TCN("tcn"),

        TE("te"),

        /**
         * 列出将在消息正文之后在尾部块中传输的头。这允许服务器计算一些值，如Content-MD5：在传输数据时。
         * 请注意，Trailer：标头不得列出Content-Length :, Trailer：或Transfer-Encoding：headers。
         */
        TRAILER("trailer"),

        TRANSFER_ENCODING("transfer-encoding"),

        UPGRADE("upgrade"),

        /**
         * https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Headers/User-Agent/Firefox
         */
        USER_AGENT("user-agent"),

        VARIANT_VARY("variant-vary"),

        /**
         * 列出了用作Web服务器选择特定内容的条件的标头。
         * 此服务器对于高效和正确缓存发送的资源很重要。
         */
        VARY("vary"),

        VIA("via"),

        WARNING("warning"),

        WWW_AUTHENTICATE("www-authenticate"),

        X_CONTENT_DURATION("x-content-duration"),

        X_CONTENT_SECURITY_POLICY("x-content-security-policy"),

        X_DNSPREFETCH_CONTROL("x-dnsprefetch-control"),

        X_FRAME_OPTIONS("x-frame-options"),

        X_REQUESTED_WITH("x-requested-with"),

        CUSTOM(""),

        ;

        private String name;

        HttpHeaderNames(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        public static HttpHeaderName of(String name) {
            HttpHeaderName[] values = values();
            for (HttpHeaderName value : values) {
                if (name.equalsIgnoreCase(value.getName())) {
                    return value;
                }
            }

            return CustomHttpHeaderName.name(name);
        }

        @Override
        public String toString() {
            return "{\"name\":\"" + super.toString() + "\",\"value\":\"" + name + "\"}";
        }
    }
}
