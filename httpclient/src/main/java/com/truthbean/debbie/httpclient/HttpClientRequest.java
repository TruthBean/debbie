package com.truthbean.debbie.httpclient;

import com.truthbean.debbie.reflection.ExecutableArgument;
import com.truthbean.debbie.mvc.request.DefaultRouterRequest;

import java.util.*;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class HttpClientRequest extends DefaultRouterRequest {
    private final List<ExecutableArgument> invokedParameters;

    public HttpClientRequest() {
        super.setId(UUID.randomUUID().toString());
        this.invokedParameters = new ArrayList<>();
    }

    public List<ExecutableArgument> getInvokedParameters() {
        return invokedParameters;
    }

    public ExecutableArgument getInvokedParameter(int index) {
        for (ExecutableArgument invokedParameter : invokedParameters) {
            if (invokedParameter.getIndex() == index) {
                return invokedParameter;
            }
        }
        return null;
    }

    public void addInvokedParameter(ExecutableArgument invokedParameter) {
        if (invokedParameter != null)
            this.invokedParameters.add(invokedParameter);
    }

    public void sortInvokedParameters() {
        this.invokedParameters.sort(null);
    }

    public void addInvokedParameters(Collection<ExecutableArgument> invokedParameters) {
        if (invokedParameters != null && !invokedParameters.isEmpty())
            this.invokedParameters.addAll(invokedParameters);
    }
}
