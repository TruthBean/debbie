package com.truthbean.debbie.httpclient;

import com.truthbean.debbie.core.reflection.InvokedParameter;
import com.truthbean.debbie.mvc.request.DefaultRouterRequest;

import java.util.UUID;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class HttpClientRequest extends DefaultRouterRequest {
    private InvokedParameter invokedParameter;

    public HttpClientRequest() {
        super.setId(UUID.randomUUID().toString());
    }

    public InvokedParameter getInvokedParameter() {
        return invokedParameter;
    }

    public void setInvokedParameter(InvokedParameter invokedParameter) {
        this.invokedParameter = invokedParameter;
    }
}
