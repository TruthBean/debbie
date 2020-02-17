package com.truthbean.debbie.mvc.url;

import com.truthbean.debbie.net.uri.UriPathFragment;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/16 09:27.
 */
public class RouterPathFragments {
    private List<UriPathFragment> pathFragments;

    private boolean hasVariable;
    private String rawPath;
    private Pattern pattern;

    public List<UriPathFragment> getPathFragments() {
        return pathFragments;
    }

    public RouterPathFragments setPathFragments(List<UriPathFragment> pathFragments) {
        this.pathFragments = pathFragments;
        return this;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public RouterPathFragments setPattern(Pattern pattern) {
        this.pattern = pattern;
        return this;
    }

    public RouterPathFragments setPattern() {
        if (pathFragments != null && !pathFragments.isEmpty()) {
            var regex = pathFragments.stream().map(UriPathFragment::getRegex).collect(Collectors.joining("/"));
            if (!regex.startsWith("/")) {
                regex = "/" + regex;
            }
            this.pattern = Pattern.compile(regex);
        }
        return this;
    }

    public String getRawPath() {
        return rawPath;
    }

    public RouterPathFragments setRawPath(String rawPath) {
        this.rawPath = rawPath;
        return this;
    }

    public RouterPathFragments setRawPath() {
        if (pathFragments != null && !pathFragments.isEmpty()) {
            this.rawPath = pathFragments.stream()
                    .map(UriPathFragment::getFragment)
                    .collect(Collectors.joining("/"));
            if (!this.rawPath.startsWith("/")) {
                this.rawPath = "/" + rawPath;
            }
        }
        return this;
    }

    public Boolean hasVariable() {
        return hasVariable;
    }

    public boolean isDynamic() {
        return !getPattern().toString().equals(rawPath);
    }

    public boolean matchUrl(String url) {
        return getPattern().matcher(url).find();
    }

    public RouterPathFragments setVariable() {
        if (pathFragments != null && !pathFragments.isEmpty()) {
            for (UriPathFragment pathFragment : pathFragments) {
                if (pathFragment.hasVariable()) {
                    hasVariable = true;
                    break;
                }
            }
        }
        return this;
    }

    @Override
    public String toString() {
        return "{\"pathFragments\":" + pathFragments + ",\"pattern\":" + pattern + ",\"rawPath\":" + rawPath + '}';
    }
}
