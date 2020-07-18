/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mvc.request;

import java.util.HashMap;
import java.util.Map;

/**
 * <a>https://developer.mozilla.org/en-US/docs/Web/HTTP/Methods</a> HTTP defines a set of httpRequest methods to indicate the desired action to be performed for a given resource.
 * Although they can also be nouns, these httpRequest methods are sometimes referred to as HTTP verbs.
 * Each of them implements a different semantic, but some common features are shared by a group of them: e.g. a httpRequest method can be safe, idempotent, or cacheable.
 *
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-01-10 14:06
 */
public enum HttpMethod {

    /**
     * all methods
     */
    ALL,

    /**
     * The GET method requests a representation of the specified resource.
     * Requests using GET should only retrieve data.
     */
    GET,

    /**
     * The HEAD method asks for a response identical to that of a GET httpRequest, but without the response body.
     */
    HEAD,

    /**
     * The POST method is used to submit an entity to the specified resource,
     * often causing a change in state or side effects on the server
     */
    POST,

    /**
     * The PUT method replaces all current representations of the target resource with the httpRequest payload.
     */
    PUT,

    /**
     * The DELETE method deletes the specified resource.
     */
    DELETE,

    /**
     * The CONNECT method establishes a tunnel to the server identified by the target resource.
     */
    CONNECT,

    /**
     * The OPTIONS method is used to describe the communication options for the target resource.
     */
    OPTIONS,

    /**
     * The TRACE method performs a message loop-back test along the path to the target resource.
     */
    TRACE,

    /**
     * The PATCH method is used to apply partial modifications to a resource.
     */
    PATCH;


    // http method mappings
    private static final Map<String, HttpMethod> mappings = new HashMap<>(16);

    static {
        for (HttpMethod httpMethod : values()) {
            mappings.put(httpMethod.name(), httpMethod);
        }
    }

    private static final String GET_HTTP_1_1 = "GET / HTTP/1.1";

    public static HttpMethod of(String name) {
        HttpMethod[] values = values();
        for (HttpMethod value : values) {
            if (name.equals(value.name())) {
                return value;
            }
        }
        return ALL;
    }

    /**
     * Resolve the given method value to an {@code HttpMethod}.
     * @param method the method value as a String
     * @return the corresponding {@code HttpMethod}, or {@code null} if not found
     * @since 0.0.2
     */
    public static HttpMethod resolve(String method) {
        return (method != null ? mappings.get(method.toUpperCase()) : null);
    }


    /**
     * Determine whether this {@code HttpMethod} matches the given
     * method value.
     * @param method the method value as a String
     * @return {@code true} if it matches, {@code false} otherwise
     * @since 0.0.2
     */
    public boolean matches(String method) {
        return (this == resolve(method));
    }

    public boolean matches(HttpMethod method) {
        return (this == method);
    }
}