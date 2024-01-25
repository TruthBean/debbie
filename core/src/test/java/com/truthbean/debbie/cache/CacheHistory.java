/**
 * Copyright (c) 2024 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.cache;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 */
public class CacheHistory implements Comparable<CacheHistory> {
    private final String id;

    private final LocalDateTime createTime;

    private final CacheInfo history;

    public CacheHistory(CacheInfo cacheInfo) {
        this.id = UUID.randomUUID().toString();
        this.createTime = LocalDateTime.now();
        this.history = cacheInfo;
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public CacheInfo getHistory() {
        return history;
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\":\"" + id + '\"' +
                ",\"createTime\":" + createTime +
                ",\"history\":" + history +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CacheHistory that = (CacheHistory) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(createTime, that.createTime) &&
                Objects.equals(history, that.history);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, createTime, history);
    }

    @Override
    public int compareTo(CacheHistory o) {
        return createTime.compareTo(o.createTime);
    }
}
