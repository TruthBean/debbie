/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mvc.response;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class NoSuchHttpStatusException extends RuntimeException {

    public NoSuchHttpStatusException(int status) {
        super("no such http status (" + status + ")");
    }

    public NoSuchHttpStatusException(int status, String message) {
        super("no such http status (" + status + "); " + message);
    }

    public NoSuchHttpStatusException(int status, String message, Throwable cause) {
        super("no such http status (" + status + "); " + message, cause);
    }

    public NoSuchHttpStatusException(int status, Throwable cause) {
        super("no such http status (" + status + "); ", cause);
    }

    public NoSuchHttpStatusException(int status, String message, Throwable cause, boolean enableSuppression,
                                     boolean writableStackTrace) {
        super("no such http status (" + status + "); " + message, cause, enableSuppression, writableStackTrace);
    }
}
