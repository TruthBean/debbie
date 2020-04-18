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

    /**
     * if return true, do next; else --- postRouter(RouterRequest, RouterResponse)
     * @param request RouterRequest
     * @param response RouterResponse
     * @return if do next
     */
    default boolean preRouter(RouterRequest request, RouterResponse response) {
        return true;
    }

    /**
     * action after the router, if return true
     * if return true, change response content,
     * if return false, change response others but content
     * if return null, do nothing
     * @param request RouterRequest
     * @param response RouterResponse
     * @return is change response
     */
    default Boolean postRouter(RouterRequest request, RouterResponse response) {
        return null;
    }

    default RouterFilter setMvcConfiguration(MvcConfiguration configuration) {
        return this;
    }
}
