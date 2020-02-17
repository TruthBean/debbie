package com.truthbean.debbie.jdbc.transaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class TransactionManager {

    private static final Deque<TransactionInfo> TRANSACTION_DEQUE = new LinkedBlockingDeque<>(1024);

    private static final ThreadLocal<LinkedHashMap<Object, Object>> resources = new ThreadLocal<>();
    private static final ThreadLocal<List<ResourceHolder>> resourceHolders = new ThreadLocal<>();

    public static void bindResource(Object key, Object value) {
        LinkedHashMap<Object, Object> resourcesMap = resources.get();
        if (resourcesMap == null) {
            resourcesMap = new LinkedHashMap<>();
            resources.set(resourcesMap);
        }
        resourcesMap.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public static void registerResourceHolder(ResourceHolder resourceHolder) {
        List<ResourceHolder> resourceHolderList = resourceHolders.get();
        if (resourceHolderList == null) {
            resourceHolderList = new ArrayList<>();
            resourceHolders.set(resourceHolderList);
        }
        resourceHolderList.add(resourceHolder);
        Collections.sort(resourceHolderList);
    }

    public static void offer(TransactionInfo transactionInfo) {
        LOGGER.debug("offer transactionInfo " + transactionInfo.getId());
        transactionInfo.bindResources(resources.get());
        transactionInfo.registerResourceHolders(resourceHolders.get());

        transactionInfo.prepare();

        // remove temp
        resources.remove();
        resourceHolders.remove();

        TRANSACTION_DEQUE.offerFirst(transactionInfo);
    }

    public synchronized static TransactionInfo peek() {
        TransactionInfo transactionInfo = TRANSACTION_DEQUE.peekFirst();
        if (transactionInfo != null)
            LOGGER.debug("peek transactionInfo " + transactionInfo.getId());
        else
            LOGGER.warn("peek transactionInfo null.");
        return transactionInfo;
    }

    public synchronized static void remove() {
        TransactionInfo transactionInfo = TRANSACTION_DEQUE.removeFirst();
        if (transactionInfo != null)
            LOGGER.debug("remove transactionInfo " + transactionInfo.getId());
        else
            LOGGER.debug("remove transactionInfo null.");
    }

    public synchronized static void clear() {
        LinkedHashMap<Object, Object> resourcesMap = resources.get();
        if (resourcesMap != null) {
            resourcesMap.clear();
        }
        resources.remove();

        List<ResourceHolder> resourceHoldersMap = resourceHolders.get();
        if (resourceHoldersMap != null) {
            resourceHoldersMap.clear();
        }
        resourceHolders.remove();

        TRANSACTION_DEQUE.clear();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionManager.class);
}
