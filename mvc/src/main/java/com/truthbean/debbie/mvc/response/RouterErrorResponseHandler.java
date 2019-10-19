package com.truthbean.debbie.mvc.response;

import com.truthbean.debbie.io.MediaType;
import com.truthbean.debbie.util.JacksonUtils;
import com.truthbean.debbie.mvc.request.RouterRequest;
import com.truthbean.debbie.mvc.router.RouterInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public final class RouterErrorResponseHandler {

    private RouterErrorResponseHandler() {
    }

    public static RouterInfo resourceNotFound(RouterRequest httpRequest) {
        ResponseEntity<Object> data = ResponseHelper.resourcesNotFound();
        Map<String, String> value = new HashMap<>();
        value.put("uri", httpRequest.getUrl());
        value.put("timestamp", String.valueOf(System.currentTimeMillis()));
        value.put("method", httpRequest.getMethod().name());
        data.setData(value);

        return error(httpRequest, data);
    }

    public static RouterInfo exception(RouterRequest httpRequest, Exception e) {
        ResponseEntity<Object> data = ResponseHelper.error(e.getMessage());
        Map<String, Object> value = new HashMap<>();
        value.put("uri", httpRequest.getUrl());
        value.put("timestamp", String.valueOf(System.currentTimeMillis()));
        value.put("exception", e);
        value.put("method", httpRequest.getMethod().name());
        data.setData(value);

        return error(httpRequest, data);
    }

    public static RouterInfo error(RouterRequest httpRequest, ResponseEntity data) {
        RouterInfo error = new RouterInfo();

        RouterResponse routerResponse = new RouterResponse();
        if (httpRequest.getResponseType().toMediaType() == MediaType.APPLICATION_XML) {
            routerResponse.setResponseType(httpRequest.getResponseType());
            error.setErrorInfo(JacksonUtils.toXml(data));
        } else {
            routerResponse.setResponseType(MediaType.APPLICATION_JSON);
            error.setErrorInfo(JacksonUtils.toJson(data));
        }
        error.setResponse(routerResponse);

        return error;
    }
}
