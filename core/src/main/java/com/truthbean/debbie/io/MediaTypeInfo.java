/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.io;

import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Unknown
 * @since 0.0.1
 */
public class MediaTypeInfo {
    private static final String TOKEN = "([a-zA-Z0-9-!#$%&'*+.^_`{|}~]+)";
    private static final String QUOTED = "\"([^\"]*)\"";
    private static final Pattern TYPE_SUBTYPE = Pattern.compile(TOKEN + "/" + TOKEN);
    private static final Pattern PARAMETER =
            Pattern.compile(";\\s*(?:" + TOKEN + "=(?:" + TOKEN + "|" + QUOTED + "))?");

    private final String mediaType;
    private final String type;
    private final String subtype;
    private final String charset;

    private MediaTypeInfo(String mediaType, String type, String subtype, String charset) {
        this.mediaType = mediaType;
        this.type = type;
        this.subtype = subtype;
        this.charset = charset;
    }

    /**
     * @param string raw media type
     * @return a media type for {@code string}, or null if {@code string} is not a well-formed media
     * type.
     */
    public static MediaTypeInfo parse(String string) {
        Matcher typeSubtype = TYPE_SUBTYPE.matcher(string);
        if (!typeSubtype.lookingAt()) {
            return null;
        }
        String type = typeSubtype.group(1).toLowerCase(Locale.CHINA);
        String subtype = typeSubtype.group(2).toLowerCase(Locale.CHINA);

        String charset = null;
        Matcher parameter = PARAMETER.matcher(string);
        for (int s = typeSubtype.end(); s < string.length(); s = parameter.end()) {
            parameter.region(s, string.length());
            if (!parameter.lookingAt()) {
                // This is not a well-formed media type.
                return null;
            }

            String name = parameter.group(1);
            if (!"charset".equalsIgnoreCase(name)) {
                continue;
            }
            String charsetParameter;
            String token = parameter.group(2);
            if (token != null) {
                // If the token is 'single-quoted' it's invalid! But we're lenient and strip the quotes.
                charsetParameter = (token.startsWith("'") && token.endsWith("'") && token.length() > 2) ? token.substring(1, token.length() - 1) : token;
            } else {
                // Value is "double-quoted". That's valid and our regex group already strips the quotes.
                charsetParameter = parameter.group(3);
            }
            if (charset != null && !charsetParameter.equalsIgnoreCase(charset)) {
                // Multiple different charsets!
                return null;
            }
            charset = charsetParameter;
        }

        return new MediaTypeInfo(string, type, subtype, charset);
    }

    /**
     * @return the high-level media type, such as "text", "image", "audio", "video", or
     * "application".
     */
    public String type() {
        return type;
    }

    public boolean isText() {
        if ("text".equalsIgnoreCase(type)) {
            return true;
        }
        if ("application".equalsIgnoreCase(type)){
            return "json".equalsIgnoreCase(subtype) || "xml".equalsIgnoreCase(subtype)
                    || "javascript".equalsIgnoreCase(subtype) || "xhtml+xml".equals(subtype);
        }
        return false;
    }

    /**
     * @return a specific media subtype, such as "plain" or "png", "mpeg", "mp4" or "xml".
     */
    public String subtype() {
        return subtype;
    }

    /**
     * @return the charset of this media type, or null if this media type doesn't specify a charset.
     */
    public Charset charset() {
        return charset(null);
    }

    /**
     * @param defaultValue default charset
     * @return the charset of this media type, or {@code defaultValue} if either this media type
     * doesn't specify a charset, of it its charset is unsupported by the current runtime.
     */
    public Charset charset(Charset defaultValue) {
        try {
            return charset != null ? Charset.forName(charset) : defaultValue;
        } catch (IllegalArgumentException e) {
            // This charset is invalid or unsupported. Give up.
            return defaultValue;
        }
    }

    /**
     * Returns the encoded media type, like "text/plain; charset=utf-8", appropriate for use in a
     * Content-Type header.
     */
    @Override
    public String toString() {
        return mediaType;
    }

    public MediaType toMediaType() {
        return MediaType.of(mediaType);
    }

    public boolean isAny() {
        return "*".equals(type);
    }

    public boolean isSameMediaType(MediaType mediaType) {
        return mediaType.info().mediaType.equalsIgnoreCase(this.mediaType);
    }

    public boolean isSameMediaType(MediaTypeInfo info) {
        return info.mediaType.equalsIgnoreCase(this.mediaType);
    }

    public static boolean contains(Collection<MediaTypeInfo> all, MediaTypeInfo target) {
        for (var type: all) {
            if (target.equals(type)) {
                return true;
            }
        }
        for (var type : all) {
            if (target.isSameMediaType(type))
                return true;
        }
        return false;
    }

    public boolean includes(String mediaType) {
        return includes(Objects.requireNonNull(parse(mediaType)));
    }

    public boolean includes(MediaType mediaType) {
        var info = mediaType.info();
        return includes(info);
    }

    public boolean includes(MediaTypeInfo info) {
        return "*".equals(type) || (type.equalsIgnoreCase(info.type) && ("*".equals(subtype) || subtype.equalsIgnoreCase(info.subtype)));
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof MediaTypeInfo && ((MediaTypeInfo) other).mediaType.equals(mediaType);
    }

    @Override
    public int hashCode() {
        return mediaType.hashCode();
    }
}