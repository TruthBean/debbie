package com.truthbean.debbie.mvc.router;

import java.util.*;
import java.util.regex.Pattern;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class RouterPathSplicer {

    private static List<String> resolvePath(Router router) {
        if (router != null) {
            var prefixPathRegex = router.value();

            if (isEmptyPaths(prefixPathRegex)) {
                prefixPathRegex = router.path();
            }
            if (!isEmptyPaths(prefixPathRegex)) {
                var prefixPath = trimPaths(prefixPathRegex);
                List<String> newPaths = new ArrayList<>();
                for (var s : prefixPath) {
                    if (!s.startsWith("/")) {
                        newPaths.add("/" + s);
                    } else {
                        newPaths.add(s);
                    }
                }
                return newPaths;
            }
        }
        return null;
    }

    private static boolean isEmptyPaths(String[] paths) {
        if (paths == null || paths.length == 0) {
            return true;
        }

        for (String path : paths) {
            if (path != null && !path.isBlank()) {
                return false;
            }
        }

        return true;
    }

    private static List<String> trimPaths(String[] paths) {
        List<String> copy = Arrays.asList(paths);
        for (String path : paths) {
            if (path == null || path.isBlank()) {
                copy.remove(path);
            }
        }
        return copy;
    }

    public static List<String> splicePaths(String apiPrefix, Router prefixRouter, Router router) {
        var apiPrefixAfterTrim = apiPrefix;
        if (!apiPrefix.isBlank() && apiPrefix.endsWith("/")) {
            apiPrefixAfterTrim = apiPrefix.substring(0, apiPrefix.length() - 1);
        }
        var paths = splicePaths(prefixRouter, router);

        List<String> list = new ArrayList<>();
        for (String s : paths) {
            list.add(apiPrefixAfterTrim + s);
        }

        return list;
    }

    public static List<String> splicePaths(String apiPrefix, List<String> prefixRouter, Router router) {
        var apiPrefixAfterTrim = apiPrefix;
        if (!apiPrefix.isBlank() && apiPrefix.endsWith("/")) {
            apiPrefixAfterTrim = apiPrefix.substring(0, apiPrefix.length() - 1);
        }
        var paths = splicePaths(prefixRouter, router);

        List<String> list = new ArrayList<>();
        for (String s : paths) {
            list.add(apiPrefixAfterTrim + s);
        }

        return list;
    }

    private static Set<String> splicePaths(Router prefixRouter, Router router) {
        var prefixPathRegex = resolvePath(prefixRouter);
        return splicePaths(prefixPathRegex, router);
    }

    public static List<Pattern> splicePathRegex(Router prefixRouter, Router router) {
        var paths = splicePaths(prefixRouter, router);
        List<Pattern> patterns = new ArrayList<>();
        for (String path : paths) {
            patterns.add(Pattern.compile(path));
        }
        return patterns;
    }

    public static Set<String> splicePaths(List<String> prefixPaths, Router router) {
        var pathRegex = router.value();
        if (isEmptyPaths(pathRegex)) {
            pathRegex = router.path();
        }

        Set<String> newPaths = new HashSet<>();

        if (prefixPaths != null) {
            if (!isEmptyPaths(pathRegex)) {
                for (var p : prefixPaths) {
                    for (var s : pathRegex) {
                        if (s == null || s.isBlank()) {
                            newPaths.add(p);
                        } else {
                            newPaths.add(p + s);
                        }
                    }
                }
            } else {
                newPaths.addAll(prefixPaths);
            }
        } else {
            if (!isEmptyPaths(pathRegex)) {
                var paths = trimPaths(pathRegex);
                for (var s : paths) {
                    if (!s.startsWith("/")) {
                        newPaths.add("/" + s);
                    } else {
                        newPaths.add(s);
                    }
                }
            } else {
                throw new RuntimeException("router value or pathRegex cannot be empty");
            }
        }
        return newPaths;
    }
}
