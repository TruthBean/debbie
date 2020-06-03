/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.data.transformer;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-02-18 13:51
 */
public interface DataTransformer<Original, Transformer> {

    /**
     * transform data
     * @param original original data
     * @return data after transformed
     */
    Transformer transform(Original original);

    /**
     * transformer to original
     * @param transformer transformer value
     * @return original value
     */
    Original reverse(Transformer transformer);
}