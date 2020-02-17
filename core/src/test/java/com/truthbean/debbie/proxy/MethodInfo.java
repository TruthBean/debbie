package com.truthbean.debbie.proxy;

import org.objectweb.asm.Type;

import java.lang.reflect.Method;

public class MethodInfo {
    private int access;
    private String name;
    private String descriptor;
    private String signature;
    private String[] exceptions;

    private boolean isInterface;

    public MethodInfo() {
    }

    public MethodInfo(Method method) {
        this.access = method.getModifiers();
        this.name = method.getName();
        this.descriptor = Type.getMethodDescriptor(method);
        this.signature = null;
        this.exceptions = null;

        this.isInterface = method.getDeclaringClass().isInterface();
    }

    public int getAccess() {
        return access;
    }

    public void setAccess(int access) {
        this.access = access;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public void setDescriptor(String descriptor) {
        this.descriptor = descriptor;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String[] getExceptions() {
        return exceptions;
    }

    public void setExceptions(String[] exceptions) {
        this.exceptions = exceptions;
    }

    public boolean isInterface() {
        return isInterface;
    }

    public void setInterface(boolean anInterface) {
        isInterface = anInterface;
    }
}
