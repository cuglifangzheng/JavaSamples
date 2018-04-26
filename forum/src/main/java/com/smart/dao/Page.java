package com.smart.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Page implements Serializable {
    private static int DEFAULT_PAGE_SIZE = 20;
    private int pageSize = DEFAULT_PAGE_SIZE;
    private long start;
    private List data;
    private long totalCount;

    public Page() {
        this(0, 0, DEFAULT_PAGE_SIZE, new ArrayList());
    }

    public Page(long start, long totalSize, int pageSize, List data) {
        this.pageSize = pageSize;
        this.start = start;
        this.totalCount = totalSize;
        this.data = data;
    }

    public long getTotlePageCount() {
        if (totalCount % pageSize == 0 ) {
            return totalCount / pageSize;
        } else {
            return totalCount/pageSize + 1;
        }
    }

    public long getCurrentPageNo() {
        return start / pageSize + 1;
    }

    public boolean hasNextPage() {
        return getCurrentPageNo() < getTotlePageCount();
    }

    public boolean hasPreviousPage() {
        return getCurrentPageNo() > 1;
    }

    public static int getStartOfPage(int pageNo, int pageSize) {
        return (pageNo - 1) * pageSize;
    }
}
