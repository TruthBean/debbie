package com.truthbean.debbie.jdbc.transaction;

import com.truthbean.debbie.jdbc.transaction.TransactionInfo;
import com.truthbean.debbie.jdbc.transaction.TransactionService;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public interface TransactionCallable<V> extends TransactionService {

    V call(TransactionInfo connection);
}
