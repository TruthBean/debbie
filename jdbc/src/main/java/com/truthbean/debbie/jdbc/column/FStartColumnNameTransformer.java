/**
 * Copyright (c) 2024 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.jdbc.column;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class FStartColumnNameTransformer implements ColumnNameTransformer {

    @Override
    public String columnNameToPropertyName(String columnName) {
        if (columnName.startsWith("f")) {
            columnName = columnName.substring(1);
        } else if (columnName.startsWith("f_")) {
            columnName = columnName.substring(2);
        }
        var parts = columnName.split("_");
        var sb = new StringBuilder();
        getColumnName(parts, sb);
        return sb.toString();
    }

    @Override
    public String propertyNameToColumnName(String propertyName) {
        var parts = propertyName.split("[A-Z]");
        var result = new StringBuilder("f_");
        getColumnName(parts, result);
        return result.toString();
    }
}
