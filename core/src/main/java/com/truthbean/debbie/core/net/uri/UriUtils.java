package com.truthbean.debbie.core.net.uri;

import com.truthbean.debbie.core.util.Constants;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
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

    public static UriComposition resolveUrl(String url, Charset charset) {
        var encodedUrl = URLEncoder.encode(url, charset);
        try {
            URL uri = new URL(encodedUrl);
            return resolveUrl(uri);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static UriComposition resolveUrl(URL url) {
        var builder = new UriComposition.Builder();
        builder.scheme(url.getProtocol());

        var userInfo = url.getUserInfo();
        System.out.println(userInfo);
        var tmp = userInfo.split(":");
        builder.username(tmp[0]);
        builder.password(tmp[1]);

        builder.host(url.getHost());
        var port = url.getPort() > 0 ? url.getPort() : 80;
        builder.port(port);

        var paths = url.getPath();

        var tmpPath = paths;
        if (paths.contains(";")) {
            tmp = paths.split(";");
            tmpPath = tmp[0];

            for (int i = 1; i < tmp.length; i++) {
                var tmpMatrix = tmp[i];
                builder.addMatrix(resolveParam(tmpMatrix));
            }
        }

        String[] pathArray = tmpPath.split("/");
        var partLength = pathArray.length;
        for (int i = 0; i < partLength - 1; i++) {
            var tempPath = pathArray[i];
            if (tempPath != null && !"".equals(tempPath.trim())) {
                builder.addPath(pathArray[i]);
            }
        }

        var lastPath = pathArray[partLength - 1];
        if (lastPath.contains(".")) {
            tmp = lastPath.split("\\.");
            builder.addPath(tmp[0]);
            builder.suffix(tmp[tmp.length - 1]);
        } else {
            builder.addPath(lastPath);
        }

        var queries = url.getQuery();
        builder.queries(resolveParam(queries));

        var fragment = url.getRef();
        builder.fragment(resolveFragment(fragment));

        return builder.build();
    }

    /*public static UriComposition resolveUri(String uri) {
        String[] split4Scheme = uri.split("://");
        var builder = new UriComposition.Builder().scheme(split4Scheme[0]);

        String schemeSpecificPart;
        String querySpecific = null;
        String fragmentSpecific = null;

        String tmp = split4Scheme[1];
        if (tmp.contains("?")) {
            var temp = tmp.split("\\?");
            schemeSpecificPart = temp[0];
            var queryTmp = temp[1];

            if (queryTmp.contains("#")) {
                var tmpArray = queryTmp.split("#");
                querySpecific = tmpArray[0];
                fragmentSpecific = tmpArray[1];
            } else {
                querySpecific = queryTmp;
            }

        } else if (tmp.contains("#")) {
            var temp = tmp.split("#");
            schemeSpecificPart = temp[0];
            fragmentSpecific = temp[1];
        } else {
            schemeSpecificPart = tmp;
        }

        String[] splitByPart = schemeSpecificPart.split("/");

        tmp = splitByPart[0];
        if (tmp.contains("@")) {
            String[] splitByAt = tmp.split("@");
            var auth = splitByAt[0];
            var temp = auth.split(":");
            builder.username(temp[0]);
            builder.password(temp[1]);

            var hostAndPort = splitByAt[1];
            if (hostAndPort.contains(":")) {
                temp = hostAndPort.split(":");
                builder.host(temp[0]);
                builder.port(Integer.parseInt(temp[1]));
            } else {
                builder.host(hostAndPort);
                builder.port(80);
            }
        } else {
            if (tmp.contains(":")) {
                var temp = tmp.split(":");
                builder.host(temp[0]);
                builder.port(Integer.parseInt(temp[1]));
            } else {
                builder.host(tmp);
                builder.port(80);
            }
        }

        var partLength = splitByPart.length;
        for (int i = 1; i < partLength - 1; i++) {
            builder.addPath(splitByPart[i]);
        }

        tmp = splitByPart[partLength - 1];
        if (tmp.contains(";")) {
            var temp = tmp.split(";");
            var lastPath = temp[0];

            if (lastPath.contains(".")) {
                var tempPath = lastPath.split("\\.");
                builder.addPath(tempPath[0]);
                builder.suffix(tempPath[1]);
            } else {
                builder.addPath(lastPath);
            }

            var tmpMatrix = temp[1];
            builder.matrix(resolveParam(tmpMatrix));

        } else {
            if (tmp.contains(".")) {
                var tempPath = tmp.split("\\.");
                builder.addPath(tempPath[0]);
                builder.suffix(tempPath[1]);
            } else {
                builder.addPath(tmp);
            }
        }

        if (querySpecific != null) {
            builder.queries(resolveParam(querySpecific));
        }

        if (fragmentSpecific != null) {
            builder.fragment(resolveFragment(fragmentSpecific));
        }

        return builder.build();
    }*/

    public static Map<String, List<String>> resolveMatrixByPath(String path) {
        Map<String, List<String>> result = new HashMap<>();
        if (path.contains(";")) {
            var tmp = path.split(";");
            for (int i = 1; i < tmp.length; i++) {
                addParam(tmp[i], result);
            }
        }
        return result;
    }

    public static Map<String, List<String>> resolveMatrix(String matrix) {
        Map<String, List<String>> result = new HashMap<>();
        if (matrix.contains(";")) {
            var tmp = matrix.split(";");
            for (String s : tmp) {
                addParam(s, result);
            }
        } else {
            return resolveParam(matrix);
        }
        return result;
    }

    public static UriFragmentComposition resolveFragment(String fragment) {
        var result = new UriFragmentComposition();

        String pathSpecific = fragment;
        String querySpecific = null;
        if (fragment.contains("?")) {
            String[] splitByQuery = fragment.split("\\?");
            querySpecific = splitByQuery[1];

            pathSpecific = splitByQuery[0];
        }

        String[] splitByPart = pathSpecific.split("/");
        var partLength = splitByPart.length;
        for (int i = 1; i < partLength - 1; i++) {
            result.addPath(splitByPart[i]);
        }

        String tmp = splitByPart[partLength - 1];
        if (tmp.contains(";")) {
            var temp = tmp.split(";");
            var lastPath = temp[0];
            result.addPath(lastPath);

            for (int i = 1; i < temp.length; i++) {
                var tmpMatrix = temp[i];
                result.addMatrix(resolveParam(tmpMatrix));
            }

        } else {
            result.addPath(tmp);
        }

        if (querySpecific != null) {
            result.addQueries(resolveParam(querySpecific));
        }

        return result;
    }

    public static String getScheme(String url) {
        return url.split("://")[0];
    }

    public static String uri(String uri) {
        return uri.split("\\" + Constants.QUESTION_MARK)[0];
    }

    public static Map<String, List<String>> queriesInUri(String uri) {
        Map<String, List<String>> result = new HashMap<>();
        if (uri.contains(Constants.QUESTION_MARK)) {
            var query = uri.split("\\" + Constants.QUESTION_MARK)[1];
            if (query.contains("#")) {
                query = query.split("#")[0];
            }
            result.putAll(resolveParam(query));
        }
        return result;
    }

    public static Map<String, List<String>> resolveParam(String params) {
        Map<String, List<String>> result = new HashMap<>();
        var paramsStr = params.split(Constants.AND_MARK);
        List<String> values;
        for (var param : paramsStr) {
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
        return result;
    }

    public static void addParam(String params, Map<String, List<String>> result) {
        var paramsMap = resolveParam(params);
        paramsMap.forEach((k, v) -> {
            List<String> values;
            if (result.containsKey(k)) {
                values = result.get(k);
            } else {
                values = new ArrayList<>();
            }
            if (v != null && !v.isEmpty()) {
                values.addAll(v);
            }
            result.put(k, values);
        });
    }

}