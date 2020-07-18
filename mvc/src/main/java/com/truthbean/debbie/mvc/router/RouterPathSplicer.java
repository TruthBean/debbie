/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mvc.router;

import com.truthbean.debbie.mvc.url.RouterPathFragments;
import com.truthbean.debbie.net.uri.UriPathFragment;
import com.truthbean.debbie.net.uri.UriPathVariable;
import com.truthbean.debbie.net.uri.UriUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class RouterPathSplicer {

    private static final String VARIABLE_REGEX = "\\{[^/]+?\\}";
    private static final Pattern BLACK_PATTERN = Pattern.compile("[A-Za-z0-9_.]+");
    private static final Pattern VARIABLE_PATTERN = Pattern.compile(VARIABLE_REGEX);

    private static List<String> resolvePath(RouterAnnotationInfo router) {
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

    public static List<String> splicePaths(String dispatcherMapping, Router prefixRouter, RouterAnnotationInfo router) {
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

    public static List<String> splicePaths(String apiPrefix, List<String> prefixRouter, RouterAnnotationInfo router) {
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

    private static Set<String> splicePaths(Router prefixRouter, RouterAnnotationInfo router) {
        if (prefixRouter != null) {
            var prefixPathRegex = resolvePath(new RouterAnnotationInfo(prefixRouter));
            if (prefixPathRegex != null)
                return splicePaths(prefixPathRegex, router);
        }
        return splicePaths(router);
    }

    public static List<Pattern> splicePathRegex(Router prefixRouter, RouterAnnotationInfo router) {
        var paths = splicePaths(prefixRouter, router);
        List<Pattern> patterns = new ArrayList<>();
        for (String path : paths) {
            patterns.add(Pattern.compile(path));
        }
        return patterns;
    }

    public static Map<String, List<String>> getPathVariable(UriPathFragment pathFragment, String targetUrl) {
        Map<String, List<String>> result = new HashMap<>();
        var routerPath = pathFragment.getFragment();
        String[] split = routerPath.split(VARIABLE_REGEX);
        List<UriPathVariable> uriPathVariableNames = pathFragment.getUriPathVariableNames();
        if (split.length == 0) {
            var name = routerPath.substring(1, routerPath.length() - 1);
            var uriPathVariableName = pathFragment.getUriPathVariable(name);
            if (uriPathVariableName != null) {
                Pattern pattern = uriPathVariableName.getPattern();
                if (pattern == null) return result;
                if (pattern.matcher(targetUrl).matches()) {
                    addUrl(targetUrl, result, name);
                }
            }
        } else {
            for (String s : split) {
                if ("".equals(s)) continue;
                String[] values = targetUrl.split(s);
                List<String> list = new ArrayList<>();
                if (values.length > 0) {
                    for (String value : values) {
                        if ("".equals(value)) continue;
                        list.add(value);
                    }
                }
                for (int i = 0; i < list.size(); i++) {
                    var uriPathVariableName = uriPathVariableNames.get(i);
                    if (uriPathVariableName == null) continue;
                    var value = list.get(i);
                    Pattern pattern = uriPathVariableName.getPattern();
                    if (pattern == null) continue;
                    if (pattern.matcher(value).matches()) {
                        String name = uriPathVariableName.getName();
                        addUrl(value, result, name);
                    }
                }
            }
        }
        return result;
    }

    private static void addUrl(String targetUrl, Map<String, List<String>> result, String name) {
        var resultCopy = new HashMap<>(result);
        List<String> list;
        if (resultCopy.containsKey(name)) {
            list = resultCopy.get(name);
        } else {
            list = new ArrayList<>();
        }
        list.add(targetUrl);
        result.put(name, list);
    }

    public static List<RouterPathFragments> splicePathFragment(String dispatcherMapping, Router prefixRouter, RouterAnnotationInfo router) {
        var paths = splicePaths(dispatcherMapping, prefixRouter, router);
        List<RouterPathFragments> patterns = new ArrayList<>();
        for (String path : paths) {
            var fragment = new RouterPathFragments();
            List<UriPathFragment> pathFragments = UriUtils.getPathFragment(path);
            for (var pathFragment : pathFragments) {
                var regex = pathFragment.getFragment();
                if (regex.contains("*")) {
                    regex = regex.replace("*", "\\*");
                }

                Matcher matcher = VARIABLE_PATTERN.matcher(regex);
                while (matcher.find()) {
                    String group = matcher.group();
                    UriPathVariable uriPathVariable = new UriPathVariable();
                    if (group.contains(":")) {
                        String[] split = group.split(":");
                        uriPathVariable.setName(split[0].substring(1));
                        var pattern = split[1].substring(0, split[1].length() - 1);
                        uriPathVariable.setPattern(Pattern.compile(pattern));
                        regex = regex.replace(group, pattern);
                    } else {
                        uriPathVariable.setName(group.substring(1, group.length() - 1));
                        uriPathVariable.setPattern(BLACK_PATTERN);
                        regex = regex.replace(group, "[A-Za-z0-9_.]+");
                    }
                    pathFragment.addPathVariable(uriPathVariable, new ArrayList<>());
                }
                pathFragment.setPattern(Pattern.compile(regex));
            }
            fragment.setPathFragments(pathFragments).setPattern().setRawPath().setVariable();
            patterns.add(fragment);
        }
        return patterns;
    }

    public static Set<String> splicePaths(List<String> prefixPaths, RouterAnnotationInfo router) {
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

    public static Set<String> splicePaths(RouterAnnotationInfo router) {
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
            throw new RouterException("router value or pathRegex cannot be empty");
        }
        return newPaths;
    }

    public static String replaceDispatcherMapping(String dispatcherMapping, String path) {
        if ("/**".equals(dispatcherMapping)) {
            return "/" + path;
        }
        return dispatcherMapping.replace("**", path);
    }
}
