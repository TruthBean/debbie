package com.truthbean.debbie.environment;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.5
 */
public interface ProfiledEnvironment extends Environment {
    /**
     * 获取优先级，数值越大越优先获取，加载顺序同之
     */
    int priority();

    String profile();
}
