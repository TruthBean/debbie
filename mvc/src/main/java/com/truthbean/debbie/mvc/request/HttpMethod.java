package com.truthbean.debbie.mvc.request;

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


    private static String GET_HTTP_1_1 = "GET / HTTP/1.1";

    public static HttpMethod of(String name) {
        HttpMethod[] values = values();
        for (HttpMethod value : values) {
            if (name.equals(value.name())) {
                return value;
            }
        }
        return ALL;
    }
}