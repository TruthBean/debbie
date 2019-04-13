package com.truthbean.code.debbie.httpclient;

import com.truthbean.code.debbie.core.reflection.InvokedParameter;
import com.truthbean.code.debbie.mvc.request.DefaultRouterRequest;

public class HttpClientRequest extends DefaultRouterRequest {
    private InvokedParameter invokedParameter;

    public InvokedParameter getInvokedParameter() {
        return invokedParameter;
    }

    public void setInvokedParameter(InvokedParameter invokedParameter) {
        this.invokedParameter = invokedParameter;
    }
}
