package org.naho.pagination;

import java.util.List;

public class PageData<T> {

    private List<T> data;
    private PageMeta pageMeta;

    public PageData() {
    }

    public PageData(List<T> data, PageMeta pageMeta) {
        this.data = data;
        this.pageMeta = pageMeta;
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
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

    public static class Builder<T> {
        private List<T> data;
        private PageMeta pageMeta;

        public Builder<T> data(List<T> data) {
            this.data = data;
            return this;
        }

        public Builder<T> pageMeta(PageMeta pageMeta) {
            this.pageMeta = pageMeta;
            return this;
        }

        public PageData<T> build() {
            return new PageData<>(data, pageMeta);
        }
    }
}