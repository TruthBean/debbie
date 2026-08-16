/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.jdbc.datasource;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class NoDatasourceException extends RuntimeException {
    public NoDatasourceException() {
    }

    public NoDatasourceException(String message) {
        super(message);
    }

    public NoDatasourceException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoDatasourceException(Throwable cause) {
        super(cause);
    }

    public NoDatasourceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
