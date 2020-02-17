package com.truthbean.debbie.mvc.request;

import com.truthbean.debbie.util.NamedThreadLocal;

/**
 * @author TruthBean
 * @since 0.0.2
 */
public class RequestHolder {
    private static final ThreadLocal<RouterRequest> requestHolder = new NamedThreadLocal<>("Request attributes");

    public static void resetRequest() {
        requestHolder.remove();
    }

    public static void setRequest(RouterRequest attributes) {
        requestHolder.set(attributes);
    }

    public static RouterRequest getRequest() {
        return requestHolder.get();
    }

    public static RouterRequest currentRequest() throws IllegalStateException {
        RouterRequest attributes = getRequest();
        if (attributes == null) {
            throw new IllegalStateException("No thread-bound request found: " +
                    "Are you referring to request attributes outside of an actual web request, " +
                    "or processing a request outside of the originally receiving thread? ");
        }
        return attributes;
    }

}
