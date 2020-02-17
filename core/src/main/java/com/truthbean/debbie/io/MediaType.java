package com.truthbean.debbie.io;

import com.sun.net.httpserver.Headers;

import java.util.Collection;

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

    IMAGE_BMP("image/bmp"),

    IMAGE_SVG_XML("image/svg+xml"),

    IMAGE_TIFF("image/tiff"),

    IMAGE_VND_WAP_WBMP("image/vnd.wap.wbmp"),

    IMAGE_WEBP("image/webp"),

    IMAGE_X_ICON("image/x-icon"),

    IMAGE_X_JNG("image/x-jng"),

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
    TEXT_ANY("text/*"),
    TEXT_ANY_UTF8("text/*;Charset=UTF-8"),

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

    APPLICATION_X_WEB_APP_MANIFEST_JSON("application/x-web-app-manifest+json"),

    APPLICATION_MSWORD("application/msword"),

    APPLICATION_ZIP("application/zip"),
    APPLICATION_GIT("application/git"),


    APPLICATION_X_RAR_COMPRESSED("application/x-rar-compressed"),

    APPLICATION_VND_MS_EXCEL("application/vnd.ms-excel"),
    APPLICATION_VND_MS_POWERPOINT("application/vnd.ms-powerpoint"),

    APPLICATION_VND_OPENXMLFORMATS_OFFICEDOCUMENT_WORDPROCESSINGML_DOCUMENT("application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
    APPLICATION_VND_OPENXMLFORMATS_OFFICEDOCUMENT_SPREADSHEETML_SHEET("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
    APPLICATION_VND_OPENXMLFORMATS_OFFICEDOCUMENT_PRESENTATIONML_PRESENTATION("application/vnd.openxmlformats-officedocument.presentationml.presentation"),

    AUDIO_MIDI("audio/midi"),

    AUDIO_MP4("audio/mp4"),

    AUDIO_MPEG("audio/mpeg"),

    AUDIO_OGG("audio/ogg"),

    AUDIO_X_REALAUDIO("audio/x-realaudio"),

    AUDIO_X_WAV("audio/x-wav"),

    VIDEO_3GPP("video/3gpp"),

    VIDEO_MP4("video/mp4"),

    VIDEO_MPEG("video/mpeg"),

    VIDEO_OGG("video/ogg"),

    VIDEO_QUICKTIME("video/quicktime"),

    VIDEO_WEBM("video/webm"),

    VIDEO_X_FLV("video/x-flv"),

    VIDEO_X_MNG("video/x-mng"),

    FONT_OPENTYPE("font/opentype"),

    ;

    // ===============================================================================================================

    private String value;

    MediaType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public MediaTypeInfo info() {
        return MediaTypeInfo.parse(value);
    }

    public boolean isSame(MediaType other) {
        return isSame(this, other);
    }

    public boolean isSame(MediaTypeInfo other) {
        return other.isSameMediaType(this);
    }

    public static String RESPONSE_TYPE = "Response-Type";

    public static String CONTENT_TYPE = "Content-Type";

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

            case FileExtConstant.KAR:
            case FileExtConstant.MID:
            case FileExtConstant.MIDI:
                return MediaType.AUDIO_MIDI;

            case FileExtConstant.NONE:
            default:
                return MediaType.ANY;
        }
    }

    public boolean isText() {
        return info().isText();
    }
}
