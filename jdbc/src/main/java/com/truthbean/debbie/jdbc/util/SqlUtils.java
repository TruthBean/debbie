/**
 * Copyright (c) 2024 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.jdbc.util;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-10-14 15:37
 */
public class SqlUtils {
    protected SqlUtils() {
    }

    /**
     * 过滤掉SQL like 字段中可能会影响数据
     * @param search search text
     * @return filtered text
     */
    public static String replaceSqlLikeKey(String search){
        if (search != null) {
            if (search.contains("?")) {
                search = search.replaceAll("\\?", "\\\\?");
            }
            if (search.contains("_")) {
                search = search.replaceAll("_", "\\\\_");
            }
            if (search.contains("%")) {
                search = search.replaceAll("%", "\\\\%");
            }
            if (search.contains("@")) {
                search = search.replaceAll("@", "\\\\@");
            }
            if (search.contains("\\")) {
                search = search.replaceAll("\\\\", "\\\\\\\\");
            }
        }
        return search;
    }
}
