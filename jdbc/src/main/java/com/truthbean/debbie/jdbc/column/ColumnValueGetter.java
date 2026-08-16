package com.truthbean.debbie.jdbc.column;

/**
 * Functional interface for reading a column value by name.
 *
 * @param <T> the value type
 * @author TruthBean
 * @since 0.5.3
 * Created on 2021/12/13 20:33.
 */
@FunctionalInterface
public interface ColumnValueGetter<T> {

    /**
     * Returns the value of the named column.
     *
     * @param name the column name
     * @return the column value
     */
    T get(String name);
}
