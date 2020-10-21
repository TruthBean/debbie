/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.data.transformer.numeric;

import com.truthbean.debbie.data.transformer.DataTransformer;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-10-10 17:51
 */
public class LongToBooleanTransformer implements DataTransformer<Long, Boolean> {
    @Override
    public Boolean transform(Long aLong) {
        return aLong != null && aLong > 0;
    }

    @Override
    public Long reverse(Boolean aBoolean) {
        return aBoolean != null && aBoolean ? 1L : 0L;
    }
}
