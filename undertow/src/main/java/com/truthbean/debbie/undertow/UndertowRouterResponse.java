package com.truthbean.debbie.undertow;

import com.truthbean.debbie.mvc.response.RouterResponse;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class UndertowRouterResponse extends RouterResponse {

    public UndertowRouterResponse() {
    }

    public UndertowRouterResponse(RouterResponse copy) {
        copyFrom(copy);
    }
}
