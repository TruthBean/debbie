package com.truthbean.debbie.core.proxy;

/**
 * @author TruthBean
 * @since 0.0.1
 */
@FunctionalInterface
public interface Action<T> {

    T action();
}
