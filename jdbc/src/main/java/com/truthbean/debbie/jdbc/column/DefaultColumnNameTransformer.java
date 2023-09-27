/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
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
        StringBuilder name = new StringBuilder();
        getColumnName(parts, name);
        return name.toString();
    }

    @Override
    public String propertyNameToColumnName(String propertyName) {
        var parts = propertyName.split("[A-Z]");
        return getName(parts);
    }
}
