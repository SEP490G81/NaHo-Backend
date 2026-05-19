package org.naho.shared.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PageMeta {
    Integer currentPage;
    Integer pageSize;
    Integer totalPages;
    Long totalElements;

    private PageMeta(Integer currentPage, Integer pageSize, Integer totalPages, Long totalElements) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
    }

    public static PageMeta fromPage(Page<?> page) {
        return new PageMeta(
                page.getNumber(),
                page.getSize(),
                page.getTotalPages(),
                page.getTotalElements()
        );
    }
}
