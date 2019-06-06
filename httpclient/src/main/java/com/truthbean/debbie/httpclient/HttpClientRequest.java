package com.truthbean.debbie.httpclient;

import com.truthbean.debbie.reflection.ExecutableArgument;
import com.truthbean.debbie.mvc.request.DefaultRouterRequest;

import java.util.UUID;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class HttpClientRequest extends DefaultRouterRequest {
    private ExecutableArgument invokedParameter;

    public HttpClientRequest() {
        super.setId(UUID.randomUUID().toString());
    }

    public ExecutableArgument getInvokedParameter() {
        return invokedParameter;
    }

    public void setInvokedParameter(ExecutableArgument invokedParameter) {
        this.invokedParameter = invokedParameter;
    }
}
