package com.truthbean.debbie.check.reflection;

import com.truthbean.debbie.reflection.ClassLoaderUtils;

import java.util.UUID;

/**
 * @author TruthBean
 * @since 0.0.2
 * Created on 2019-11-30 14:26.
 */
public class TestParameterized extends AbstractParameterized<BeanB, ReflectionTarget> implements AAAA {
    public Class<ReflectionTarget> getTarget() {
        return ReflectionTarget.class;
    }

    @Override
    public void a() {
        System.out.println("bbbbbb");
    }
}

abstract class AbstractParameterized<A extends BeanA, S> implements AAAA, AA {

    public Class<S> getS(A a, String sClassName) {
        System.out.println(sClassName);
        System.out.println(a.getA());
        try {
            return (Class<S>) Class.forName(sClassName, false, ClassLoaderUtils.getDefaultClassLoader());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void privateMethod() {
        System.out.println("private ... ");
    }

    @Override
    public void a() {
        System.out.println("aaaaaa");
    }
}

interface AAAA {
    default void aaa() {
        System.out.println("a");
    }

    void a();
}

interface AA {
    default Integer max() {
        return testPrivateMethod();
    }

    static void test() {
        System.out.println("test ..... ");
    }

    private Integer testPrivateMethod() {
        return Integer.MAX_VALUE;
    }
}

class BeanA {
    public String getA() {
        return UUID.randomUUID().toString();
    }
}

class BeanB extends BeanA {
    public String getB() {
        return UUID.randomUUID().toString();
    }
}


