package com.truthbean.debbie.jdbc.transaction;

import com.truthbean.debbie.data.transformer.DataTransformer;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class TransactionIsolationLevelTransformer implements DataTransformer<TransactionIsolationLevel, String> {
    @Override
    public String transform(TransactionIsolationLevel transactionIsolationLevel) {
        return transactionIsolationLevel.name();
    }

    @Override
    public TransactionIsolationLevel reverse(String s) {
        return TransactionIsolationLevel.valueOf(s);
    }
}
