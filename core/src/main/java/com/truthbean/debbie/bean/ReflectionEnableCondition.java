package com.truthbean.debbie.bean;

import com.truthbean.debbie.environment.Environment;
import com.truthbean.debbie.reflection.ReflectionConfigurer;

public class ReflectionEnableCondition implements BeanCondition {

    public static final ReflectionEnableCondition INSTANCE = new ReflectionEnableCondition();

    @Override
    public boolean matches(Environment environment) {
        return ReflectionConfigurer.isReflectEnable(environment);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
