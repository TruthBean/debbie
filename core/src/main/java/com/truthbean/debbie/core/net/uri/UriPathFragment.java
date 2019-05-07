package com.truthbean.debbie.core.net.uri;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public void addPathVariable(String name, String value) {
        List<String> values;
        if (pathVariable.containsKey(name)) {
            values = pathVariable.get(name);
        } else {
            values = new ArrayList<>();
        }
        values.add(value);
        pathVariable.put(name, values);
    }

    public void addPathVariable(String name, List<String> value) {
        pathVariable.put(name, value);
    }

    public void addPathVariables(Map<String, List<String>> map) {
        pathVariable.putAll(map);
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
