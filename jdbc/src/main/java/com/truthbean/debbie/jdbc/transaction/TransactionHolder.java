/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.jdbc.transaction;

import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.debbie.jdbc.datasource.DataSourceDriverName;

import java.io.Closeable;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.7
 */
public class TransactionHolder implements Closeable {
    private final String method = "(no method)";

    private DataSourceDriverName driverName;

    private boolean forceCommit;
    private Class<? extends Throwable> rollbackFor;

    private final Map<Object, Object> resources = new LinkedHashMap<>();
    private final List<ResourceHolder> resourceHolders = new LinkedList<>();

    private final ThreadLocal<TransactionInstance> transactionInstance = new ThreadLocal<>();

    public TransactionHolder() {
    }

    public Method getMethod() {
        if (hasMethod())
            return (Method) this.resources.get(this.method);
        return null;
    }

    private boolean hasMethod() {
        return this.resources.containsKey(this.method);
    }

    public DataSourceDriverName getDriverName() {
        return driverName;
    }

    public void setDriverName(DataSourceDriverName driverName) {
        this.driverName = driverName;
    }

    public boolean isForceCommit() {
        return forceCommit;
    }

    public void setForceCommit(boolean forceCommit) {
        this.forceCommit = forceCommit;
    }

    public Class<? extends Throwable> getRollbackFor() {
        return rollbackFor;
    }

    public void setRollbackFor(Class<? extends Throwable> rollbackFor) {
        this.rollbackFor = rollbackFor;
    }

    public void bindResource(Object key, Object value) {
        resources.put(key, value);
    }

    public void bindResources(Map<Object, Object> resources) {
        if (resources != null && !resources.isEmpty())
            this.resources.putAll(resources);
    }

    public void bindMethod(Method method) {
        this.resources.put(this.method, method);
    }

    public void clearResource() {
        resources.clear();
    }

    public Object getResource(Object key) {
        return resources.get(key);
    }

    public void registerResourceHolder(ResourceHolder resourceHolder) {
        if (resourceHolder != null)
            resourceHolders.add(resourceHolder);
    }

    public void registerResourceHolders(List<ResourceHolder> resourceHolders) {
        if (resourceHolders != null && !resourceHolders.isEmpty()) {
            this.resourceHolders.addAll(resourceHolders);
        }
    }

    public void releaseResourceHolder(ResourceHolder resourceHolder) {
        resourceHolders.remove(resourceHolder);
    }

    public void clearResourceHolders() {
        resourceHolders.clear();
    }

    public void setTransactionInstance(TransactionInstance transactionInstance) {
        this.transactionInstance.set(transactionInstance);
    }

    public void prepare() {
        for (ResourceHolder resourceHolder : resourceHolders) {
            resourceHolder.prepare();
        }
    }

    private void beforeCommit() {
        for (ResourceHolder resourceHolder : resourceHolders) {
            resourceHolder.beforeCommit();
        }
    }

    public void commit() {
        // before
        beforeCommit();

        // commit
        if (transactionInstance.get() == null) {
            LOGGER.error("method (" + method + ") invoked without transaction. ");
            return;
        }
        if (transactionInstance.get().getConnection() == null) {
            LOGGER.error("method (" + method + ") not bind connection. ");
            return;
        }

        transactionInstance.get().commit();

        // after
        afterCommit();
    }

    private void afterCommit() {
        for (ResourceHolder resourceHolder : resourceHolders) {
            resourceHolder.afterCommit();
        }
    }

    private void beforeRollback() {
        for (ResourceHolder resourceHolder : resourceHolders) {
            resourceHolder.beforeRollback();
        }
    }

    public void rollback() {
        // before
        beforeRollback();

        if (transactionInstance.get() == null) {
            LOGGER.error("method (" + method + ") invoked without transaction. ");
            return;
        }
        if (transactionInstance.get().getConnection() == null) {
            LOGGER.error("method (" + method + ") not bind connection. ");
            return;
        }

        transactionInstance.get().rollback();

        // after
        afterRollback();
    }

    private void afterRollback() {
        for (ResourceHolder resourceHolder : resourceHolders) {
            resourceHolder.afterRollback();
        }
    }

    private void beforeClose() {
        if (transactionInstance.get() != null) {
            transactionInstance.get().setClosed(true);
        }
        for (ResourceHolder resourceHolder : resourceHolders) {
            resourceHolder.beforeClose();
        }
    }

    @Override
    public void close() {
        synchronized (this) {
            if (transactionInstance.get() == null) {
                LOGGER.error("method (" + method + ") invoked without transaction. ");
                return;
            }

            if (!transactionInstance.get().isUsing()) {
                beforeClose();

                if (transactionInstance.get().getConnection() == null) {
                    LOGGER.error("method (" + method + ") not bind connection. ");
                    return;
                }

                if (hasMethod())
                    LOGGER.trace(() -> "Close connection by transactional method(" + getMethod() + ") and remove it. ");

                transactionInstance.get().close();

                afterClose();

                // clear
                resources.clear();
                resourceHolders.clear();
                transactionInstance.remove();
            } else {
                LOGGER.warn(() -> transactionInstance.get().getId() + ": connection(" + transactionInstance.get().getConnection() + ") " + transactionInstance.get().getConnection().hashCode() + " is using, cannot remove it! ");
            }
        }
    }

    private void afterClose() {
        for (ResourceHolder resourceHolder : resourceHolders) {
            resourceHolder.afterClose();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TransactionHolder that)) return false;
        return driverName == that.driverName &&
                forceCommit == that.forceCommit && Objects.equals(rollbackFor, that.rollbackFor)
                && Objects.equals(resources, that.resources) && Objects.equals(resourceHolders, that.resourceHolders);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, driverName, forceCommit, rollbackFor, resources, resourceHolders);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionInfo.class);
}
