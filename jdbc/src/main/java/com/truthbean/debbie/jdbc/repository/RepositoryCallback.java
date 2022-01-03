/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.jdbc.repository;

import com.truthbean.debbie.jdbc.transaction.TransactionCallable;
import com.truthbean.debbie.jdbc.transaction.TransactionException;
import com.truthbean.debbie.jdbc.transaction.TransactionInfo;
import com.truthbean.debbie.jdbc.transaction.TransactionManager;
import com.truthbean.Logger;
import com.truthbean.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.*;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class RepositoryCallback {

    private static <R> TransactionInfo getTransaction(TransactionCallable<R> action) {
        var transactionInfo = action.getTransaction();
        if (transactionInfo == null) {
            throw new TransactionException("ConnectionBinder did not bind connection yet !");
        }
        return transactionInfo;
    }

    public static <R> R actionTransactional(TransactionCallable<R> action) {
        var transaction = getTransaction(action);
        R result = null;
        try {
            transaction.setAutoCommit(false);
            result = action.call(transaction);
            transaction.commit();
        } catch (Exception e) {
            LOGGER.error("action error ", e);
            transaction.rollback();
        } finally {
            transaction.close();
            TransactionManager.remove(transaction);
        }
        return result;
    }

    public static <R> Future<R> asyncActionTransactional(TransactionCallable<R> action) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Callable<R> callable = () -> actionTransactional(action);
        return action(executor, callable);
    }

    public static <R> R action(TransactionCallable<R> action) {
        R result = null;

        TransactionInfo transaction = null;
        try {
            transaction = getTransaction(action);
            transaction.setAutoCommit(true);
            result = action.call(transaction);
        } catch (Exception e) {
            LOGGER.error("action error ", e);
        } finally {
            if (transaction != null) {
                transaction.close();
                TransactionManager.remove(transaction);
            }
        }
        return result;
    }

    public static <R> Optional<R> actionOptional(TransactionCallable<R> action) {
        Optional<R> result = Optional.empty();
        TransactionInfo transaction = null;
        try {
            transaction = getTransaction(action);
            transaction.setAutoCommit(true);
            var a = action.call(transaction);
            if (a != null) {
                result = Optional.of(a);
            }
        } catch (Exception e) {
            LOGGER.error("action error ", e);
        } finally {
            if (transaction != null) {
                transaction.close();
                TransactionManager.remove(transaction);
            }
        }
        return result;
    }

    public static <R> CompletableFuture<R> asyncAction(TransactionCallable<R> action) {
        return CompletableFuture.supplyAsync(() -> action(action));
    }

    public static <R> R actionTransactional(TransactionInfo transaction, Callable<R> action) {
        R result = null;
        try {
            transaction.setAutoCommit(false);
            result = action.call();
            transaction.commit();
        } catch (Exception e) {
            LOGGER.error("action error ", e);
            transaction.rollback();
        } finally {
            transaction.close();
        }
        return result;
    }

    public static <R> Future<R> asyncActionTransactional(TransactionInfo transaction, Callable<R> action) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Callable<R> callable = () -> actionTransactional(transaction, action);
        return action(executor, callable);
    }

    private static <R> Future<R> action(ExecutorService executor, Callable<R> callable) {
        Future<R> result = executor.submit(callable);
        try {
            executor.shutdown();
            boolean awaitTermination = executor.awaitTermination(5, TimeUnit.SECONDS);
            if (!awaitTermination) {
                executor.shutdownNow();
            }
        } catch (InterruptedException ignored) {
        } finally {
            if (!executor.isTerminated()) {
                LOGGER.debug("task is stil running...");
            }
            executor.shutdownNow();
        }
        return result;
    }

    public static <R> R action(TransactionInfo transaction, Callable<R> action) {
        R result = null;
        try (transaction) {
            transaction.setAutoCommit(true);
            result = action.call();
        } catch (Exception e) {
            LOGGER.error("action error ", e);
        }
        return result;
    }

    public static <R> Optional<R> actionOptional(TransactionInfo transaction, Callable<R> action) {
        Optional<R> result = Optional.empty();
        try (transaction) {
            transaction.setAutoCommit(true);
            var a = action.call();
            if (a != null) {
                result = Optional.of(a);
            }
        } catch (Exception e) {
            LOGGER.error("action error ", e);
        }
        return result;
    }

    public static <R> CompletableFuture<R> asyncAction(TransactionInfo transaction, Callable<R> action) {
        return CompletableFuture.supplyAsync(() -> {
            R result = null;
            try (transaction) {
                transaction.setAutoCommit(true);
                result = action.call();
            } catch (Exception e) {
                LOGGER.error("action error ", e);
            }
            return result;
        });
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryCallback.class);
}