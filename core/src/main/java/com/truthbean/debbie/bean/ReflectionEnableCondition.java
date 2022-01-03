package com.truthbean.debbie.bean;

import com.truthbean.debbie.env.EnvironmentContent;
import com.truthbean.debbie.reflection.ReflectionConfigurer;

public class ReflectionEnableCondition implements BeanCondition {

    public static final ReflectionEnableCondition INSTANCE = new ReflectionEnableCondition();

    @Override
    public boolean matches(EnvironmentContent envContent) {
        return ReflectionConfigurer.isReflectEnable(envContent);
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
