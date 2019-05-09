package com.truthbean.debbie.jdbc.repository;

import com.truthbean.debbie.jdbc.datasource.connection.ConnectionCallable;
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

    private static <R> Connection getConnection(ConnectionCallable<R> action) {
        var connection = action.getConnection();
        if (connection == null) {
            throw new RuntimeException("ConnectionBinder did not bind connection yet !");
        }
        return connection;
    }

    public static <R> R actionTransactional(ConnectionCallable<R> action) {
        var connection = getConnection(action);
        R result = null;
        try {
            connection.setAutoCommit(false);
            result = action.call(connection);
            commit(connection);
        } catch (Exception e) {
            LOGGER.error("action error ", e);
            rollback(connection);
        } finally {
            close(connection);
        }
        return result;
    }

    public static <R> Future<R> asyncActionTransactional(ConnectionCallable<R> action) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Callable<R> callable = () -> {
            var connection = getConnection(action);
            R result = null;
            try {
                connection.setAutoCommit(false);
                result = action.call(connection);
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

    public static <R> R action(ConnectionCallable<R> action) {
        R result = null;
        var connection = getConnection(action);
        try {
            connection.setAutoCommit(true);
            result = action.call(connection);
        } catch (Exception e) {
            LOGGER.error("action error ", e);
        } finally {
            close(connection);
        }
        return result;
    }

    public static <R> Optional<R> actionOptional(ConnectionCallable<R> action) {
        Optional<R> result = Optional.empty();
        var connection = getConnection(action);
        try {
            connection.setAutoCommit(true);
            var a = action.call(connection);
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

    public static <R> CompletableFuture<R> asyncAction(ConnectionCallable<R> action) {
        return CompletableFuture.supplyAsync(() -> {
            var connection = getConnection(action);
            R result = null;
            try {
                connection.setAutoCommit(true);
                result = action.call(connection);
            } catch (Exception e) {
                LOGGER.error("action error ", e);
            } finally {
                close(connection);
            }
            return result;
        });
    }

    public static <R> R actionTransactional(Connection connection, Callable<R> action) {

        R result = null;
        try {
            connection.setAutoCommit(false);
            result = action.call();
            commit(connection);
        } catch (Exception e) {
            LOGGER.error("action error ", e);
            rollback(connection);
        } finally {
            close(connection);
        }
        return result;
    }

    public static <R> Future<R> asyncActionTransactional(Connection connection, Callable<R> action) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Callable<R> callable = () -> {
            R result = null;
            try {
                connection.setAutoCommit(false);
                result = action.call();
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

    public static <R> R action(Connection connection, Callable<R> action) {
        R result = null;
        try {
            connection.setAutoCommit(true);
            result = action.call();
        } catch (Exception e) {
            LOGGER.error("action error ", e);
        } finally {
            close(connection);
        }
        return result;
    }

    public static <R> Optional<R> actionOptional(Connection connection, Callable<R> action) {
        Optional<R> result = Optional.empty();
        try {
            connection.setAutoCommit(true);
            var a = action.call();
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

    public static <R> CompletableFuture<R> asyncAction(Connection connection, Callable<R> action) {
        return CompletableFuture.supplyAsync(() -> {
            R result = null;
            try {
                connection.setAutoCommit(true);
                result = action.call();
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