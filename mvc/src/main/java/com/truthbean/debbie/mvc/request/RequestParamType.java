package com.truthbean.debbie.mvc.request;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-03-11 13:20
 */
public enum RequestParamType {

    /**
     * query
     */
    QUERY,

    /**
     * uri matrix
     */
    MATRIX,

    /**
     * uri path
     */
    PATH,

    /**
     * params
     */
    PARAM,

    /**
     * mix all
     */
    MIX,

    /**
     * request body
     */
    BODY,
    /**
     * request head
     */
    HEAD,

    /**
     * session attribute
     */
    SESSION,

    /**
     * cookie attribute
     */
    COOKIE

}
