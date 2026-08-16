/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.httpclient;

/**
 * Runtime exception thrown when an HTTP client operation fails
 * (e.g. connection refused, timeout, or unexpected response).
 *
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-10-13 18:53
 */
public class HttpClientException extends RuntimeException {
    /** Creates a new exception with no message or cause. */
    public HttpClientException() {
    }

    /** Creates a new exception with the given detail message. */
    public HttpClientException(String message) {
        super(message);
    }

    /** Creates a new exception with the given message and cause. */
    public HttpClientException(String message, Throwable cause) {
        super(message, cause);
    }

    /** Creates a new exception with the given cause. */
    public HttpClientException(Throwable cause) {
        super(cause);
    }

    /** Creates a new exception with full control over suppression and stack trace. */
    public HttpClientException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
