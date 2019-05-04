package com.truthbean.debbie.core.io;

import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public enum MediaType {

    /**
     * json with utf8
     */
    APPLICATION_JSON_UTF8("application/json;Charset=UTF-8"),
    APPLICATION_JSON("application/json"),

    APPLICATION_XML("application/xml"),
    APPLICATION_XML_UTF8("application/xml;Charset=UTF-8"),

    APPLICATION_JAVASCRIPT("application/javascript"),
    APPLICATION_JAVASCRIPT_UTF8("application/javascript;Charset=UTF-8"),

    IMAGE_PNG("image/png"),
    IMAGE_GIF("image/gif"),
    IMAGE_JPEG("image/jpeg"),

    TEXT_PLAIN("text/plain"),
    TEXT_PLAIN_UTF8("text/plain;Charset=UTF-8"),

    TEXT_HTML("text/html"),
    TEXT_HTML_UTF8("text/html;Charset=UTF-8"),

    TEXT_CSS("text/css"),
    TEXT_CSS_UTF8("text/css;Charset=UTF-8"),

    APPLICATION_OCTET_STREAM("application/octet-stream"),

    ANY("*/*"),

    APPLICATION_ATOM_XML("application/atom+xml"),

    APPLICATION_FORM_URLENCODED("application/x-www-form-urlencoded"),

    APPLICATION_PDF("application/pdf"),

    APPLICATION_RSS_XML("application/rss+xml"),

    APPLICATION_XHTML_XML("application/xhtml+xml"),

    MULTIPART_FORM_DATA("multipart/form-data"),

    TEXT_EVENT_STREAM("text/event-stream"),

    TEXT_MARKDOWN("text/markdown"),

    APPLICATION_X_MSDOS_PROGRAM("application/x-msdos-program"),

    APPLICATION_X_MSDOWNLOAD("application/x-msdownload"),

    APPLICATION_ZIP("application/zip"),

    APPLICATION_X_RAR_COMPRESSED("application/x-rar-compressed"),

    APPLICATION_VND_MS_EXCEL("application/vnd.ms-excel");

    // ===============================================================================================================

    private String value;

    MediaType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public boolean isSame(MediaType other) {
        return isSame(this, other);
    }

    public static String RESPONSE_TYPE = "Response-Type";

    public static String CONTENT_TYPE = "content-type";

    public static String RESPONSE_TYPE_WITH_APPLICATION_JSON = "Response-Type: application/json";

    public static String RESPONSE_TYPE_WITH_TEXT_HTML = "Response-Type: text/html";

    public static MediaType of(String value) {
        var valueCopy = value;
        for (MediaType responseType : MediaType.values()) {
            if (value.contains(";")) {
                valueCopy = value.split(";")[0];
            }
            if (valueCopy.equals(responseType.getValue())) {
                return responseType;
            }
        }
        return TEXT_PLAIN;
    }

    public static MediaType guess(String value) {
        if (value.contains("PNG")) {
            return IMAGE_PNG;
        }
        if (value.contains("GExif") || value.contains("JFIF")) {
            return IMAGE_JPEG;
        }
        return APPLICATION_OCTET_STREAM;
    }

    public static MediaType ofWithOctetDefault(String value) {
        var values = MediaType.values();
        for (var type : values) {
            if (value.equals(type.getValue())) {
                return type;
            }
        }
        return APPLICATION_OCTET_STREAM;
    }

    public static boolean contains(Collection<MediaType> all, MediaType target) {
        var newTarget = MediaType.of(target.value);
        for (var type: all) {
            var newType = MediaType.of(type.value);
            if (newTarget == newType) {
                return true;
            }
        }
        return false;
    }

    public static boolean isSame(MediaType mediaType1, MediaType mediaType2) {
        if (mediaType1 == null && mediaType2 == null) {
            return true;
        }
        if (mediaType1 == null || mediaType2 == null) {
            return false;
        }
        var newType1 = MediaType.of(mediaType1.value);
        var newType2 = MediaType.of(mediaType2.value);
        return newType1 == newType2;
    }

    public static MediaType getTypeByUriExt(String ext) {
        switch (ext) {
            case FileExtConstant.HTML:
            case FileExtConstant.HTM:
                return MediaType.TEXT_HTML_UTF8;

            case FileExtConstant.CSS:
                return MediaType.TEXT_CSS;

            case FileExtConstant.JSON:
                return MediaType.APPLICATION_JSON;

            case FileExtConstant.XML:
                return MediaType.APPLICATION_XML;

            case FileExtConstant.JS:
                return MediaType.APPLICATION_JAVASCRIPT;

            case FileExtConstant.GIF:
                return MediaType.IMAGE_GIF;

            case FileExtConstant.PNG:
                return MediaType.IMAGE_PNG;

            case FileExtConstant.JPEG:
            case FileExtConstant.JPG:
                return MediaType.IMAGE_JPEG;

            case FileExtConstant.TEXT:
                return MediaType.TEXT_PLAIN;

            case FileExtConstant.NONE:
            default:
                return MediaType.ANY;
        }
    }

    // ============================================================================================================

    static class InternalMediaType {
        private static final String TOKEN = "([a-zA-Z0-9-!#$%&'*+.^_`{|}~]+)";
        private static final String QUOTED = "\"([^\"]*)\"";
        private static final Pattern TYPE_SUBTYPE = Pattern.compile(TOKEN + "/" + TOKEN);
        private static final Pattern PARAMETER =
                Pattern.compile(";\\s*(?:" + TOKEN + "=(?:" + TOKEN + "|" + QUOTED + "))?");

        private String mediaType;
        private String type;
        private String subtype;
        private String charset;

        private InternalMediaType(String mediaType, String type, String subtype, String charset) {
            this.mediaType = mediaType;
            this.type = type;
            this.subtype = subtype;
            this.charset = charset;
        }

        /**
         * Returns a media type for {@code string}, or null if {@code string} is not a well-formed media
         * type.
         */
        public static InternalMediaType parse(String string) {
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

            return new InternalMediaType(string, type, subtype, charset);
        }

        /**
         * Returns the high-level media type, such as "text", "image", "audio", "video", or
         * "application".
         */
        public String type() {
            return type;
        }

        /**
         * Returns a specific media subtype, such as "plain" or "png", "mpeg", "mp4" or "xml".
         */
        public String subtype() {
            return subtype;
        }

        /**
         * Returns the charset of this media type, or null if this media type doesn't specify a charset.
         */
        public Charset charset() {
            return charset(null);
        }

        /**
         * Returns the charset of this media type, or {@code defaultValue} if either this media type
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

        @Override
        public boolean equals(Object other) {
            return other instanceof InternalMediaType && ((InternalMediaType) other).mediaType.equals(mediaType);
        }

        @Override
        public int hashCode() {
            return mediaType.hashCode();
        }
    }
}
