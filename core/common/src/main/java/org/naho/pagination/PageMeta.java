package org.naho.pagination;

public record PageMeta(
        Integer currentPage,
        Integer pageSize,
        Integer totalPages,
        Long totalElements,
        Boolean hasNext,
        Boolean hasPrevious
) {

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private Integer currentPage;
        private Integer pageSize;
        private Integer totalPages;
        private Long totalElements;
        private Boolean hasNext;
        private Boolean hasPrevious;

        public Builder currentPage(Integer currentPage) {
            this.currentPage = currentPage;
            return this;
        }

        public Builder pageSize(Integer pageSize) {
            this.pageSize = pageSize;
            return this;
        }

        public Builder totalPages(Integer totalPages) {
            this.totalPages = totalPages;
            return this;
        }

        public Builder totalElements(Long totalElements) {
            this.totalElements = totalElements;
            return this;
        }

        public Builder hasNext(Boolean hasNext) {
            this.hasNext = hasNext;
            return this;
        }

        public Builder hasPrevious(Boolean hasPrevious) {
            this.hasPrevious = hasPrevious;
            return this;
        }

        public PageMeta build() {
            return new PageMeta(
                    currentPage,
                    pageSize,
                    totalPages,
                    totalElements,
                    hasNext,
                    hasPrevious
            );
        }
    }
}