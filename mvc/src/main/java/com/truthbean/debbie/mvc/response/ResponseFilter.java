package com.truthbean.debbie.mvc.response;

/**
 * @author truthbean
 * @since 0.0.2
 */
public interface ResponseFilter {

    default boolean doFilter(RouterResponse response) {
        return false;
    }
}
