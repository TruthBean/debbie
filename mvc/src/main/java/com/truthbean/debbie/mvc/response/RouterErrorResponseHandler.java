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

import com.truthbean.debbie.io.MediaType;
import com.truthbean.debbie.mvc.request.RouterRequest;
import com.truthbean.debbie.mvc.router.RouterInfo;
import com.truthbean.debbie.util.JacksonUtils;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public final class RouterErrorResponseHandler {

    private RouterErrorResponseHandler() {
    }

    public static RouterInfo resourceNotFound(RouterRequest routerRequest) {
        ResponseEntity<Object> data = ResponseHelper.resourcesNotFound();

        var value = new ErrorResponseData();
        value.setUri(routerRequest.getUrl());
        value.setTimestamp(System.currentTimeMillis());
        value.setMethod(routerRequest.getMethod().name());
        data.setData(value);

        return error(routerRequest, data);
    }

    public static RouterInfo exception(RouterRequest routerRequest, Throwable e) {
        ResponseEntity<Object> data = ResponseHelper.error(e.getMessage());

        var value = new ErrorResponseData();
        value.setUri(routerRequest.getUrl());
        value.setTimestamp(System.currentTimeMillis());
        value.setException(e);
        value.setMethod(routerRequest.getMethod().name());
        data.setData(value);

        return error(routerRequest, data);
    }

    public static RouterInfo error(RouterRequest routerRequest, ResponseEntity data) {
        RouterInfo error = new RouterInfo();
        error.setRequest(routerRequest);

        RouterResponse routerResponse = new RouterResponse();
        routerResponse.setError(true);
        routerResponse.setContent(data);
        error.setResponse(routerResponse);

        return error;
    }

    public static RouterResponse handleError(RouterInfo error, ErrorResponseCallback callback) {
        RouterRequest request = error.getRequest();
        RouterResponse response = error.getResponse();
        Object data = response.getContent();

        if (callback == null) {
            response.setStatus(HttpStatus.OK);
            if (request.getResponseType().isSameMediaType(MediaType.APPLICATION_XML)) {
                String xmlValue = JacksonUtils.toXml(data);
                response.setResponseType(request.getResponseType());
                response.setContent(xmlValue);
            } else {
                response.setResponseType(MediaType.APPLICATION_JSON_UTF8);
                response.setContent(JacksonUtils.toJson(data));
            }
        } else
            callback.callback(response);
        return response;
    }
}
