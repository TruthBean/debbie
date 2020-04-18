package com.truthbean.debbie.lang;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.0.2
 * Created on 2020-03-22 21:58
 */
@FunctionalInterface
public interface Callback<R> {

    R call(Object...args);
}
