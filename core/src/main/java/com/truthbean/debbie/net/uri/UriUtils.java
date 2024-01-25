/**
 * Copyright (c) 2024 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.net.uri;

import com.truthbean.debbie.reflection.ClassLoaderUtils;
import com.truthbean.debbie.util.Assert;
import com.truthbean.debbie.util.Constants;
import com.truthbean.Logger;
import com.truthbean.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
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
    /** Pseudo URL prefix for loading from the class path: "classpath:". */
    public static final String CLASSPATH_URL_PREFIX = "classpath:";

    /** URL prefix for loading from the file system: "file:". */
    public static final String FILE_URL_PREFIX = "file:";

    /** URL prefix for loading from a jar file: "jar:". */
    public static final String JAR_URL_PREFIX = "jar:";

    /** URL prefix for loading from a war file on Tomcat: "war:". */
    public static final String WAR_URL_PREFIX = "war:";

    /** URL protocol for a file in the file system: "file". */
    public static final String URL_PROTOCOL_FILE = "file";

    /** URL protocol for an entry from a jar file: "jar". */
    public static final String URL_PROTOCOL_JAR = "jar";

    /** URL protocol for an entry from a war file: "war". */
    public static final String URL_PROTOCOL_WAR = "war";

    /** URL protocol for an entry from a zip file: "zip". */
    public static final String URL_PROTOCOL_ZIP = "zip";

    /** URL protocol for an entry from a WebSphere jar file: "wsjar". */
    public static final String URL_PROTOCOL_WSJAR = "wsjar";

    /** URL protocol for an entry from a JBoss jar file: "vfszip". */
    public static final String URL_PROTOCOL_VFSZIP = "vfszip";

    /** URL protocol for a JBoss file system resource: "vfsfile". */
    public static final String URL_PROTOCOL_VFSFILE = "vfsfile";

    /** URL protocol for a general JBoss VFS resource: "vfs". */
    public static final String URL_PROTOCOL_VFS = "vfs";

    /** File extension for a regular jar file: ".jar". */
    public static final String JAR_FILE_EXTENSION = ".jar";

    /** Separator between JAR URL and file path within the JAR: "!/". */
    public static final String JAR_URL_SEPARATOR = "!/";

    /** Special separator between WAR URL and jar part on Tomcat. */
    public static final String WAR_URL_SEPARATOR = "*/";

    private UriUtils() {
    }

    /**
     * Determine whether the given URL points to a resource in a jar file.
     * i.e. has protocol "jar", "war, ""zip", "vfszip" or "wsjar".
     * @param url the URL to check
     * @return whether the URL has been identified as a JAR URL
     */
    public static boolean isJarUrl(URL url) {
        String protocol = url.getProtocol();
        return (UriUtils.URL_PROTOCOL_JAR.equals(protocol) || UriUtils.URL_PROTOCOL_WAR.equals(protocol) ||
                UriUtils.URL_PROTOCOL_ZIP.equals(protocol) || UriUtils.URL_PROTOCOL_VFSZIP.equals(protocol) ||
                UriUtils.URL_PROTOCOL_WSJAR.equals(protocol));
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

    /**
     * Set the {@link URLConnection#setUseCaches "useCaches"} flag on the
     * given connection, preferring {@code false} but leaving the
     * flag at {@code true} for JNLP based resources.
     * @param con the URLConnection to set the flag on
     */
    public static void useCachesIfNecessary(URLConnection con) {
        con.setUseCaches(con.getClass().getSimpleName().startsWith("JNLP"));
    }

    public static UriComposition resolveUrl(URL url) {
        var builder = new UriComposition.Builder();
        builder.scheme(url.getProtocol());

        var userInfo = url.getUserInfo();
        if (userInfo != null) {
            LOGGER.trace(() -> userInfo);
            var tmp = userInfo.split(":");
            builder.username(tmp[0]);
            builder.password(tmp[1]);
        }

        builder.host(url.getHost());
        var port = url.getPort() > 0 ? url.getPort() : 80;
        builder.port(port);

        var paths = url.getPath();

        var tmpPath = paths;
        if (paths.contains(";")) {
            var tmp = paths.split(";");
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
            if (tempPath != null && !tempPath.isBlank()) {
                builder.addPath(pathArray[i]);
            }
        }

        if (partLength > 0) {
            var lastPath = pathArray[partLength - 1];
            if (lastPath.contains(".")) {
                var tmp = lastPath.split("\\.");
                builder.addPath(tmp[0]);
                builder.suffix(tmp[tmp.length - 1]);
            } else {
                builder.addPath(lastPath);
            }
        }

        var queries = url.getQuery();
        if (queries != null)
            builder.queries(resolveParam(queries));

        var fragment = url.getRef();
        if (fragment != null) {
            builder.fragment(resolveFragment(fragment));
        }

        return builder.build();
    }

    public static String getScheme(String uri) {
        if (uri.contains("://")) {
            String[] split4Scheme = uri.split("://");
            return split4Scheme[0].trim();
        }
        return null;
    }

    public static List<UriPathFragment> getPathFragment(String uri) {
        List<UriPathFragment> result = new ArrayList<>();
        if ("/".equals(uri)) {
            UriPathFragment fragment = new UriPathFragment();
            fragment.setFragment("/");
            result.add(fragment);
            return result;
        }

        var urlPaths = getPaths(uri);
        String[] paths = urlPaths.split("/");
        for (String path : paths) {
            if (!path.isBlank()) {
                UriPathFragment fragment = new UriPathFragment();
                fragment.setFragment(path);
                result.add(fragment);
            }
        }
        return result;
    }

    public static UriComposition resolveUri(String uri) {
        String[] split4Scheme = uri.split("://");
        var scheme = getScheme(uri);
        var builder = new UriComposition.Builder();
        if (scheme != null) {
            builder.scheme(getScheme(uri));
        }

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
            buildHostAndPort(builder, hostAndPort);
        } else {
            buildHostAndPort(builder, tmp);
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
    }

    private static void buildHostAndPort(UriComposition.Builder builder, String tmp) {
        if (tmp.contains(":")) {
            var temp = tmp.split(":");
            builder.host(temp[0]);
            builder.port(Integer.parseInt(temp[1]));
        } else {
            builder.host(tmp);
            builder.port(80);
        }
    }

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

    public static String getPaths(String uri) {
        if (uri.contains("://")) {
            uri = uri.split("://")[1];
            uri = uri.substring(uri.indexOf("/"));
        }
        if (uri.contains("?")) {
            return uri.split("\\" + Constants.QUESTION_MARK)[0];
        }
        if (uri.contains("#")) {
            return uri.split("#")[0];
        }
        return uri;
    }

    public static String getPathsWithoutMatrix(String uri) {
        if (uri.contains("://")) {
            uri = uri.split("://")[1];
            uri = uri.substring(uri.indexOf("/"));
        }
        if (uri.contains(";")) {
            var temp = uri.split(";");
            uri = temp[0];
        }
        if (uri.contains("?")) {
            return uri.split("\\" + Constants.QUESTION_MARK)[0];
        }
        if (uri.contains("#")) {
            return uri.split("#")[0];
        }
        return uri;
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

    /**
     * Resolve the given resource location to a {@code java.net.URL}.
     * <p>Does not check whether the URL actually exists; simply returns
     * the URL that the given location would correspond to.
     * @param resourceLocation the resource location to resolve: either a
     * "classpath:" pseudo URL, a "file:" URL, or a plain file path
     * @return a corresponding URL object
     * @throws FileNotFoundException if the resource cannot be resolved to a URL
     *
     * @since 0.0.2
     */
    public static URL getUrl(String resourceLocation) throws FileNotFoundException {
        Assert.notNull(resourceLocation, "Resource location must not be null");
        if (resourceLocation.startsWith(CLASSPATH_URL_PREFIX)) {
            String path = resourceLocation.substring(CLASSPATH_URL_PREFIX.length());
            ClassLoader cl = ClassLoaderUtils.getDefaultClassLoader();
            URL url = (cl != null ? cl.getResource(path) : ClassLoader.getSystemResource(path));
            if (url == null) {
                String description = "class path resource [" + path + "]";
                throw new FileNotFoundException(description +
                        " cannot be resolved to URL because it does not exist");
            }
            return url;
        }
        try {
            // try URL
            return new URL(resourceLocation);
        } catch (MalformedURLException ex) {
            // no URL -> treat as file path
            try {
                return new File(resourceLocation).toURI().toURL();
            } catch (MalformedURLException ex2) {
                throw new FileNotFoundException("Resource location [" + resourceLocation +
                        "] is neither a URL not a well-formed file path");
            }
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(UriUtils.class);

}