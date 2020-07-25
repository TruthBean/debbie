/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.metrics;

import java.util.function.BooleanSupplier;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 */
public class HealthChecker {
    private final String name;
    private HealthStatus status;

    private HealthChecker(String name) {
        this.name = name;
    }

    public static HealthChecker check(String name) {
        return new HealthChecker(name);
    }

    public HealthChecker check(BooleanSupplier supplier) {
        boolean bool = supplier.getAsBoolean();
        if (bool) {
            this.status = HealthStatus.UP;
        } else {
            this.status = HealthStatus.DOWN;
        }
        return this;
    }

    public HealthChecker up() {
        this.status = HealthStatus.UP;
        return this;
    }

    public HealthChecker down() {
        this.status = HealthStatus.DOWN;
        return this;
    }

    @Override
    public String toString() {
        return "{\"name\":\"" + name + "\",\"status\":\"" + status + "\"}";
    }
}
