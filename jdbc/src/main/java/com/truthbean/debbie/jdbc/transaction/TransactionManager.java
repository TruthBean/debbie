package com.truthbean.debbie.jdbc.transaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Deque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class TransactionManager {

    private static final Deque<TransactionInfo> TRANSACTION_DEQUE = new LinkedBlockingDeque<>(1024);

    public static void offer(TransactionInfo transactionInfo) {
        LOGGER.debug("offer transactionInfo " + transactionInfo.getId());
        TRANSACTION_DEQUE.offerFirst(transactionInfo);
    }

    public synchronized static TransactionInfo peek() {
        TransactionInfo transactionInfo = TRANSACTION_DEQUE.peekFirst();
        LOGGER.debug("peek transactionInfo " + transactionInfo.getId());
        return transactionInfo;
    }

    public synchronized static void remove() {
        TransactionInfo transactionInfo = TRANSACTION_DEQUE.removeFirst();
        LOGGER.debug("remove transactionInfo " + transactionInfo.getId());
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionManager.class);
}
