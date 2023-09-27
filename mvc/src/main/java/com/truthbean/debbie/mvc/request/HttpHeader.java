/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mvc.request;

import com.truthbean.debbie.io.FileNameUtils;
import com.truthbean.debbie.io.MediaType;
import com.truthbean.debbie.io.MediaTypeInfo;

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
        var value =  headers.get(name);
        if (value == null) {
            return headers.get(name.toLowerCase());
        }
        return value;
    }

    public String getHeader(HttpHeaderName headerName) {
        return getHeader(headerName.getName());
    }

    public String getHeader(String name) {
        var values = headers.get(name);
        if (values == null) {
            values = headers.get(name.toLowerCase());
        }
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

    public void addHeader(String name, String value) {
        if (name == null || name.isBlank()) return;

        var values = headers.get(name);
        if (values == null || values.isEmpty()) {
            values = new ArrayList<>();
        }
        values.add(value);
        this.headers.put(name, values);
    }

    public HttpHeader copy() {
        HttpHeader httpHeader = new HttpHeader();
        httpHeader.headers.putAll(this.headers);
        return httpHeader;
    }

    public boolean isEmpty() {
        return headers.isEmpty();
    }

    public MediaTypeInfo getMediaTypeFromHeaders(String name, String url) {
        String headerValue = getHeader(name);
        MediaTypeInfo type;
        if (headerValue != null && !headerValue.isBlank()) {
            type = MediaTypeInfo.parse(headerValue);
        } else {
            String ext = FileNameUtils.getExtension(url);
            if (ext == null || ext.isBlank()) {
                type = MediaType.ANY.info();
            } else {
                type = MediaType.getTypeByUriExt(ext).info();
            }
        }
        return type;
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
            return "{\"name\":\"customize\",\"value\":\"" + name + "\"}";
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
        ACCEPT("Accept"),

        ACCEPT_CHARSET("Accept-Charset"),

        ACCEPT_FEATURES("Accept-Features"),

        ACCEPT_ENCODING("Accept-Encoding"),

        ACCEPT_LANGUAGE("Accept-Language"),

        ACCEPT_RANGES("Accept-Ranges"),

        /**
         * https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Access_control_CORS
         */

        ACCESS_CONTROL_ALLOW_CREDENTIALS("Access-Control-Allow-Credentials"),

        ACCESS_CONTROL_ALLOW_ORIGIN("Access-Control-Allow-Origin"),

        ACCESS_CONTROL_ALLOW_METHODS("Access-Control-Allow-Methods"),

        ACCESS_CONTROL_ALLOW_HEADERS("Access-Control-Allow-Headers"),

        ACCESS_CONTROL_MAX_AGE("Access-Control-Max-Age"),

        ACCESS_CONTROL_EXPOSE_HEADERS("Access-Control-Expose-Headers"),

        ACCESS_CONTROL_REQUEST_METHOD("Access-Control-Request-Method"),

        ACCESS_CONTROL_REQUEST_HEADERS("Access-Control-Request-Headers"),


        AGE("Age"),

        ALLOW("Allow"),

        Alt_Svc("Alt-Svc"),

        ALTERNATES("Alternates"),

        AUTHORIZATION("Authorization"),

        /**
         * https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Caching_FAQ
         */
        CACHE_CONTROL("Cache-Control"),

        CLEAR_SITE_DATA("Clear-Site-Data"),

        CONNECTION("Connection"),

        CONTENT_ENCODING("Content-Encoding"),

        CONTENT_LANGUAGE("Content-Language"),

        CONTENT_LENGTH("Content-Length"),

        CONTENT_LOCATION("Content-Location"),

        CONTENT_RANGE("Content-Range"),

        /**
         * https://developer.mozilla.org/zh-CN/docs/Web/HTTP/CSP
         */
        CONTENT_SECURITY_POLICY("Content-Security-Policy"),

        /**
         * https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Headers/Content-Security-Policy-Report-Only
         */
        CONTENT_SECURITY_POLICY_REPORT_ONLY("Content-Security-Policy-Report-Only"),

        CONTENT_TYPE("Content-Type"),

        COOKIE("Cookie"),

        /**
         * 设置该值为1， 表明用户明确退出任何形式的网上跟踪
         */
        DNT("DNT"),

        DPR("DPR"),

        DATE("Date"),

        DIGEST("Digest"),

        ETAG("ETag"),

        EXPECT("Expect"),

        /**
         * https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Headers/Expect-CT
         */
        EXPECT_CT("Expect-CT"),

        EXPIRES("Expires"),

        /**
         * https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Headers/Forwarded
         */
        FORWARDED("Forwarded"),

        FROM("From"),

        HOST("Host"),

        IF_MATCH("If-Match"),

        IF_MODIFIED_SINCE("If-Modified-Since"),

        IF_NONE_MATCH("If-None-Match"),

        IF_RANGE("If-Range"),

        IF_UNMODIFIED_SINCE("If-Unmodified-Since"),

        /**
         * 给出服务器在先前HTTP连接上接收的最后事件的ID.
         * 用于同步文本/事件流
         */
        LAST_EVENT_ID("Last-Event-Id"),

        LAST_MODIFIED("Last-Modified"),

        /**
         * 等同于HTML标签中的"link"，但它是在HTTP层上，给出一个与获取的资源相关的URL以及关系的种类
         */
        LINK("Link"),

        LOCATION("Location"),

        MAX_FORWARDS("Max-Forwards"),

        NEGOTIATE("Negotiate"),

        ORIGIN("Origin"),

        PRAGMA("Pragma"),

        PROXY_AUTHENTICATE("Proxy-Authenticate"),

        PROXY_AUTHORIZATION("Proxy-Authorization"),

        /**
         * https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Headers/Public-Key-Pins
         */
        PUBLIC_KEY_PINS("Public-Key-Pins"),

        PUBLIC_KEY_PINS_REPORT_ONLY("Public-Key-Pins-Report-Only"),

        RANGE("Range"),

        /**
         * （请注意，在HTTP / 0.9规范中引入的正交错误必须在协议的后续版本中保留）
         */
        REFERER("Referer"),

        RETRY_AFTER("Retry-After"),

        SAVE_DATA("Save-Data"),

        SEC_WEBSOCKET_EXTENSIONS("Sec-WebSocket-Extensions"),

        SEC_WEBSOCKET_KEY("Sec-WebSocket-Key"),

        SEC_WEBSOCKET_ORIGIN("Sec-WebSocket-Origin"),

        SEC_WEBSOCKET_PROTOCOL("Sec-WebSocket-Protocol"),

        SEC_WEBSOCKET_VERSION("Sec-WebSocket-Version"),

        SERVER("Server"),

        SET_COOKIE("Set-Cookie"),

        @Deprecated
        SET_COOKIE2("Set-Cookie2"),

        /**
         * https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Headers/SourceMap
         */
        SOURCEMAP("SourceMap"),

        /**
         * https://developer.mozilla.org/zh-CN/docs/Security/HTTP_Strict_Transport_Security
         */
        STRICT_TRANSPORT_SECURITY("strict-transport-security"),

        /**
         * https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Content_negotiation
         */
        TCN("TCN"),

        /**
         * https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Headers/TE
         */
        TE("TE"),

        /**
         * https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Headers/Tk
         */
        TK("Tk"),

        /**
         * https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Headers/Timing-Allow-Origin
         */
        Timing_Allow_Origin("Timing-Allow-Origin"),

        /**
         * 列出将在消息正文之后在尾部块中传输的头。这允许服务器计算一些值，如Content-MD5：在传输数据时。
         * 请注意，Trailer：标头不得列出Content-Length :, Trailer：或Transfer-Encoding：headers。
         */
        TRAILER("Trailer"),

        TRANSFER_ENCODING("Transfer-Encoding"),

        UPGRADE("Upgrade"),

        /**
         * https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Headers/Upgrade-Insecure-Requests
         */
        UPGRADE_INSECURE_REQUESTS("Upgrade-Insecure-Requests"),

        /**
         * https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Headers/User-Agent/Firefox
         */
        USER_AGENT("User-Agent"),

        VARIANT_VARY("Variant-Vary"),

        /**
         * 列出了用作Web服务器选择特定内容的条件的标头。
         * 此服务器对于高效和正确缓存发送的资源很重要。
         */
        VARY("Vary"),

        VIA("Via"),

        WARNING("Warning"),

        /**
         * https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Headers/WWW-Authenticate
         */
        WWW_AUTHENTICATE("WWW-Authenticate"),

        WANT_DIGEST("Want-Digest"),

        X_CONTENT_DURATION("X-Content-Duration"),

        X_CONTENT_SECURITY_POLICY("X-Content-Security-Policy"),

        /**
         * https://developer.mozilla.org/zh-CN/docs/Controlling_DNS_prefetching
         */
        X_DNS_PREFETCH_CONTROL("X-DNS-Prefetch-Control"),

        X_FRAME_OPTIONS("X-Frame-Options"),

        X_REQUESTED_WITH("X-Requested-With"),

        /**
         * https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Headers/X-XSS-Protection
         */
        X_XSS_PROTECTION("X-XSS-Protection"),

        CUSTOM(""),

        ;

        private final String name;

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
