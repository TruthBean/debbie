package com.truthbean.code.debbie.jdbc.column;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class FStartColumnNameTransformer implements ColumnNameTransformer {
    @Override
    public String changeColumnNameToPropertyName(String columnName) {
        if (columnName.startsWith("f")) {
            columnName = columnName.substring(1);
        } else if (columnName.startsWith("f_")) {
            columnName = columnName.substring(2);
        }
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
}
