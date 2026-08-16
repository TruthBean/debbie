package com.truthbean.debbie.jdbc.column;

/**
 * Functional interface for writing a column value by name.
 *
 * @param <T> the value type
 * @author TruthBean
 * @since 0.5.3
 * Created on 2021/12/13 20:33.
 */
@FunctionalInterface
public interface ColumnValueSetter<T> {

    /**
     * Sets the value of the named column.
     *
     * @param name  the column name
     * @param value the value to set
     */
    void set(String name, T value);
}
