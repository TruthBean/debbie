package com.truthbean.code.debbie.core.reflection.asm;

import org.objectweb.asm.Type;

import java.lang.reflect.Modifier;

/**
 * @author 璩诗斌
 * @since 0.0.1
 */
public class MethodStateInfo extends AsmMethodInfo {
    private int access;
    private Type[] argumentTypes;
    private int localOffset;

    public int getAccess() {
        return access;
    }

    public void setAccess(int access) {
        this.access = access;
        this.localOffset = Modifier.isStatic(access) ? 0 : 1;
    }

    public Type[] getArgumentTypes() {
        return argumentTypes;
    }

    public void setArgumentTypes(Type[] argumentTypes) {
        this.argumentTypes = argumentTypes;
    }

    public int getLocalOffset() {
        return localOffset;
    }

}
