/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.jdbc.column;

/**
 * @author truthbean
 * @since 0.0.1
 */
public class DefaultColumnNameTransformer implements ColumnNameTransformer {
    @Override
    public String columnNameToPropertyName(String columnName) {
        var parts = columnName.split("_");
        return getName(parts);
    }

    private String getName(String[] parts) {
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
        return getName(parts);
    }
}
