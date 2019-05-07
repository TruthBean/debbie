package com.truthbean.debbie.jdbc.repository;

import com.truthbean.debbie.core.proxy.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.*;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class RepositoryAction {
    public static <R> R actionTransactional(Connection connection, Action<R> action) {

        R result = null;
        try {
            connection.setAutoCommit(false);
            result = action.action();
            commit(connection);
        } catch (Exception e) {
            LOGGER.error("action error ", e);
            rollback(connection);
        } finally {
            close(connection);
        }
        return result;
    }

    public static <R> Future<R> asyncActionTransactional(Connection connection, Action<R> action) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Callable<R> callable = () -> {
            R result = null;
            try {
                connection.setAutoCommit(false);
                result = action.action();
                commit(connection);
            } catch (Exception e) {
                LOGGER.error("action error ", e);
                rollback(connection);
            } finally {
                close(connection);
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

    public static <R> R action(Connection connection, Action<R> action) {
        R result = null;
        try {
            connection.setAutoCommit(true);
            result = action.action();
        } catch (Exception e) {
            LOGGER.error("action error ", e);
        } finally {
            close(connection);
        }
        return result;
    }

    public static <R> Optional<R> actionOptional(Connection connection, Action<R> action) {
        Optional<R> result = Optional.empty();
        try {
            connection.setAutoCommit(true);
            var a = action.action();
            if (a != null) {
                result = Optional.of(a);
            }
        } catch (Exception e) {
            LOGGER.error("action error ", e);
        } finally {
            close(connection);
        }
        return result;
    }

    public static <R> CompletableFuture<R> asyncAction(Connection connection, Action<R> action) {
        return CompletableFuture.supplyAsync(() -> {
            R result = null;
            try {
                connection.setAutoCommit(true);
                result = action.action();
            } catch (Exception e) {
                LOGGER.error("action error ", e);
            } finally {
                close(connection);
            }
            return result;
        });
    }

    private static void commit(Connection connection) {
        try {
            if (!connection.isReadOnly()) {
                connection.commit();
            }
        } catch (SQLException e) {
            LOGGER.error("connection commit error. ", e);
        }
    }

    private static void rollback(Connection connection) {
        try {
            if (!connection.isReadOnly()) {
                connection.rollback();
            }
        } catch (SQLException e) {
            LOGGER.error("connection rollback error. ", e);
        }
    }

    private static void close(Connection connection) {
        try {
            if (!connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryAction.class);
}