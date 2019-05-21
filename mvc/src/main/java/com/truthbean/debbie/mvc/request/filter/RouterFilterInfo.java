package com.truthbean.debbie.mvc.request.filter;

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
    private Class<? extends RouterFilter> routerFilterType;

    private List<String> rawUrlPattern;
    private List<Pattern> urlPattern;

    public RouterFilterInfo() {
        urlPattern = new ArrayList<>();
        rawUrlPattern = new ArrayList<>();
    }

    public Class<? extends RouterFilter> getRouterFilterType() {
        return routerFilterType;
    }

    public void setRouterFilterType(Class<? extends RouterFilter> routerFilterType) {
        this.routerFilterType = routerFilterType;
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
                Objects.equals(getRouterFilterType(), that.getRouterFilterType()) &&
                Objects.equals(getUrlPattern(), that.getUrlPattern());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getRouterFilterType(), getUrlPattern());
    }

    @Override
    public String toString() {
        return "{" +
                "\"name\":\"" + name + '\"' +
                ",\"routerFilter\":" + routerFilterType +
                ",\"rawUrlPattern\":" + rawUrlPattern +
                ",\"urlPattern\":" + urlPattern +
                '}';
    }

    @Override
    public int compareTo(RouterFilterInfo o) {
        return Integer.compare(order, o.order);
    }
}
