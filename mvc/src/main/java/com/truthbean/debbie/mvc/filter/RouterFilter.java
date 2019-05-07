package com.truthbean.debbie.mvc.filter;

import com.truthbean.debbie.mvc.request.RouterRequest;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public interface RouterFilter {

    default boolean doFilter(RouterRequest request) {
        return true;
    }
}
