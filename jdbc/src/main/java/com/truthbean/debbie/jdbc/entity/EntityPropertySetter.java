package com.truthbean.debbie.jdbc.entity;

/**
 * @author TruthBean
 * @since 0.5.3
 * Created on 2021/12/13 21:30.
 */
@FunctionalInterface
public interface EntityPropertySetter<E, P> {

    void set(E entity, P property);
}
