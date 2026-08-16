/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.jdbc.column;

/**
 * Transforms between database column names and7Java property names
 * (e.g. {@code user_name} ↔ {@code userName}).
 *
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-03-19 11:34.
 */
public interface ColumnNameTransformer {
    /** Converts a database column name to a Java property name. */
    String columnNameToPropertyName(String columnName);

    /** Converts a Java property name to a database column name. */
    String propertyNameToColumnName(String propertyName);

    /**
     * Joins column-name parts into a camelCase property name, appending
     * to the given result builder.
     *
     * @param parts  the column name split into parts (e.g. by underscore)
     * @param result the builder to append the camelCase result to
     */
    default void getColumnName(String[] parts, StringBuilder result) {
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) {
                parts[i] = parts[i].substring(0, 1).toUpperCase() + parts[i].substring(1);
            }
            result.append(parts[i]);
        }
    }
}
