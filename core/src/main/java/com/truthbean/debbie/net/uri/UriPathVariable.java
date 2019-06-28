package com.truthbean.debbie.net.uri;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * format: {name:pattern}
 *
 * @author TruthBean
 * @since 0.0.2
 */
public class UriPathVariable {
    private String name;
    private Pattern pattern;

    public UriPathVariable() {
    }

    public UriPathVariable(String name, Pattern pattern) {
        this.name = name;
        this.pattern = pattern;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UriPathVariable that = (UriPathVariable) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(pattern, that.pattern);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, pattern);
    }

    @Override
    public String toString() {
        return "{" + "name:\'" + name + '\'' + "," + "pattern:" + pattern + '}';
    }
}
