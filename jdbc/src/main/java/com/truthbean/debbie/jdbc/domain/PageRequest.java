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

    private final Sort sort;

    public static PageRequest of(int currentPage, int pageSize) {
        return new PageRequest(currentPage, pageSize);
    }

    /**
     * Creates a new {@link PageRequest} with sort parameters applied.
     *
     * @param page zero-based page index.
     * @param size the size of the page to be returned.
     * @param sort must not be {@literal null}.
     * @since 0.0.2
     */
    public static PageRequest of(int page, int size, Sort sort) {
        return new PageRequest(page, size, sort);
    }

    /**
     * Creates a new {@link PageRequest} with sort direction and properties applied.
     *
     * @param page       zero-based page index.
     * @param size       the size of the page to be returned.
     * @param direction  must not be {@literal null}.
     * @param properties must not be {@literal null}.
     * @since 0.0.2
     */
    public static PageRequest of(int page, int size, Sort.Direction direction, String... properties) {
        return of(page, size, Sort.by(direction, properties));
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

    /**
     * 分页彩虹算法<br>
     * 来自：https://github.com/iceroot/iceroot/blob/master/src/main/java/com/icexxx/util/IceUtil.java<br>
     * 通过传入的信息，生成一个分页列表显示
     *
     * @param currentPage  当前页
     * @param pageCount    总页数
     * @param displayCount 每屏展示的页数
     * @return 分页条
     */
    public static int[] rainbow(int currentPage, int pageCount, int displayCount) {
        boolean isEven = true;
        isEven = displayCount % 2 == 0;
        int left = displayCount / 2;
        int right = displayCount / 2;

        int length = displayCount;
        if (isEven) {
            right++;
        }
        if (pageCount < displayCount) {
            length = pageCount;
        }
        int[] result = new int[length];
        if (pageCount >= displayCount) {
            if (currentPage <= left) {
                for (int i = 0; i < result.length; i++) {
                    result[i] = i + 1;
                }
            } else if (currentPage > pageCount - right) {
                for (int i = 0; i < result.length; i++) {
                    result[i] = i + pageCount - displayCount + 1;
                }
            } else {
                for (int i = 0; i < result.length; i++) {
                    result[i] = i + currentPage - left + (isEven ? 1 : 0);
                }
            }
        } else {
            for (int i = 0; i < result.length; i++) {
                result[i] = i + 1;
            }
        }
        return result;

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

        this.sort = Sort.unsorted();
    }

    private PageRequest(int currentPage, int pageSize, Sort sort) {
        if (currentPage < 1) {
            currentPage = 1;
        }
        this.currentPage = currentPage;
        if (pageSize < 1) {
            pageSize = 1;
        }
        this.pageSize = pageSize;

        this.sort = sort;
    }

    private PageRequest(int page, int size, Sort.Direction direction, String... properties) {
        this(page, size, Sort.by(direction, properties));
    }

    public Sort getSort() {
        return sort;
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
