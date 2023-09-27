/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mvc.router;

import com.truthbean.debbie.io.MediaType;
import com.truthbean.debbie.io.MediaTypeInfo;
import com.truthbean.debbie.io.MultipartFile;
import com.truthbean.debbie.lang.Copyable;
import com.truthbean.debbie.mvc.request.HttpMethod;
import com.truthbean.debbie.mvc.request.RouterRequest;
import com.truthbean.debbie.mvc.response.RouterResponse;
import com.truthbean.debbie.mvc.url.RouterPathFragments;
import com.truthbean.debbie.reflection.ExecutableArgument;
import com.truthbean.debbie.reflection.ReflectionHelper;
import com.truthbean.debbie.reflection.TypeHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-01-07 23:21
 */
public class RouterInfo implements Copyable {

    private RouterAnnotationInfo annotationInfo;

    private RouterExecutor executor;

    private List<RouterPathFragments> paths;

    private RouterRequest request;

    private RouterResponse response;
    private Collection<MediaTypeInfo> defaultResponseTypes;

    public RouterInfo() {
    }

    public RouterInfo(RouterExecutor executor) {
        this.executor = executor;
    }

    public void setExecutor(RouterExecutor executor) {
        this.executor = executor;
    }

    public RouterExecutor getExecutor() {
        return executor;
    }

    public boolean hasExecutor() {
        return this.executor != null;
    }

    public boolean returnVoid() {
        return this.executor.returnVoid();
    }

    public RouterAnnotationInfo getAnnotationInfo() {
        return annotationInfo;
    }

    public void setAnnotationInfo(RouterAnnotationInfo annotationInfo) {
        this.annotationInfo = annotationInfo;
    }

    public List<RouterPathFragments> getPaths() {
        return paths;
    }

    public void setPaths(List<RouterPathFragments> paths) {
        this.paths = paths;
    }

    public List<HttpMethod> getRequestMethod() {
        return Arrays.asList(this.annotationInfo.method());
    }

    public RouterResponse getResponse() {
        return response;
    }

    public void setResponse(RouterResponse response) {
        this.response = response;
    }

    public MediaType getRequestType() {
        return this.annotationInfo.requestType();
    }

    public void setRequestType(MediaType requestType) {
        this.annotationInfo.setRequestType(requestType);
    }

    public RouterRequest getRequest() {
        return request;
    }

    public void setRequest(RouterRequest request) {
        this.request = request;
    }

    public Collection<MediaTypeInfo> getDefaultResponseTypes() {
        return defaultResponseTypes;
    }

    public void setDefaultResponseTypes(Collection<MediaTypeInfo> defaultResponseTypes) {
        this.defaultResponseTypes = defaultResponseTypes;
    }

    public boolean hasDefaultResponseType() {
        return this.defaultResponseTypes != null && !this.defaultResponseTypes.isEmpty();
    }

    public MediaTypeInfo getDefaultResponseType() {
        if (hasDefaultResponseType()) {
            return this.defaultResponseTypes.iterator().next();
        }
        return MediaType.ANY.info();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RouterInfo)) {
            return false;
        }
        RouterInfo that = (RouterInfo) o;
        return Objects.equals(executor, that.executor) &&
                Objects.equals(paths, that.paths) &&
                getRequestMethod() == that.getRequestMethod() &&

                Objects.equals(request, that.request) &&
                Objects.equals(response, that.response);
    }

    @Override
    public int hashCode() {
        return Objects.hash(executor, paths, getRequestMethod(), response, request);
    }

    @Override
    public String toString() {
        return "{" +
                "\"executor\":" + executor +
                ",\"paths\":" + paths +
                ",\"requestMethod\":" + getRequestMethod() +
                ",\"response\":" + response +
                ",\"request\":" + request +
                '}';
    }

    /**
     * deep clone
     * @return RouterInfo clone
     */
    @Override
    public RouterInfo copy() {
        RouterInfo clone = new RouterInfo();
        // todo clone
        clone.executor = this.executor;
        clone.annotationInfo = this.annotationInfo;

        if (paths != null)
            clone.paths = new ArrayList<>(paths);

        if (response != null) {
            clone.response = response.copy();
        }

        clone.setRequestType(getRequestType());
        if (request != null) {
            var copy = request.copy();
            if (copy != null) {
                Map<String, List<String>> pathAttributes = request.getPathAttributes();
                if (pathAttributes != null && !pathAttributes.isEmpty()) {
                    copy.setPathAttributes(pathAttributes);
                }
                /*Map<String, List<String>> matrix = request.getMatrix();
                if (matrix != null && !matrix.isEmpty()) {
                    copy.getMatrix().putAll(matrix);
                }*/
                clone.request = copy;
            }
        }

        if (defaultResponseTypes != null && !defaultResponseTypes.isEmpty()) {
            clone.defaultResponseTypes = new ArrayList<>(defaultResponseTypes);
        }
        return clone;
    }

    /*public static class RouterJsonInfo {
        private String errorInfo;

        private String method;

        private List<String> methodParams;

        private String routerClass;

        private List<String> paths;

        private List<String> requestMethod;

        private String requestType;
        private RouterRequest request;

        private RouterResponse.RouterJsonResponse response;

        public String getErrorInfo() {
            return errorInfo;
        }

        public String getMethod() {
            return method;
        }

        public List<String> getMethodParams() {
            return methodParams;
        }

        public String getRouterClass() {
            return routerClass;
        }

        public List<String> getPaths() {
            return paths;
        }

        public List<String> getRequestMethod() {
            return requestMethod;
        }

        public String getRequestType() {
            return requestType;
        }

        public RouterRequest getRequest() {
            return request;
        }

        public RouterResponse.RouterJsonResponse getResponse() {
            return response;
        }

        @Override
        public String toString() {
            return "{" +
                    "\"errorInfo\":\"" + errorInfo + '\"' +
                    ",\"method\":" + method +
                    ",\"methodParams\":" + methodParams +
                    ",\"routerClass\":" + routerClass +
                    ",\"paths\":" + paths +
                    ",\"requestMethod\":" + requestMethod +
                    ",\"requestType\":" + requestType +
                    ",\"request\":" + request +
                    ",\"response\":" + response +
                    '}';
        }
    }

    public RouterJsonInfo toJsonInfo() {
        var jsonInfo = new RouterJsonInfo();
        jsonInfo.method = getMethod().toString();

        if (getMethodParams() != null) {
            jsonInfo.methodParams = new ArrayList<>();
            for (ExecutableArgument methodParam : getMethodParams()) {
                jsonInfo.methodParams.add(methodParam.getType().getTypeName());
            }
        }

        jsonInfo.routerClass = getRouterClass().getName();

        if (paths != null) {
            jsonInfo.paths = new ArrayList<>();
            for (RouterPathFragments path : paths) {
                jsonInfo.paths.add(path.getRawPath());
            }
        }

        if (getRequestMethod() != null) {
            jsonInfo.requestMethod = new ArrayList<>();
            for (HttpMethod httpMethod : getRequestMethod()) {
                jsonInfo.requestMethod.add(httpMethod.name());
            }
        }

        if (response != null) {
            jsonInfo.response = response.toJsonInfo();
        }

        jsonInfo.requestType = getRequestType().getValue();
        if (request != null) {
            var copy = request.copy();
            if (copy != null)
                jsonInfo.request = copy;
        }

        return jsonInfo;
    }*/
}
