package com.truthbean.debbie.mvc.router;

import com.truthbean.debbie.io.MediaType;
import com.truthbean.debbie.io.MultipartFile;
import com.truthbean.debbie.reflection.ExecutableArgument;
import com.truthbean.debbie.reflection.ReflectionHelper;
import com.truthbean.debbie.reflection.TypeHelper;
import com.truthbean.debbie.mvc.request.HttpMethod;
import com.truthbean.debbie.mvc.request.RouterRequest;
import com.truthbean.debbie.mvc.response.RouterResponse;
import com.truthbean.debbie.mvc.url.RouterPathFragments;
import com.truthbean.debbie.util.JacksonUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-01-07 23:21
 */
public class RouterInfo implements Cloneable {

    private String errorInfo;

    private Method method;

    private List<ExecutableArgument> methodParams;

    private Class<?> routerClass;
    private Object routerInstance;

    private List<RouterPathFragments> paths;

    private List<HttpMethod> requestMethod;

    private MediaType requestType;
    private RouterRequest request;

    private RouterResponse response;

    public String getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public List<ExecutableArgument> getMethodParams() {
        return methodParams;
    }

    public void setMethodParams(List<ExecutableArgument> methodParams) {
        this.methodParams = methodParams;
    }

    public Class<?> getRouterClass() {
        return routerClass;
    }

    public void setRouterClass(Class<?> routerClass) {
        this.routerClass = routerClass;
    }

    public Object getRouterInstance() {
        return routerInstance;
    }

    public void setRouterInstance(Object routerInstance) {
        this.routerInstance = routerInstance;
    }

    public List<RouterPathFragments> getPaths() {
        return paths;
    }

    public void setPaths(List<RouterPathFragments> paths) {
        this.paths = paths;
    }

    public List<HttpMethod> getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(List<HttpMethod> requestMethod) {
        this.requestMethod = requestMethod;
    }

    public RouterResponse getResponse() {
        return response;
    }

    public void setResponse(RouterResponse response) {
        this.response = response;
    }

    public MediaType getRequestType() {
        return requestType;
    }

    public void setRequestType(MediaType requestType) {
        this.requestType = requestType;
    }

    public RouterRequest getRequest() {
        return request;
    }

    public void setRequest(RouterRequest request) {
        this.request = request;
    }

    public RouterInfo() {
    }

    private final List<ExecutableArgument> baseTypeMethodParams = new ArrayList<>();
    private final List<ExecutableArgument> notBaseTypeMethodParams = new ArrayList<>();

    public void setBaseTypeMethodParams() {
        RouterMethodArgumentHandler handler = new RouterMethodArgumentHandler();
        for (ExecutableArgument param : methodParams) {
            if (TypeHelper.isBaseType(param.getType()) || param.getType() == MultipartFile.class) {
                baseTypeMethodParams.add(param);
            } else {
                List<Field> fields = ReflectionHelper.getDeclaredFields(param.getType());
                int i = 0;
                while (i < fields.size()) {
                    baseTypeMethodParams.add(handler.typeOf(fields.get(i), i++));
                }
            }
        }
    }

    public List<ExecutableArgument> getBaseTypeMethodParams() {
        if (baseTypeMethodParams.isEmpty()) {
            setBaseTypeMethodParams();
        }
        return baseTypeMethodParams;
    }

    public void setNotBaseTypeMethodParams() {
        for (ExecutableArgument param : methodParams) {
            if (!TypeHelper.isBaseType(param.getType()) && param.getType() != MultipartFile.class) {
                baseTypeMethodParams.add(param);
            }
        }
    }

    public List<ExecutableArgument> getNotBaseTypeMethodParams() {
        if (notBaseTypeMethodParams.isEmpty()) {
            setNotBaseTypeMethodParams();
        }
        return notBaseTypeMethodParams;
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
        return Objects.equals(method, that.method) &&
                Objects.equals(methodParams, that.methodParams) &&
                Objects.equals(routerClass, that.routerClass) &&
                Objects.equals(paths, that.paths) &&
                requestMethod == that.requestMethod &&

                Objects.equals(request, that.request) &&
                Objects.equals(response, that.response) &&

                Objects.equals(baseTypeMethodParams, that.baseTypeMethodParams) &&
                Objects.equals(notBaseTypeMethodParams, that.notBaseTypeMethodParams);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, methodParams, routerClass, paths, requestMethod, response,
                request, baseTypeMethodParams, notBaseTypeMethodParams);
    }

    @Override
    public String toString() {
        return "{" +
                "\"method\":" + method +
                ",\"methodParams\":" + methodParams +
                ",\"routerClass\":" + routerClass +
                ",\"paths\":" + paths +
                ",\"requestMethod\":" + requestMethod +
                ",\"response\":" + response +
                ",\"request\":" + request +
                ",\"baseTypeMethodParams\":" + baseTypeMethodParams +
                ",\"notBaseTypeMethodParams\":" + notBaseTypeMethodParams +
                '}';
    }

    /**
     * deep clone
     * @return RouterInfo clone
     */
    @Override
    public RouterInfo clone() {
        RouterInfo clone = new RouterInfo();
        clone.errorInfo = errorInfo;
        clone.method = method;
        if (methodParams != null) {
            clone.methodParams = new ArrayList<>(methodParams);
        }
        clone.routerClass = routerClass;
        clone.routerInstance = routerInstance;

        if (paths != null)
            clone.paths = new ArrayList<>(paths);

        if (requestMethod != null)
            clone.requestMethod = new ArrayList<>(requestMethod);

        if (response != null) {
            clone.response = response.clone();
        }

        clone.requestType = requestType;
        if (request != null) {
            clone.request = request.copy();
        }
        return clone;
    }

    public static class RouterJsonInfo {
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
        jsonInfo.errorInfo = errorInfo;
        jsonInfo.method = method.toString();

        if (methodParams != null) {
            jsonInfo.methodParams = new ArrayList<>();
            for (ExecutableArgument methodParam : methodParams) {
                jsonInfo.methodParams.add(methodParam.getType().getName());
            }
        }

        jsonInfo.routerClass = routerClass.getName();

        if (paths != null) {
            jsonInfo.paths = new ArrayList<>();
            for (RouterPathFragments path : paths) {
                jsonInfo.paths.add(path.getRawPath());
            }
        }

        if (requestMethod != null) {
            jsonInfo.requestMethod = new ArrayList<>();
            for (HttpMethod httpMethod : requestMethod) {
                jsonInfo.requestMethod.add(httpMethod.name());
            }
        }

        if (response != null) {
            jsonInfo.response = response.toJsonInfo();
        }

        jsonInfo.requestType = requestType.getValue();
        if (request != null) {
            jsonInfo.request = request.copy();
        }

        return jsonInfo;
    }
}
