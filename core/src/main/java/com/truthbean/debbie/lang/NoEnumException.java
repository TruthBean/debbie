/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.lang;

/**
 * @author TruthBean
 * @since 0.0.2
 * Created on 2019-11-25 19:09.
 */
public class NoEnumException extends RuntimeException {
    public NoEnumException() {
    }

    public NoEnumException(String message) {
        super(message);
    }

    public NoEnumException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoEnumException(Throwable cause) {
        super(cause);
    }

    public NoEnumException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
