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

import com.truthbean.Logger;
import com.truthbean.logger.LoggerFactory;

import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class TransactionManager {

    private static final ThreadLocal<Deque<TransactionInfo>> TRANSACTION_DEQUE = new ThreadLocal<>();

    private static final ThreadLocal<LinkedHashMap<Object, Object>> resources = new ThreadLocal<>();
    private static final ThreadLocal<List<ResourceHolder>> resourceHolders = new ThreadLocal<>();

    private TransactionManager() {
    }

    public static void bindResource(Object key, Object value) {
        LinkedHashMap<Object, Object> resourcesMap = resources.get();
        if (resourcesMap == null) {
            resourcesMap = new LinkedHashMap<>();
            resources.set(resourcesMap);
        }
        resourcesMap.put(key, value);
    }

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
        LOGGER.debug(() -> "offer transactionInfo " + transactionInfo.getId());
        transactionInfo.bindResources(resources.get());
        transactionInfo.registerResourceHolders(resourceHolders.get());

        transactionInfo.prepare();

        // remove temp
        resources.remove();
        resourceHolders.remove();

        getOrCreateIfAbsent().offerFirst(transactionInfo);
    }

    public static synchronized TransactionInfo peek() {
        var deque = TRANSACTION_DEQUE.get();
        TransactionInfo transactionInfo = null;
        if (deque != null) {
            transactionInfo = deque.peekFirst();
        }
        if (transactionInfo != null)
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("peek transactionInfo " + transactionInfo.getId());
        else
            LOGGER.warn("peek transactionInfo null.");
        return transactionInfo;
    }

    public static synchronized void remove() {
        var deque = TRANSACTION_DEQUE.get();
        if (deque != null) {
            TransactionInfo transactionInfo = deque.removeFirst();
            if (transactionInfo != null)
                LOGGER.debug(() -> "remove transactionInfo " + transactionInfo.getId());
            else
                LOGGER.debug(() -> "remove transactionInfo null.");
        } else {
            LOGGER.debug(() -> "remove transactionInfo null.");
        }
    }

    public static synchronized void remove(TransactionInfo transactionInfo) {
        if (transactionInfo == null) {
            LOGGER.debug(() -> "remove transactionInfo null.");
            return;
        }

        var deque = TRANSACTION_DEQUE.get();
        if (deque != null) {
            boolean bool = deque.remove(transactionInfo);
            if (bool)
                LOGGER.debug(() -> "remove transactionInfo " + transactionInfo.getId());
            else
                LOGGER.debug(() -> "remove transactionInfo null.");
        } else {
            LOGGER.debug(() -> "remove transactionInfo null.");
        }
    }

    public static synchronized void clear() {
        LOGGER.info("clean transactions.");
        var deque = TRANSACTION_DEQUE.get();
        if (deque != null) {
            for (TransactionInfo transactionInfo : deque) {
                LOGGER.debug(() -> "remove transactionInfo " + transactionInfo.getId());
                transactionInfo.close();
            }
            deque.clear();
        }

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

        TRANSACTION_DEQUE.remove();
    }

    private static Deque<TransactionInfo> getOrCreateIfAbsent() {
        Deque<TransactionInfo> deque = TRANSACTION_DEQUE.get();
        if (deque == null) {
            TRANSACTION_DEQUE.set(new LinkedBlockingDeque<>(1024));
        }
        return TRANSACTION_DEQUE.get();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionManager.class);
}
