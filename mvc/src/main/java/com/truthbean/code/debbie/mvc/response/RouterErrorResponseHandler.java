package com.truthbean.code.debbie.mvc.response;

import com.truthbean.code.debbie.core.io.MediaType;
import com.truthbean.code.debbie.core.util.JacksonUtils;
import com.truthbean.code.debbie.mvc.request.RouterRequest;
import com.truthbean.code.debbie.mvc.router.RouterInfo;

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
        RouterInfo resourcesNotFound = new RouterInfo();
        ResponseEntity<Map<String, String>> data = ResponseHelper.resourcesNotFound();
        Map<String, String> value = new HashMap<>();
        value.put("url", httpRequest.getUrl());
        value.put("timestamp", String.valueOf(System.currentTimeMillis()));
        data.setData(value);

        RouterInvokeResult routerResponse = new RouterInvokeResult();
        if (httpRequest.getResponseType() == MediaType.APPLICATION_XML) {
            routerResponse.setResponseType(httpRequest.getResponseType());
            resourcesNotFound.setErrorInfo(JacksonUtils.toXml(data));
        } else {
            routerResponse.setResponseType(MediaType.APPLICATION_JSON);
            resourcesNotFound.setErrorInfo(JacksonUtils.toJson(data));
        }
        resourcesNotFound.setResponse(routerResponse);

        resourcesNotFound.setRequest(httpRequest.clone());

        return resourcesNotFound;
    }

    public static RouterInfo exception(RouterRequest httpRequest, Exception e) {
        RouterInfo resourcesNotFound = new RouterInfo();
        ResponseEntity data = ResponseHelper.error(e.getMessage());
        Map<String, Object> value = new HashMap<>();
        value.put("url", httpRequest.getUrl());
        value.put("timestamp", String.valueOf(System.currentTimeMillis()));
        value.put("exception", e);
        data.setData(value);

        RouterInvokeResult routerResponse = new RouterInvokeResult();
        if (httpRequest.getResponseType() == MediaType.APPLICATION_XML) {
            routerResponse.setResponseType(httpRequest.getResponseType());
            resourcesNotFound.setErrorInfo(JacksonUtils.toXml(data));
        } else {
            routerResponse.setResponseType(MediaType.APPLICATION_JSON);
            resourcesNotFound.setErrorInfo(JacksonUtils.toJson(data));
        }
        resourcesNotFound.setResponse(routerResponse);

        return resourcesNotFound;
    }
}
