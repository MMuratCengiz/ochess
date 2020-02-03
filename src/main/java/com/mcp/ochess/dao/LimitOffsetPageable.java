package com.mcp.ochess.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class LimitOffsetPageable implements Pageable {
    private int limit;
    private int offset;

    public LimitOffsetPageable(int limit, int offset) {
        this.limit = limit;
        this.offset = offset;
    }

    @Override
    public int getPageNumber() {
        return 0;
    }

    @Override
    public int getPageSize() {
        return limit;
    }

    @Override
    public long getOffset() {
        return offset;
    }

    @Override
    public Sort getSort() {
        return null;
    }

    @Override
    public Pageable next() {
        return null;
    }

    @Override
    public Pageable previousOrFirst() {
        return null;
    }

    @Override
    public Pageable first() {
        return null;
    }

    @Override
    public boolean hasPrevious() {
        return false;
    }
}
