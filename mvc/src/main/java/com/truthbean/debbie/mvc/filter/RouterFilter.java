package com.truthbean.debbie.mvc.filter;

import com.truthbean.debbie.mvc.MvcConfiguration;
import com.truthbean.debbie.mvc.request.RouterRequest;
import com.truthbean.debbie.mvc.response.RouterResponse;

import java.io.Serializable;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public interface RouterFilter extends Serializable {

    default boolean notFilter(RouterRequest request) {
        return false;
    }

    default boolean preRouter(RouterRequest request, RouterResponse response) {
        return true;
    }

    default void postRouter(RouterRequest request, RouterResponse response) {
    }

    default RouterFilter setMvcConfiguration(MvcConfiguration configuration) {
        return this;
    }
}
