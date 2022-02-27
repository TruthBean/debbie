/**
 * Copyright (c) 2022 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mvc.exception;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class RouterFilterMappingFormatException extends RuntimeException {
    public RouterFilterMappingFormatException() {
    }

    public RouterFilterMappingFormatException(String message) {
        super(message);
    }

    public RouterFilterMappingFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public RouterFilterMappingFormatException(Throwable cause) {
        super(cause);
    }

    public RouterFilterMappingFormatException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
