/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.truthbean.debbie.net.uri;

import com.truthbean.debbie.util.Constants;
import com.truthbean.core.util.StringUtils;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.util.*;

/**
 * Splits an HTTP query string into a path string and key-value parameter pairs.
 * This decoder is for one time use only.  Create a new instance for each URI:
 * <pre>
 * {@link QueryStringDecoder} decoder = new {@link QueryStringDecoder}("/hello?recipient=world&amp;x=1;y=2");
 * assert decoder.path().equals("/hello");
 * assert decoder.parameters().get("recipient").get(0).equals("world");
 * assert decoder.parameters().get("x").get(0).equals("1");
 * assert decoder.parameters().get("y").get(0).equals("2");
 * </pre>
 *
 * This decoder can also decode the content of an HTTP POST request whose
 * content type is &lt;tt&gt;application/x-www-form-urlencoded&lt;/tt&gt;:
 * <pre>
 * {@link QueryStringDecoder} decoder = new {@link QueryStringDecoder}("recipient=world&amp;x=1;y=2", false);
 * ...
 * </pre>
 *
 * <h3>HashDOS vulnerability fix</h3>
 *
 * As a workaround to the <a href="http://netty.io/s/hashdos">HashDOS</a> vulnerability, the decoder
 * limits the maximum number of decoded key-value parameter pairs, up to {@literal 1024} by
 * default, and you can configure it when you construct the decoder by passing an additional
 * integer parameter.
 *
 * @see QueryStringEncoder
 */
public class QueryStringDecoder {

    private static final int DEFAULT_MAX_PARAMS = 1024;

    private final Charset charset;
    private final String uri;
    private final int maxParams;
    private int pathEndIdx;
    private String path;
    private Map<String, List<String>> params;

    /**
     * Creates a new decoder that decodes the specified URI. The decoder will
     * assume that the query string is encoded in UTF-8.
     * @param uri uri
     */
    public QueryStringDecoder(String uri) {
        this(uri, Constants.DEFAULT_CHARSET);
    }

    /**
     * Creates a new decoder that decodes the specified URI encoded in the
     * specified charset.
     * @param uri uri
     * @param hasPath has path
     */
    public QueryStringDecoder(String uri, boolean hasPath) {
        this(uri, Constants.DEFAULT_CHARSET, hasPath);
    }

    /**
     * Creates a new decoder that decodes the specified URI encoded in the
     * specified charset.
     * @param uri uri
     * @param charset charset
     */
    public QueryStringDecoder(String uri, Charset charset) {
        this(uri, charset, true);
    }

    /**
     * Creates a new decoder that decodes the specified URI encoded in the
     * specified charset.
     * @param uri uri
     * @param charset charset
     * @param hasPath has path
     */
    public QueryStringDecoder(String uri, Charset charset, boolean hasPath) {
        this(uri, charset, hasPath, DEFAULT_MAX_PARAMS);
    }

    /**
     * Creates a new decoder that decodes the specified URI encoded in the
     * specified charset.
     * @param uri uri
     * @param charset charset
     * @param hasPath has path
     * @param maxParams max params
     */
    public QueryStringDecoder(String uri, Charset charset, boolean hasPath, int maxParams) {
        this.uri = uri;
        this.charset = charset;
        // `-1` means that path end index will be initialized lazily
        pathEndIdx = hasPath ? -1 : 0;
        this.maxParams = maxParams;
    }

    /**
     * Creates a new decoder that decodes the specified URI. The decoder will
     * assume that the query string is encoded in UTF-8.
     * @param uri uri
     */
    public QueryStringDecoder(URI uri) {
        this(uri, Constants.DEFAULT_CHARSET);
    }

    /**
     * Creates a new decoder that decodes the specified URI encoded in the
     * specified charset.
     * @param uri uri
     * @param charset charset
     */
    public QueryStringDecoder(URI uri, Charset charset) {
        this(uri, charset, DEFAULT_MAX_PARAMS);
    }

