package com.truthbean.code.debbie.core.net.url;

import com.truthbean.code.debbie.core.io.FileExtConstant;
import com.truthbean.code.debbie.core.io.MediaType;
import com.truthbean.code.debbie.core.util.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-02-19 12:21
 */
public final class UriUtils {
    private UriUtils() {
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

    public static String uri(String uri) {
        return uri.split("\\" + Constants.QUESTION_MARK)[0];
    }

    public static Map<String, List<String>> queriesInUri(String uri) {
        Map<String, List<String>> result = new HashMap<>();
        if (uri.contains(Constants.QUESTION_MARK)) {
            var paramStr = uri.split("\\" + Constants.QUESTION_MARK)[1];
            var params = paramStr.split(Constants.AND_MARK);
            List<String> values;
            for (var param : params) {

                var split = param.split(Constants.EQUAL_MARK);
                var key = split[0];

                if (result.containsKey(key)) {
                    values = result.get(key);
                } else {
                    values = new ArrayList<>();
                }

                if (split.length == 2 && key != null) {
                    values.add(split[1]);
                    result.put(key, values);
                } else {
                    values.add(null);
                    result.put(key, values);
                }

            }
        }
        return result;
    }
}