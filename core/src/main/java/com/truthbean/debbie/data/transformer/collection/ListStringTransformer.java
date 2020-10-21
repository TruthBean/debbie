/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.data.transformer.collection;

import com.truthbean.debbie.data.transformer.DataTransformer;
import com.truthbean.debbie.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author truthbean
 * @since 0.0.2
 */
public class ListStringTransformer implements DataTransformer<List<String>, String> {
    @Override
    public String transform(List<String> strings) {
        return StringUtils.joining(strings, ",");
    }

    @Override
    public List<String> reverse(String s) {
        if (s != null) {
            String[] split = s.split(",");
            return new ArrayList<>(Arrays.asList(split));
        }
        return new ArrayList<>();
    }
}
