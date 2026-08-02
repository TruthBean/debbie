/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.check.jdbc.transaction;

import com.truthbean.Console;
import com.truthbean.debbie.jdbc.transaction.ResourceHolder;

public class Print2ResourceHolder implements ResourceHolder {
    @Override
    public int getOrder() {
        return 2;
    }

    @Override
    public void prepare() {
        Console.println("0. prepare2.....");
    }

    @Override
    public void beforeCommit() {
        Console.println("1. beforeCommit2.....");
    }

    @Override
    public void afterCommit() {
        Console.println("2. afterCommit2.....");
    }

    @Override
    public void beforeRollback() {
        Console.println("3. beforeRollback2.....");
    }

    @Override
    public void afterRollback() {
        Console.println("4. afterRollback2.....");
    }

    @Override
    public void beforeClose() {
        Console.println("5. beforeClose2.....");
    }

    @Override
    public void afterClose() {
        Console.println("6. afterClose2.....");
    }
}
