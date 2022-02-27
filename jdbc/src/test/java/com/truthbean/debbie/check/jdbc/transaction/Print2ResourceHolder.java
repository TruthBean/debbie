/**
 * Copyright (c) 2022 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.check.jdbc.transaction;

import com.truthbean.debbie.jdbc.transaction.ResourceHolder;

public class Print2ResourceHolder implements ResourceHolder {
    @Override
    public int getOrder() {
        return 2;
    }

    @Override
    public void prepare() {
        System.out.println("0. prepare2.....");
    }

    @Override
    public void beforeCommit() {
        System.out.println("1. beforeCommit2.....");
    }

    @Override
    public void afterCommit() {
        System.out.println("2. afterCommit2.....");
    }

    @Override
    public void beforeRollback() {
        System.out.println("3. beforeRollback2.....");
    }

    @Override
    public void afterRollback() {
        System.out.println("4. afterRollback2.....");
    }

    @Override
    public void beforeClose() {
        System.out.println("5. beforeClose2.....");
    }

    @Override
    public void afterClose() {
        System.out.println("6. afterClose2.....");
    }
}
