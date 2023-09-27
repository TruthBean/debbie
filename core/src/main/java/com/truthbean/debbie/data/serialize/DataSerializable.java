/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.data.serialize;

import java.io.InputStream;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.1
 * Created on 2020-10-30 14:19
 */
public interface DataSerializable<R> {

    R serialize(Object obj);

    <T> T deserialize(R text, Class<T> type);

    <T> T deserialize(InputStream inputStream, Class<T> type);
}
