package com.truthbean.code.debbie.mvc.router;

import com.truthbean.code.debbie.core.io.MultipartFile;
import com.truthbean.code.debbie.core.reflection.InvokedParameter;
import com.truthbean.code.debbie.core.reflection.ReflectionHelper;
import com.truthbean.code.debbie.core.reflection.TypeHelper;
import com.truthbean.code.debbie.mvc.request.HttpMethod;
import com.truthbean.code.debbie.mvc.request.RouterRequest;
import com.truthbean.code.debbie.mvc.response.RouterInvokeResultData;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-01-07 23:21
 */
public class RouterInfo implements Cloneable {

    private String errorInfo;

    private Method method;

    private List<InvokedParameter> methodParams;

    private Class<?> routerClass;

    private List<Pattern> paths;

    private HttpMethod requestMethod;

    private RouterInvokeResultData response;

    private RouterRequest request;

    private boolean hasTemplate;
    private String templateSuffix;
    private String templatePrefix;

    public boolean hasTemplate() {
        return hasTemplate;
    }

    public void setHasTemplate(boolean hasTemplate) {
        this.hasTemplate = hasTemplate;
    }

    public String getTemplateSuffix() {
        return templateSuffix;
    }

    public void setTemplateSuffix(String templateSuffix) {
        this.templateSuffix = templateSuffix;
    }

    public String getTemplatePrefix() {
        return templatePrefix;
    }

    public void setTemplatePrefix(String templatePrefix) {
        this.templatePrefix = templatePrefix;
    }

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

    public List<InvokedParameter> getMethodParams() {
        return methodParams;
    }

    public void setMethodParams(List<InvokedParameter> methodParams) {
        this.methodParams = methodParams;
    }

    public Class<?> getRouterClass() {
        return routerClass;
    }

    public void setRouterClass(Class<?> routerClass) {
        this.routerClass = routerClass;
    }

    public List<Pattern> getPaths() {
        return paths;
    }

    public void setPaths(List<Pattern> paths) {
        this.paths = paths;
    }

    public HttpMethod getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(HttpMethod requestMethod) {
        this.requestMethod = requestMethod;
    }

    public RouterInvokeResultData getResponse() {
        return response;
    }

    public void setResponse(RouterInvokeResultData response) {
        this.response = response;
    }

    public RouterRequest getRequest() {
        return request;
    }

    public void setRequest(RouterRequest request) {
        this.request = request;
    }

    public RouterInfo() {
    }

    /*public RouterInfo(Method method, List<InvokedParameter> methodParams, Class<?> clazz, Pattern pathRegex,
                      HttpMethod requestMethod, MediaType responseType, AbstractResponseHandler abstractHandlerFilter) {
        this.method = method;
        this.methodParams = methodParams;
        this.clazz = clazz;
        this.pathRegex = pathRegex;
        this.requestMethod = requestMethod;
        this.responseType = responseType;
        this.responseHandler = abstractHandlerFilter;
    }*/

    private final List<InvokedParameter> baseTypeMethodParams = new ArrayList<>();
    private final List<InvokedParameter> notBaseTypeMethodParams = new ArrayList<>();

    public void setBaseTypeMethodParams() {
        RouterInvokedParameterHandler handler = new RouterInvokedParameterHandler();
        for (InvokedParameter param : methodParams) {
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

    public List<InvokedParameter> getBaseTypeMethodParams() {
        if (baseTypeMethodParams.isEmpty()) {
            setBaseTypeMethodParams();
        }
        return baseTypeMethodParams;
    }

    public void setNotBaseTypeMethodParams() {
        for (InvokedParameter param : methodParams) {
            if (!TypeHelper.isBaseType(param.getType()) && param.getType() != MultipartFile.class) {
                baseTypeMethodParams.add(param);
            }
        }
    }

    public List<InvokedParameter> getNotBaseTypeMethodParams() {
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

                Objects.equals(response, that.response) &&
                Objects.equals(request, that.request) &&

                Objects.equals(baseTypeMethodParams, that.baseTypeMethodParams) &&
                Objects.equals(notBaseTypeMethodParams, that.notBaseTypeMethodParams) &&
                Objects.equals(templatePrefix, that.templatePrefix) &&
                Objects.equals(templateSuffix, that.templateSuffix);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, methodParams, routerClass, paths, requestMethod, response,
                request, baseTypeMethodParams, notBaseTypeMethodParams, templatePrefix, templateSuffix);
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
                ",\"templatePrefix\":" + templatePrefix +
                ",\"templateSuffix\":" + templateSuffix +
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
        clone.paths = paths;
        clone.requestMethod = requestMethod;
        if (response != null) {
            clone.response = response;
        }
        if (request != null) {
            clone.request = request.clone();
        }
        clone.templatePrefix = templatePrefix;
        clone.templateSuffix = templateSuffix;
        return clone;
    }
}
