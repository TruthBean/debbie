package com.truthbean.debbie.jdbc.column;

/**
 * @author truthbean
 * @since 0.0.1
 */
public class DefaultColumnNameTransformer implements ColumnNameTransformer {
    @Override
    public String columnNameToPropertyName(String columnName) {
        var parts = columnName.split("_");
        var result = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) {
                parts[i] = parts[i].substring(0, 1).toUpperCase() + parts[i].substring(1);
            }
            result.append(parts[i]);
        }
        return result.toString();
    }

    @Override
    public String propertyNameToColumnName(String propertyName) {
        var parts = propertyName.split("[A-Z]");
        var result = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) {
                parts[i] = parts[i].substring(0, 1).toUpperCase() + parts[i].substring(1);
            }
            result.append(parts[i]);
        }
        return result.toString();
    }
}
