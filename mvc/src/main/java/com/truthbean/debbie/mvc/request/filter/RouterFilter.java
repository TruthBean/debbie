package com.truthbean.debbie.mvc.request.filter;

import com.truthbean.debbie.mvc.request.RouterRequest;
import com.truthbean.debbie.mvc.response.RouterResponse;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public interface RouterFilter {

    default boolean doFilter(RouterRequest request, RouterResponse response) {
        return true;
    }
}
