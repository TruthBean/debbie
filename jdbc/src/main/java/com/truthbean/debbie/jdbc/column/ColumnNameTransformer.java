package com.truthbean.debbie.jdbc.column;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-03-19 11:34.
 */
public interface ColumnNameTransformer {
    String columnNameToPropertyName(String columnName);

    String propertyNameToColumnName(String propertyName);
}
