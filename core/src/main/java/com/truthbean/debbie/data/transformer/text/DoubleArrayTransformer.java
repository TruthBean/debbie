/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.data.transformer.text;

import com.truthbean.debbie.data.transformer.DataTransformer;

import java.util.Arrays;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-08-13 14:33
 */
public class DoubleArrayTransformer implements DataTransformer<double[], String>  {
    @Override
    public String transform(double[] floats) {
        return Arrays.toString(floats);
    }

    @Override
    public double[] reverse(String s) {
        return string2doubles(s);
    }

    public static double[] string2doubles(String json) {
        if (json != null) {
            if (json.startsWith("[") && json.endsWith("]")) {
                json = json.substring(1, json.length() - 1);
            }
            String[] split = json.split(",");
            double[] floats = new double[split.length];
            for (int i = 0; i < split.length; i++) {
                floats[i] = Double.parseDouble(split[i]);
            }
            return floats;
        }
        return null;
    }
}
