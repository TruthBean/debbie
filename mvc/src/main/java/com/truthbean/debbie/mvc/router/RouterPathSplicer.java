package com.truthbean.debbie.mvc.router;

import com.truthbean.debbie.core.net.uri.UriPathFragment;
import com.truthbean.debbie.core.net.uri.UriUtils;
import com.truthbean.debbie.mvc.url.RouterPathFragments;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class RouterPathSplicer {

    private static final String VARIABLE_REGEX = "\\{[^/]+?\\}";
    private static final Pattern VARIABLE_PATTERN = Pattern.compile(VARIABLE_REGEX);

    private static List<String> resolvePath(Router router) {
        if (router != null) {
            var prefixPathRegex = router.value();

            if (isEmptyPaths(prefixPathRegex)) {
                prefixPathRegex = router.urlPatterns();
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

    public static List<String> splicePaths(String dispatcherMapping, Router prefixRouter, Router router) {
        if (!dispatcherMapping.isBlank()) {
            if (!dispatcherMapping.startsWith("/")) {
                dispatcherMapping = "/" + dispatcherMapping;
            }
            if (dispatcherMapping.endsWith("/")) {
                dispatcherMapping = dispatcherMapping.substring(0, dispatcherMapping.length() - 1);
            }
        }
        var paths = splicePaths(prefixRouter, router);

        List<String> list = new ArrayList<>();
        for (String s : paths) {
            if (dispatcherMapping.endsWith("**") && s.startsWith("/")) {
                s = s.substring(1);
            }
            s = dispatcherMapping.replace("**", s);
            list.add(s);
        }

        return list;
    }

    public static List<String> splicePaths(String apiPrefix, List<String> prefixRouter, Router router) {
        var apiPrefixAfterTrim = apiPrefix;
        if (!apiPrefix.isBlank() && apiPrefix.endsWith("/")) {
            apiPrefixAfterTrim = apiPrefix.substring(0, apiPrefix.length() - 1);
        }
        Set<String> paths;
        if (prefixRouter != null) {
            paths = splicePaths(prefixRouter, router);
        } else {
            paths = splicePaths(router);
        }

        List<String> list = new ArrayList<>();
        for (String s : paths) {
            list.add(apiPrefixAfterTrim + s);
        }

        return list;
    }

    private static Set<String> splicePaths(Router prefixRouter, Router router) {
        if (prefixRouter != null) {
            var prefixPathRegex = resolvePath(prefixRouter);
            if (prefixPathRegex != null)
                return splicePaths(prefixPathRegex, router);
        }
        return splicePaths(router);
    }

    public static List<Pattern> splicePathRegex(Router prefixRouter, Router router) {
        var paths = splicePaths(prefixRouter, router);
        List<Pattern> patterns = new ArrayList<>();
        for (String path : paths) {
            patterns.add(Pattern.compile(path));
        }
        return patterns;
    }

    public static Map<String, List<String>> getPathVariable(String routerPath, String targetUrl) {
        Map<String, List<String>> result = new HashMap<>();
        String[] split = routerPath.split(VARIABLE_REGEX);
        for (String s: split) {
            String[] values = targetUrl.split(s);
            String[] names = routerPath.split(s);
            for (int i = 0; i < names.length; i++) {
                var name = names[i];
                var value = values[i];
                if (!name.isBlank()) {
                    name = name.substring(1, name.length() - 1);
                    var resultCopy = new HashMap<>(result);
                    List<String> list;
                    if (resultCopy.containsKey(name)) {
                        list = resultCopy.get(name);
                    } else {
                        list = new ArrayList<>();
                    }
                    list.add(value);
                    result.put(name, list);
                }
            }
        }
        return result;
    }

    public static List<RouterPathFragments> splicePathFragment(String dispatcherMapping, Router prefixRouter, Router router) {
        var paths = splicePaths(dispatcherMapping, prefixRouter, router);
        List<RouterPathFragments> patterns = new ArrayList<>();
        for (String path : paths) {
            var fragment = new RouterPathFragments();
            List<UriPathFragment> pathFragments = UriUtils.getPathFragment(path);
            for (var pathFragment: pathFragments) {
                var regex = pathFragment.getFragment();
                if (regex.contains("*")) {
                    regex = regex.replace("*", "\\*");
                }
                Matcher matcher = VARIABLE_PATTERN.matcher(regex);
                while (matcher.find()) {
                    String group = matcher.group();
                    pathFragment.addPathVariable(group, new ArrayList<>());
                    regex = regex.replace(group, "[\\w]*");
                }
                pathFragment.setPattern(Pattern.compile(regex));
            }
            fragment.setPathFragments(pathFragments).setPattern().setRawPath().setVariable();
            patterns.add(fragment);
        }
        return patterns;
    }

    public static Set<String> splicePaths(List<String> prefixPaths, Router router) {
        var pathRegex = router.value();
        if (isEmptyPaths(pathRegex)) {
            pathRegex = router.urlPatterns();
        }

        Set<String> newPaths = new HashSet<>();

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
        return newPaths;
    }

    public static Set<String> splicePaths(Router router) {
        var pathRegex = router.value();
        if (isEmptyPaths(pathRegex)) {
            pathRegex = router.urlPatterns();
        }

        Set<String> newPaths = new HashSet<>();

        if (!isEmptyPaths(pathRegex)) {
            var paths = trimPaths(pathRegex);
            paths.forEach(s -> {
                if (!s.startsWith("/")) {
                    newPaths.add("/" + s);
                } else {
                    newPaths.add(s);
                }
            });
        } else {
            throw new RuntimeException("router value or pathRegex cannot be empty");
        }
        return newPaths;
    }
}
