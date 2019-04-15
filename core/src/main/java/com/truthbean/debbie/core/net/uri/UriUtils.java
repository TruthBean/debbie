package com.truthbean.debbie.core.net.uri;

import com.truthbean.debbie.core.util.Constants;

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

    public static UriComposition resolveUri(String uri) {
        var uriComposition = new UriComposition();

        String[] tmp = uri.split("://");
        var builder = new UriComposition.Builder().scheme(tmp[0]);
        // todo
        return builder.build();
    }

    public static String getScheme(String url) {
        return url.split("://")[0];
    }

    public static String getHost(String url) {
        var tmp = url.split("://")[1];
        System.out.println(tmp);
        return null;
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

    public static void main(String[] args) {
        String url = "https://user:password@debbie.truthbean.com/hello/hahahah?key1=name&key1=name&key1=name&key2=2&key3=3.45&4=";
        var queriesInUri = queriesInUri(url);
        System.out.println(queriesInUri.toString());

        System.out.println(getScheme(url));

        System.out.println("------------------------");
        System.out.println(getHost(url));
    }
}