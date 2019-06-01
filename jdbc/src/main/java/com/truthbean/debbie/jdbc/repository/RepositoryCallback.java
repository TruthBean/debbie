package com.truthbean.debbie.jdbc.repository;

import com.truthbean.debbie.jdbc.datasource.connection.TransactionCallable;
import com.truthbean.debbie.jdbc.transaction.TransactionInfo;
import com.truthbean.debbie.jdbc.transaction.TransactionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            throw new RuntimeException("ConnectionBinder did not bind connection yet !");
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
            TransactionManager.remove();
        }
        return result;
    }

    public static <R> Future<R> asyncActionTransactional(TransactionCallable<R> action) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Callable<R> callable = () -> {
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
                TransactionManager.remove();
            }
            return result;
        };
        Future<R> result = executor.submit(callable);
        try {
            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException ignored) {
        } finally {
            if (!executor.isTerminated()) {
                LOGGER.debug("task is stil running...");
            }
            executor.shutdownNow();
        }
        return result;
    }

    public static <R> R action(TransactionCallable<R> action) {
        R result = null;
        try (var transaction = getTransaction(action)) {
            transaction.setAutoCommit(true);
            result = action.call(transaction);
        } catch (Exception e) {
            LOGGER.error("action error ", e);
        }
        return result;
    }

    public static <R> Optional<R> actionOptional(TransactionCallable<R> action) {
        Optional<R> result = Optional.empty();
        try (var transaction = getTransaction(action)) {
            transaction.setAutoCommit(true);
            var a = action.call(transaction);
            if (a != null) {
                result = Optional.of(a);
            }
        } catch (Exception e) {
            LOGGER.error("action error ", e);
        }
        return result;
    }

    public static <R> CompletableFuture<R> asyncAction(TransactionCallable<R> action) {
        return CompletableFuture.supplyAsync(() -> {
            R result = null;
            try (var transaction = getTransaction(action)) {
                transaction.setAutoCommit(true);
                result = action.call(transaction);
            } catch (Exception e) {
                LOGGER.error("action error ", e);
            }
            return result;
        });
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
            TransactionManager.remove();
        }
        return result;
    }

    public static <R> Future<R> asyncActionTransactional(TransactionInfo transaction, Callable<R> action) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Callable<R> callable = () -> {
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
                TransactionManager.remove();
            }
            return result;
        };
        Future<R> result = executor.submit(callable);
        try {
            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);
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