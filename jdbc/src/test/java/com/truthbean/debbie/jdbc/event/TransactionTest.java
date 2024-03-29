/**
 * Copyright (c) 2024 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.jdbc.event;

import com.truthbean.debbie.boot.DebbieApplication;
import com.truthbean.debbie.test.annotation.DebbieApplicationTest;
import org.junit.jupiter.api.Test;

/**
 * @author truthbean/Rogar·Q
 * @since 0.0.2
 * Created on 2020-01-19 18:45.
 */
@DebbieApplicationTest
public class TransactionTest {
    public static void main(String[] args) {
        var application = DebbieApplication.create(TransactionTest.class, args);
        application.start();
        application.exit();
    }

    @Test
    void test() {

    }
}