    /**
     * Creates a new decoder that decodes the specified URI encoded in the
     * specified charset.
     * @param uri uri
     * @param charset charset
     * @param maxParams max params
     */
    public QueryStringDecoder(URI uri, Charset charset, int maxParams) {
        String rawPath = uri.getRawPath();
        if (rawPath == null) {
            rawPath = Constants.EMPTY_STRING;
        }
        String rawQuery = uri.getRawQuery();
        // Also take care of cut of things like "http://localhost"
        this.uri = rawQuery == null? rawPath : rawPath + '?' + rawQuery;
        pathEndIdx = rawPath.length();
        this.charset = charset;
        this.maxParams = maxParams;
    }

    @Override
    public String toString() {
        return uri();
    }

    /**
     * @return the uri used to initialize this {@link QueryStringDecoder}.
     */
    public String uri() {
        return uri;
    }

    /**
     * @return the decoded path string of the URI.
     */
    public String path(boolean ignoreEncode) {
        if (path == null) {
            path = decodeComponent(uri, 0, pathEndIdx(), charset, true, ignoreEncode);
        }
        return path;
    }

    /**
     * @return the decoded key-value parameter pairs of the URI.
     */
    public Map<String, List<String>> parameters() {
        if (params == null) {
            params = decodeParams(uri, pathEndIdx(), charset, maxParams);
        }
        return params;
    }

    public boolean hasParams() {
        params = parameters();
        return params != null && !params.isEmpty();
    }

    /**
     * @return the raw path string of the URI.
     */
    public String rawPath() {
        return uri.substring(0, pathEndIdx());
    }

    /**
     * @return raw query string of the URI.
     */
    public String rawQuery() {
        int start = pathEndIdx() + 1;
        return start < uri.length() ? uri.substring(start) : Constants.EMPTY_STRING;
    }

    private int pathEndIdx() {
        if (pathEndIdx == -1) {
            pathEndIdx = findPathEndIndex(uri);
        }
        return pathEndIdx;
    }

    private static Map<String, List<String>> decodeParams(String s, int from, Charset charset, int paramsLimit) {
        int len = s.length();
        if (from >= len) {
            return Collections.emptyMap();
        }
        if (s.charAt(from) == '?') {
            from++;
        }
        Map<String, List<String>> params = new LinkedHashMap<>();
        int nameStart = from;
        int valueStart = -1;
        int i;
        loop:
        for (i = from; i < len; i++) {
            switch (s.charAt(i)) {
            case '=':
                if (nameStart == i) {
                    nameStart = i + 1;
                } else if (valueStart < nameStart) {
                    valueStart = i + 1;
                }
                break;
            case '&':
                if (addParam(s, nameStart, valueStart, i, params, charset)) {
                    paramsLimit--;
                    if (paramsLimit == 0) {
                        return params;
                    }
                }
                nameStart = i + 1;
                break;
            case '#':
                break loop;
            default:
                // continue
            }
        }
        addParam(s, nameStart, valueStart, i, params, charset);
        return params;
    }

    private static boolean addParam(String s, int nameStart, int valueStart, int valueEnd,
                                    Map<String, List<String>> params, Charset charset) {
        if (nameStart >= valueEnd) {
            return false;
        }
        if (valueStart <= nameStart) {
            valueStart = valueEnd + 1;
        }
        String name = decodeComponent(s, nameStart, valueStart - 1, charset, false, false);
        String value = decodeComponent(s, valueStart, valueEnd, charset, false, false);
        List<String> values = params.get(name);
        if (values == null) {
            // Often there's only 1 value.
            values = new ArrayList<String>(1);
            params.put(name, values);
        }
        values.add(value);
        return true;
    }

    /**
     * Decodes a bit of an URL encoded by a browser.
     * <p>
     * This is equivalent to calling {@link #decodeComponent(String, Charset, boolean)}
     * with the UTF-8 charset (recommended to comply with RFC 3986, Section 2).
     * @param s The string to decode (can be empty).
     * @return The decoded string, or {@code s} if there's nothing to decode.
     * If the string to decode is {@code null}, returns an empty string.
     * @throws IllegalArgumentException if the string contains a malformed
     * escape sequence.
     */
    public static String decodeComponent(final String s) {
        return decodeComponent(s, Constants.DEFAULT_CHARSET, false);
    }

