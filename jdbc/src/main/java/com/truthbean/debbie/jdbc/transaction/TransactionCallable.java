package com.truthbean.debbie.jdbc.transaction;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public interface TransactionCallable<V> extends TransactionService {

    V call(TransactionInfo connection);
}
