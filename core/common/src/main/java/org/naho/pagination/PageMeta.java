package org.naho.pagination;

public record PageMeta(
        Integer currentPage,
        Integer pageSize,
        Integer totalPages,
        Long totalElements
) {

}
