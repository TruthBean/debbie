/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mvc.response;

import com.truthbean.debbie.mvc.request.HttpHeader;

import java.util.HashMap;
import java.util.Map;

/**
 * @author TruthBean
 * @since 0.5.2
 */
public class BaseRouterResponse {
    private final Map<String, String> headers = new HashMap<>();

    private Object content;

    private HttpStatus status;

    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    public void addHeaders(Map<String, String> headers) {
        if (headers != null && !headers.isEmpty()) {
            this.headers.putAll(headers);
        }
    }

    public void addHeader(HttpHeader.HttpHeaderName headerName, String value) {
        headers.put(headerName.getName(), value);
    }

    public Map<String, String> getHeaders() {
        return Map.copyOf(headers);
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public void copyFrom(BaseRouterResponse response) {
        this.headers.putAll(response.headers);

        this.content = response.content;

        this.status = response.status;
    }

    public void copyNoNull(BaseRouterResponse response) {
        this.headers.putAll(response.headers);

        if (response.content != null)
            this.content = response.content;

        if (response.status != null)
            this.status = response.status;
    }

    public BaseRouterResponse cloneObject() {
        BaseRouterResponse response;
        try {
            response = (BaseRouterResponse) super.clone();
        } catch (CloneNotSupportedException e) {
            response = new BaseRouterResponse();
        }

        response.headers.putAll(this.headers);

        response.content = this.content;

        response.status = this.status;

        return response;
    }

    @Override
    public String toString() {
        return "\"headers\":" + headers + "," + "\"content\":" + content + "," + "\"status\":" + status;
    }
}
