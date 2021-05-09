/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.cache;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 */
public class CacheRepository {

    public final Map<String, CacheHistory> data = new LinkedHashMap<>();

    public void commit(CacheInfo cacheInfo) {
        CacheHistory cacheHistory = new CacheHistory(cacheInfo);
        data.put(cacheHistory.getId(), cacheHistory);
    }

    public void revert(String historyId) {
        data.remove(historyId);
    }

    public CacheHistory show(String historyId) {
        return data.get(historyId);
    }

    public Collection<CacheHistory> logs() {
        return Collections.unmodifiableCollection(data.values());
    }
}
