package com.truthbean.debbie.jdbc.column;

/**
 * @author TruthBean
 * @since 0.5.3
 * Created on 2021/12/13 20:33.
 */
@FunctionalInterface
public interface ColumnValueSetter<T> {

    void set(String name, T value);
}
