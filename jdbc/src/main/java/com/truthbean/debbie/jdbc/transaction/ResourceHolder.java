package com.truthbean.debbie.jdbc.transaction;

/**
 * @author truthbean
 * @since 0.0.2
 */
public interface ResourceHolder extends Comparable {

    int getOrder();

    void prepare();

    void beforeCommit();

    void afterCommit();

    void beforeRollback();

    void afterRollback();

    void beforeClose();

    void afterClose();

    @Override
    default int compareTo(Object o) {
        if (o instanceof ResourceHolder)
            return Integer.compare(getOrder(), ((ResourceHolder) o).getOrder());
        throw new ClassCastException();
    }
}
