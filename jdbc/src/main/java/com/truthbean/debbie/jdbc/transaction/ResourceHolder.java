package com.truthbean.debbie.jdbc.transaction;

/**
 * @author truthbean
 * @since 0.0.2
 */
public interface ResourceHolder extends Comparable<ResourceHolder> {

    int getOrder();

    void prepare();

    void beforeCommit();

    void afterCommit();

    void beforeRollback();

    void afterRollback();

    void beforeClose();

    void afterClose();

    @Override
    default int compareTo(ResourceHolder o) {
        return Integer.compare(getOrder(), o.getOrder());
    }
}
