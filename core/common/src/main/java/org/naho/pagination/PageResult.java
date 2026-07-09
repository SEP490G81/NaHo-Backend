package org.naho.pagination;

import java.util.List;

public class PageResult<T> {
    private List<T> data;
    private PageMeta pageMeta;

    public PageResult() {
    }

    public PageResult(List<T> data, PageMeta pageMeta) {
        this.data = data;
        this.pageMeta = pageMeta;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public PageMeta getPageMeta() {
        return pageMeta;
    }

    public void setPageMeta(PageMeta pageMeta) {
        this.pageMeta = pageMeta;
    }
}
