/**
 * Copyright (c) 2024 TruthBean(Rogar·Q)
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
        this.invokedParameters.sort(ExecutableArgument::compareTo);
    }

    public void addInvokedParameters(Collection<ExecutableArgument> invokedParameters) {
        if (invokedParameters != null && !invokedParameters.isEmpty())
            this.invokedParameters.addAll(invokedParameters);
    }
}
