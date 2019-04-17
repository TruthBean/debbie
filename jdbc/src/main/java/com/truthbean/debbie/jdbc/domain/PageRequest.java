package com.truthbean.debbie.jdbc.domain;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class PageRequest implements Serializable {
    private int currentPage;
    private int pageSize;

    public static PageRequest of(int currentPage, int pageSize) {
        return new PageRequest(currentPage, pageSize);
    }

    public static long totalPages(int pageSize, long totalCounts) {
        if (totalCounts == 0L) {
            return 1L;
        }
        if (totalCounts % pageSize == 0L) {
            return totalCounts / pageSize;
        } else {
            return totalCounts / pageSize + 1L;
        }
    }

    private PageRequest(int currentPage, int pageSize) {
        if (currentPage < 1) {
            currentPage = 1;
        }
        this.currentPage = currentPage;
        if (pageSize < 1) {
            pageSize = 1;
        }
        this.pageSize = pageSize;
    }

    public int getOffset() {
        return (currentPage - 1) * pageSize;
    }

    public boolean hasPrevious() {
        return currentPage > 0;
    }

    public PageRequest previousOrFirst() {
        return hasPrevious() ? previous() : first();
    }

    public PageRequest previous() {
        return (currentPage == 0) ? this : new PageRequest(currentPage - 1, pageSize);
    }

    public PageRequest first() {
        return new PageRequest(0, pageSize);
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    @Override
    public String toString() {
        return "{\"currentPage\":" + currentPage + ",\"pageSize\":" + pageSize + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PageRequest)) {
            return false;
        }
        PageRequest that = (PageRequest) o;
        return currentPage == that.currentPage && pageSize == that.pageSize;
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentPage, pageSize);
    }
}
