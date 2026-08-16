/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.httpclient;

import com.truthbean.debbie.mvc.request.DefaultRouterRequest;
import com.truthbean.debbie.reflection.ExecutableArgument;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Represents an outgoing HTTP request built from a router-annotated
 * interface method, carrying the URL, method, headers, cookies, and
 * invoked parameter values.
 *
 * @author TruthBean
 * @since 0.0.1
 */
public class HttpClientRequest extends DefaultRouterRequest {
    /** ordered list of method parameters to be resolved and sent */
    private final List<ExecutableArgument> invokedParameters;

    /** Creates a new request with a random UUID id. */
    public HttpClientRequest() {
        super.setId(UUID.randomUUID().toString());
        this.invokedParameters = new ArrayList<>();
    }

    /** Returns all invoked parameters. */
    public List<ExecutableArgument> getInvokedParameters() {
        return invokedParameters;
    }

    /**
     * Returns the invoked parameter at the given method argument index.
     *
     * @param index the method argument index
     * @return the matching parameter, or {@code null} if not found
     */
    public ExecutableArgument getInvokedParameter(int index) {
        for (ExecutableArgument invokedParameter : invokedParameters) {
            if (invokedParameter.getIndex() == index) {
                return invokedParameter;
            }
        }
        return null;
    }

    /** Adds a single invoked parameter if non-null. */
    public void addInvokedParameter(ExecutableArgument invokedParameter) {
        if (invokedParameter != null)
            this.invokedParameters.add(invokedParameter);
    }

    /** Sorts invoked parameters by their index. */
    public void sortInvokedParameters() {
        this.invokedParameters.sort(ExecutableArgument::compareTo);
    }

    /** Adds a collection of invoked parameters if non-null and non-empty. */
    public void addInvokedParameters(Collection<ExecutableArgument> invokedParameters) {
        if (invokedParameters != null && !invokedParameters.isEmpty())
            this.invokedParameters.addAll(invokedParameters);
    }
}
