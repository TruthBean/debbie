/**
 * Copyright (c) 2022 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mvc.request;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-03-11 13:20
 */
public enum RequestParameterType {

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
    COOKIE,

    /**
     * inner attribute
     */
    INNER

}
