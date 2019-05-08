package com.truthbean.debbie.mvc.response;

public interface ResponseFilter {

    default boolean doFilter(RouterResponse response) {
        return false;
    }
}
