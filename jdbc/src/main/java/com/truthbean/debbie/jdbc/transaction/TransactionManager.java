package com.truthbean.debbie.jdbc.transaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Deque;
import java.util.concurrent.LinkedBlockingDeque;

public class TransactionManager {

    private static Deque<TransactionInfo> transactionInfos = new LinkedBlockingDeque<>(1024);

    public static void offer(TransactionInfo transactionInfo) {
        LOGGER.debug("offer transactionInfo " + transactionInfo.getId());
        transactionInfos.offerFirst(transactionInfo);
    }

    public static TransactionInfo peek() {
        TransactionInfo transactionInfo = transactionInfos.peekFirst();
        LOGGER.debug("peek transactionInfo " + transactionInfo.getId());
        return transactionInfo;
    }

    public static void remove() {
        TransactionInfo transactionInfo = transactionInfos.removeFirst();
        LOGGER.debug("remove transactionInfo " + transactionInfo.getId());
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionManager.class);
}
