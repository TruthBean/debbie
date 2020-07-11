/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.jdbc.transaction;

import com.truthbean.debbie.jdbc.datasource.DriverConnection;

import java.sql.Connection;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public interface TransactionService {

    default TransactionInfo getTransaction() {
        TransactionInfo transactionInfo = TransactionManager.peek();
        if (transactionInfo == null) {
            throw new TransactionException("No debbie transaction");
        }
        return transactionInfo;
    }

    default Connection getConnection() {
        TransactionInfo transactionInfo = TransactionManager.peek();
        if (transactionInfo == null) {
            throw new TransactionException("No debbie transaction");
        }
        return transactionInfo.getConnection();
    }

    default DriverConnection getDriverConnection() {
        TransactionInfo transactionInfo = TransactionManager.peek();
        if (transactionInfo == null) {
            throw new TransactionException("No debbie transaction");
        }
        return transactionInfo.getDriverConnection();
    }
}
