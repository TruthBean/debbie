package com.truthbean.debbie.jdbc.repository;

import com.truthbean.debbie.jdbc.domain.PageRequest;

import java.util.List;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class Page<T> {

    /**
     * previous page
     */
    private int previousPage;

    /**
     * current page
     */
    private int currentPage;

    /**
     * next page
     */
    private long nextPage;

    /**
     * pageSize of elements in one page
     */
    private int pageSize;


    /**
     * this page content
     */
    private List<T> content;

    /**
     * total page
     */
    private long totalPage;

    private int offset;

    /**
     * total element size
     */
    private long totalCount;

    public Page() {
    }

    private Page(int currentPage, int offset, int pageSize,
                 long totalCount, List<T> content,
                 long totalPage, int previousPage, long nextPage) {
        this.currentPage = currentPage;
        this.offset = offset;
        this.pageSize = pageSize;

        this.totalCount = totalCount;
        this.content = content;

        this.totalPage = totalPage;
        this.previousPage = previousPage;
        this.nextPage = nextPage;
    }

    /**
     * @param currentPage 当前页
     * @param pageSize    一页显示条数
     * @param totalCounts 总条数
     * @return Page
     */
    public static <T> Page<T> createPage(int currentPage, int pageSize, long totalCounts, List<T> content) {
        //总页数
        long totalPages = totalPages(pageSize, totalCounts);
        //前一页
        int previousPage = prePage(currentPage);
        //后一页
        long nextPage = nextPage(currentPage, totalPages);
        int offset = getOffset(pageSize, currentPage);

        return new Page<T>(currentPage, offset, pageSize,
                totalCounts, content,
                totalPages, previousPage, nextPage);
    }

    /**
     * @param pageRequest page 信息
     * @param totalCounts 总条数
     * @return Page
     */
    public static <T> Page<T> createPage(PageRequest pageRequest, long totalCounts, List<T> content) {
        int pageSize = pageRequest.getPageSize();
        int currentPage = pageRequest.getCurrentPage();
        //总页数
        long totalPages = totalPages(pageSize, totalCounts);
        //前一页
        int previousPage = prePage(currentPage);
        //后一页
        long nextPage = nextPage(currentPage, totalPages);
        int offset = pageRequest.getOffset();

        return new Page<T>(currentPage, offset, pageSize,
                totalCounts, content,
                totalPages, previousPage, nextPage);
    }

    /**
     * 返回开始查询的地方
     */
    private static int getOffset(int pageSize, int currentPage) {
        return (currentPage - 1) * pageSize;
    }

    private static long nextPage(int currentPage, long totalPages) {
        return (currentPage == totalPages) ? totalPages : (currentPage + 1);
    }

    private static int prePage(int currentPage) {
        return (currentPage == 1) ? 1 : (currentPage - 1);
    }

    private static long totalPages(int pageSize, long totalCounts) {
        if (totalCounts == 0) {
            return 1;
        }
        if (totalCounts % pageSize == 0) {
            return totalCounts / pageSize;
        } else {
            return totalCounts / pageSize + 1;
        }
    }

    public int getPreviousPage() {
        return previousPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public long getNextPage() {
        return nextPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public long getTotalPage() {
        return totalPage;
    }

    public int getOffset() {
        return offset;
    }

    public List<T> getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "{" +
                "\"previousPage\":" + previousPage +
                ",\"currentPage\":" + currentPage +
                ",\"content\":" + content +
                ",\"nextPage\":" + nextPage +
                ",\"totalPage\":" + totalPage +
                ",\"pageSize\":" + pageSize +
                ",\"offset\":" + offset +
                ",\"totalCount\":" + totalCount +
                '}';
    }
}
