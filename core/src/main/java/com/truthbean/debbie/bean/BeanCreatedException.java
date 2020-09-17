/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.bean;

import com.truthbean.Logger;

import java.util.Objects;

/**
 * @author truthbean
 * @since 0.0.1
 */
public class BeanCreatedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public BeanCreatedException() {
    }

    public BeanCreatedException(String message) {
        super(message);
    }

    public BeanCreatedException(String message, Throwable cause) {
        super(message, cause);
    }

    public BeanCreatedException(Throwable cause) {
        super(cause);
    }

    public BeanCreatedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public static void throwException(Logger logger, Exception e) {
        if (logger.isTraceEnabled())
            logger.trace("", e);
        var cause = e.getCause();
        var errorMessage = e.getMessage();
        if (cause != null) {
            errorMessage = cause.getMessage();
        } else {
            cause = e;
        }
        if (errorMessage != null)
            throw new BeanCreatedException(errorMessage, cause, true, true);
        else throw new BeanCreatedException("", Objects.requireNonNullElse(cause, e), true, true);
    }
}
