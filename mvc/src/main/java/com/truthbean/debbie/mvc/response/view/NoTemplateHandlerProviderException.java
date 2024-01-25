/**
 * Copyright (c) 2024 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mvc.response.view;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-02-18 22:35
 */
public class NoTemplateHandlerProviderException extends RuntimeException {
    public NoTemplateHandlerProviderException() {
    }

    public NoTemplateHandlerProviderException(String message) {
        super(message);
    }

    public NoTemplateHandlerProviderException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoTemplateHandlerProviderException(Throwable cause) {
        super(cause);
    }

    public NoTemplateHandlerProviderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