    /**
     * Decodes a bit of an URL encoded by a browser.
     * <p>
     * The string is expected to be encoded as per RFC 3986, Section 2.
     * This is the encoding used by JavaScript functions {@code encodeURI}
     * and {@code encodeURIComponent}, but not {@code escape}.  For example
     * in this encoding, &eacute; (in Unicode {@code U+00E9} or in UTF-8
     * {@code 0xC3 0xA9}) is encoded as {@code %C3%A9} or {@code %c3%a9}.
     * <p>
     * This is essentially equivalent to calling
     *   {@link URLDecoder#decode(String, String)}
     * except that it's over 2x faster and generates less garbage for the GC.
     * Actually this function doesn't allocate any memory if there's nothing
     * to decode, the argument itself is returned.
     * @param s The string to decode (can be empty).
     * @param charset The charset to use to decode the string (should really
     * be UTF_8.
     * @return The decoded string, or {@code s} if there's nothing to decode.
     * If the string to decode is {@code null}, returns an empty string.
     * @throws IllegalArgumentException if the string contains a malformed
     * escape sequence.
     */
    public static String decodeComponent(final String s, final Charset charset, boolean ignoreEncode) {
        if (s == null) {
            return Constants.EMPTY_STRING;
        }
        return decodeComponent(s, 0, s.length(), charset, false, ignoreEncode);
    }

    private static String decodeComponent(String s, int from, int toExcluded, Charset charset, boolean isPath, boolean ignoreEncode) {
        int len = toExcluded - from;
        if (len <= 0) {
            return Constants.EMPTY_STRING;
        }
        int firstEscaped = -1;
        for (int i = from; i < toExcluded; i++) {
            char c = s.charAt(i);
            if (c == '%' || c == '+' && !isPath) {
                firstEscaped = i;
                break;
            }
        }
        if (firstEscaped == -1) {
            return s.substring(from, toExcluded);
        }

        CharsetDecoder decoder = charset.newDecoder();

        // Each encoded byte takes 3 characters (e.g. "%20")
        int decodedCapacity = (toExcluded - firstEscaped) / 3;
        ByteBuffer byteBuf = ByteBuffer.allocate(decodedCapacity);
        CharBuffer charBuf = CharBuffer.allocate(decodedCapacity);

        StringBuilder strBuf = new StringBuilder(len);
        strBuf.append(s, from, firstEscaped);

        for (int i = firstEscaped; i < toExcluded; i++) {
            char c = s.charAt(i);
            if (ignoreEncode || c != '%') {
                strBuf.append(c != '+' || isPath? c : Constants.SPACE);
                continue;
            }

            byteBuf.clear();
            do {
                if (i + 3 > toExcluded) {
                    throw new IllegalArgumentException("unterminated escape sequence at index " + i + " of: " + s);
                }
                byteBuf.put(StringUtils.decodeHexByte(s, i + 1));
                i += 3;
            } while (i < toExcluded && s.charAt(i) == '%');
            i--;

            byteBuf.flip();
            charBuf.clear();
            CoderResult result = decoder.reset().decode(byteBuf, charBuf, true);
            try {
                if (!result.isUnderflow()) {
                    result.throwException();
                }
                result = decoder.flush(charBuf);
                if (!result.isUnderflow()) {
                    result.throwException();
                }
            } catch (CharacterCodingException ex) {
                throw new IllegalStateException(ex);
            }
            strBuf.append(charBuf.flip());
        }
        return strBuf.toString();
    }

    private static int findPathEndIndex(String uri) {
        int len = uri.length();
        for (int i = 0; i < len; i++) {
            char c = uri.charAt(i);
            if (c == '?' || c == '#') {
                return i;
            }
        }
        return len;
    }
}
