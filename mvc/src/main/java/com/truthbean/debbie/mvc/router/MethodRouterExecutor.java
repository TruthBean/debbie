/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mvc.router;

import com.truthbean.debbie.io.MultipartFile;
import com.truthbean.debbie.reflection.ExecutableArgument;
import com.truthbean.debbie.reflection.ReflectionHelper;
import com.truthbean.debbie.reflection.TypeHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.2.0
 * Created on 2020-11-27 14:16
 */
public class MethodRouterExecutor implements RouterExecutor {
    private Method method;

    private List<ExecutableArgument> methodParams;

    private Class<?> routerClass;
    private Object routerInstance;

    private final List<ExecutableArgument> baseTypeMethodParams = new ArrayList<>();
    private final List<ExecutableArgument> notBaseTypeMethodParams = new ArrayList<>();

    public void setBaseTypeMethodParams(ClassLoader classLoader) {
        RouterMethodArgumentHandler handler = new RouterMethodArgumentHandler(classLoader);
        for (ExecutableArgument param : getMethodParams()) {
            if (TypeHelper.isBaseType(param.getType()) || param.getType() == MultipartFile.class) {
                baseTypeMethodParams.add(param);
            } else {
                List<Field> fields = ReflectionHelper.getDeclaredFields(param.getRawType());
                int i = 0;
                while (i < fields.size()) {
                    baseTypeMethodParams.add(handler.typeOf(fields.get(i), i++));
                }
            }
        }
    }

    public List<ExecutableArgument> getBaseTypeMethodParams(ClassLoader classLoader) {
        if (baseTypeMethodParams.isEmpty()) {
            setBaseTypeMethodParams(classLoader);
        }
        return baseTypeMethodParams;
    }

    public void setNotBaseTypeMethodParams() {
        for (ExecutableArgument param : getMethodParams()) {
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
    public Object execute(Object... params) throws Throwable {
        return ReflectionHelper.invokeMethod(true, routerInstance, method, params);
    }

    @Override
    public boolean returnVoid() {
        return method.getReturnType() == void.class || method.getReturnType() == Void.class;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MethodRouterExecutor that = (MethodRouterExecutor) o;
        return Objects.equals(method, that.method) &&
                Objects.equals(methodParams, that.methodParams) &&
                Objects.equals(routerClass, that.routerClass) &&
                Objects.equals(baseTypeMethodParams, that.baseTypeMethodParams) &&
                Objects.equals(notBaseTypeMethodParams, that.notBaseTypeMethodParams);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, methodParams, routerClass, baseTypeMethodParams, notBaseTypeMethodParams);
    }

    @Override
    public String toString() {
        return "{" + "\"method\":" + method + "," +
                "\"methodParams\":" + methodParams + "," +
                "\"routerClass\":" + routerClass + "," +
                "\"routerInstance\":" + routerInstance + "," +
                "\"baseTypeMethodParams\":" + baseTypeMethodParams + "," +
                "\"notBaseTypeMethodParams\":" + notBaseTypeMethodParams + "}";
    }
}
