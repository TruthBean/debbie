package com.truthbean.debbie.mvc.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class RouterFilterInfo implements Comparable<RouterFilterInfo> {
    private int order;
    private String name;
    private RouterFilter routerFilter;

    private List<String> rawUrlPattern;
    private List<Pattern> urlPattern;

    public RouterFilterInfo() {
        urlPattern = new ArrayList<>();
        rawUrlPattern = new ArrayList<>();
    }

    public RouterFilter getRouterFilter() {
        return routerFilter;
    }

    public void setRouterFilter(RouterFilter routerFilter) {
        this.routerFilter = routerFilter;
    }

    public List<Pattern> getUrlPattern() {
        return urlPattern;
    }

    public List<String> getRawUrlPattern() {
        return rawUrlPattern;
    }

    public String[] getUrlPatterns() {
        return rawUrlPattern.toArray(new String[0]);
    }

    public void addUrlPattern(Pattern urlPattern) {
        this.urlPattern.add(urlPattern);
    }

    public void addRawUrlPattern(String rawUrlPattern) {
        this.rawUrlPattern.add(rawUrlPattern);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RouterFilterInfo)) return false;
        RouterFilterInfo that = (RouterFilterInfo) o;
        return Objects.equals(getName(), that.getName()) &&
                Objects.equals(getRouterFilter(), that.getRouterFilter()) &&
                Objects.equals(getUrlPattern(), that.getUrlPattern());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getRouterFilter(), getUrlPattern());
    }

    @Override
    public String toString() {
        return "{" +
                "\"name\":\"" + name + '\"' +
                ",\"routerFilter\":" + routerFilter +
                ",\"rawUrlPattern\":" + rawUrlPattern +
                ",\"urlPattern\":" + urlPattern +
                '}';
    }

    @Override
    public int compareTo(RouterFilterInfo o) {
        return Integer.compare(order, o.order);
    }
}
