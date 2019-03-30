package com.truthbean.code.debbie.jdbc.repository;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class Page {

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
    private int nextPage;

    /**
     * size of elements in one page
     */
    private int size;

    /**
     * total element size
     */
    private int totalElement;

    /**
     * total page size
     */
    private int totalPage;

    private int beginIndex;

    private int lastIndex;

    public Page() {
    }

    public Page(int currentPage, int size, int totalCount, int totalPage,
                int previousPage, int nextPage, int beginPage, int lastPage) {
        this.currentPage = currentPage;
        this.size = size;
        this.totalElement = totalCount;
        this.totalPage = totalPage;
        this.previousPage = previousPage;
        this.nextPage = nextPage;
        this.beginIndex = beginPage;
        this.lastIndex = lastPage;
    }

    /**
     * @param size 一页显示条数
     * @param currentPage 当前页
     * @param totalCounts 总条数
     * @return Page
     */
    public static Page createPage(int size, int currentPage, int totalCounts) {
        //总页数
        int totalPages = totalPages(size, totalCounts);
        //前一页
        int previousPage = prePage(currentPage);
        //后一页
        int nextPage = nextPage(currentPage, totalPages);
        int beginIndex = beginIndex(size, currentPage);
        int lastIndex = lastIndex(beginIndex, size);
        return new Page(currentPage, size, totalCounts, totalPages, previousPage, nextPage, beginIndex, lastIndex);
    }

    /**
     * 停止搜索的地方
     * @param beginIndex sql offset
     * @param size sql limit
     * @return lastIndex
     */
    private static int lastIndex(int beginIndex, int size) {
        return beginIndex + size;
    }

    /**
     * 返回开始查询的地方
     */
    private static int beginIndex(int size, int currentPage) {
        return (currentPage - 1) * size;
    }

    private static int nextPage(int currentPage, int totalPages) {
        return (currentPage == totalPages) ? totalPages : (currentPage + 1);
    }

    private static int prePage(int currentPage) {
        return (currentPage == 1) ? 1 : (currentPage - 1);
    }

    private static int totalPages(int size, int totalcounts) {
        if (totalcounts == 0) {
            return 1;
        }
        if (totalcounts % size == 0) {
            return totalcounts / size;
        } else {
            return totalcounts / size + 1;
        }
    }

    public int getPreviousPage() {
        return previousPage;
    }

    public void setPreviousPage(int previousPage) {
        this.previousPage = previousPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getNextPage() {
        return nextPage;
    }

    public void setNextPage(int nextPage) {
        this.nextPage = nextPage;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getTotalElement() {
        return totalElement;
    }

    public void setTotalElement(int totalElement) {
        this.totalElement = totalElement;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getBeginIndex() {
        return beginIndex;
    }

    public void setBeginIndex(int beginIndex) {
        this.beginIndex = beginIndex;
    }

    public int getLastIndex() {
        return lastIndex;
    }

    public void setLastIndex(int lastIndex) {
        this.lastIndex = lastIndex;
    }
}
