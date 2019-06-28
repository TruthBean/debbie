package com.truthbean.debbie.net.uri;

import java.util.*;
import java.util.regex.Pattern;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/16 09:27.
 */
public class UriPathFragment implements Cloneable {
    /**
     * path fragment
     */
    private String fragment;

    private Pattern pattern;

    private final Map<String, List<String>> pathVariable = new HashMap<>();
    private final Map<UriPathVariable, List<String>> uriPathVariable = new HashMap<>();

    public String getFragment() {
        return fragment;
    }

    public void setFragment(String fragment) {
        this.fragment = fragment;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public String getRegex() {
        if (pattern != null) {
            return pattern.pattern();
        }
        if (fragment != null) {
            return fragment;
        }
        return null;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public Map<String, List<String>> getPathVariable() {
        return pathVariable;
    }

    public Map<UriPathVariable, List<String>> getUriPathVariable() {
        return uriPathVariable;
    }

    public List<UriPathVariable> getUriPathVariableNames() {
        List<UriPathVariable> list = new ArrayList<>();
        UriPathVariable[] uriPathVariables = uriPathVariable.keySet().toArray(UriPathVariable[]::new);
        for (int i = uriPathVariables.length - 1; i >= 0; i--) {
            list.add(uriPathVariables[i]);
        }
        return list;
    }

    public UriPathVariable getUriPathVariable(String name) {
        List<UriPathVariable> list = new ArrayList<>();
        Set<UriPathVariable> uriPathVariables = uriPathVariable.keySet();
        for (UriPathVariable variable : uriPathVariables) {
            if (variable.getName().equals(name)) {
                return variable;
            }
        }
        return null;
    }

    public void addPathVariable(UriPathVariable name, String value) {
        List<String> values;
        if (uriPathVariable.containsKey(name)) {
            values = uriPathVariable.get(name);
        } else {
            values = new ArrayList<>();
        }
        values.add(value);
        pathVariable.put(name.getName(), values);
        uriPathVariable.put(name, values);
    }

    public void addPathVariable(UriPathVariable name, List<String> value) {
        uriPathVariable.put(name, value);
        pathVariable.put(name.getName(), value);
    }

    public void addPathVariables(Map<UriPathVariable, List<String>> map) {
        if (map != null && !map.isEmpty()) {
            uriPathVariable.putAll(map);
            map.forEach((key, value) -> pathVariable.put(key.getName(), value));
        }
    }

    @Override
    public String toString() {
        return "{\"fragment\":\"" + fragment + "\",\"pathVariable\":" + pathVariable + '}';
    }

    @Override
    public UriPathFragment clone() {
        UriPathFragment fragment;
        try {
            fragment = (UriPathFragment) super.clone();
        } catch (CloneNotSupportedException e) {
            fragment = new UriPathFragment();
            e.printStackTrace();
        }
        fragment.fragment = this.fragment;
        fragment.pathVariable.putAll(this.pathVariable);

        return fragment;
    }

    public void reset() {
        this.pathVariable.keySet().forEach(key -> this.pathVariable.put(key, null));
    }

    public boolean hasVariable() {
        return !this.pathVariable.isEmpty();
    }
}
